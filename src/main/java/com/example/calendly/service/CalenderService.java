package com.example.calendly.service;

import com.example.calendly.dto.GoogleCalenderDto;
import com.example.calendly.dto.UserDto;
import com.example.calendly.entity.User;
import com.example.calendly.utils.SessionUtil;
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class CalenderService {

    private static final String APPLICATION_NAME = "";
    private static HttpTransport httpTransport;
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    @Autowired
    private SessionUtil sessionUtil;

    @Autowired
    private Gson gson;

    @Autowired
    private UserService userService;

    @Value("${google.client.client-id}")
    private String clientId;

    @Value("${google.client.client-secret}")
    private String clientSecret;

    @Value("${google.client.redirectUri}")
    private String redirectURI;

    private GoogleAuthorizationCodeFlow createAuthorizationCodeFlow() throws Exception {
        GoogleClientSecrets.Details web = new GoogleClientSecrets.Details();
        web.setClientId(clientId);
        web.setClientSecret(clientSecret);
        GoogleClientSecrets clientSecrets = new GoogleClientSecrets().setWeb(web);
        httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        return new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets,
                Collections.singleton(CalendarScopes.CALENDAR)).build();
    }

    public String authorize() throws Exception {
        GoogleAuthorizationCodeFlow flow = createAuthorizationCodeFlow();
        AuthorizationCodeRequestUrl authorizationUrl = flow.newAuthorizationUrl().setRedirectUri(redirectURI);
        authorizationUrl.setScopes(Arrays.asList("profile", "email", "https://www.googleapis.com/auth/calendar"));
        authorizationUrl.set("access_type", "offline");
        System.out.println("cal authorizationUrl->" + authorizationUrl);
        return authorizationUrl.build();
    }

    public void loginCallback(HttpServletRequest request, String code) {
        try {
            GoogleAuthorizationCodeFlow flow = createAuthorizationCodeFlow();
            TokenResponse response = flow.newTokenRequest(code).setRedirectUri(redirectURI).execute();
            Credential credential = flow.createAndStoreCredential(response, "userID");
//            client = new com.google.api.services.calendar.Calendar.Builder(httpTransport, JSON_FACTORY, credential)
//                    .setApplicationName(APPLICATION_NAME).build();

            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory()).setAudience(Collections.singletonList(clientId)).build();
            GoogleIdToken idToken = verifier.verify(((GoogleTokenResponse) response).getIdToken());

            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                String email = payload.getEmail();
                boolean emailVerified = payload.getEmailVerified();
                if (emailVerified){
                    User user = userService.registerUser(email, gson.toJson(response));
                    sessionUtil.initializeSession(request, gson.toJson(UserDto.builder().id(user.getId()).email(email).token(user.getToken()).build()));
                }else {
                    throw new RuntimeException("email not verified");
                }
            } else {
                throw new RuntimeException("invalid token");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private Event createGoogleCalenderEvent(GoogleCalenderDto calenderDto) {
        Event event = new Event()
                .setSummary(calenderDto.getSummary())
                .setDescription(calenderDto.getDescription());

        DateTime startDateTime = new DateTime(calenderDto.getStartTime());
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone(calenderDto.getTimeZone());
        event.setStart(start);

        DateTime endDateTime = new DateTime(calenderDto.getEndTime());
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone(calenderDto.getTimeZone());
        event.setEnd(end);

        EventAttendee[] attendees = new EventAttendee[calenderDto.getAttendees().size()];
        List<String> attendeesList = calenderDto.getAttendees();
        for (int i = 0; i < attendeesList.size(); i++) {
            attendees[i] = new EventAttendee().setEmail(attendeesList.get(i));
        }
        event.setAttendees(Arrays.asList(attendees));

        EventReminder[] reminderOverrides = new EventReminder[]{
                new EventReminder().setMethod("email").setMinutes(24 * 60),
                new EventReminder().setMethod("popup").setMinutes(10),
        };
        Event.Reminders reminders = new Event.Reminders()
                .setUseDefault(false)
                .setOverrides(Arrays.asList(reminderOverrides));
        event.setReminders(reminders);
        return event;
    }

    private String getUpdatedToken(String tokenString) {
        Map<String,String> map = gson.fromJson(tokenString, Map.class);

        MultiValueMap<String, String> postData = new LinkedMultiValueMap<String, String>();

        postData.add("client_id", clientId);
        postData.add("client_secret", clientSecret);
        postData.add("refresh_token", map.get("refresh_token"));
        postData.add("grant_type", "refresh_token");

        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String url = "https://oauth2.googleapis.com/token";
        ResponseEntity<String> stringResponseEntity = restTemplate.postForEntity(url, new HttpEntity<>(postData, headers), String.class);
        if(stringResponseEntity.getStatusCode().equals(HttpStatus.OK)){
            Map newTokenStringMap = gson.fromJson(stringResponseEntity.getBody(), Map.class);
            map.put("access_token",newTokenStringMap.get("access_token").toString());
            map.put("id_token",newTokenStringMap.get("id_token").toString());
            String updateTokenString = gson.toJson(map);
            return updateTokenString;
        }
        throw new RuntimeException("exception while requesting token");
    }

    public void scheduleCalenderEvent(GoogleCalenderDto googleCalenderDto, String tokenString) {
        try {
            Calendar client = getCalendarClient(tokenString);
            Event event = createGoogleCalenderEvent(googleCalenderDto);
            String calendarId = "primary";
            try {
                client.events().insert(calendarId, event).execute();
            }catch (GoogleJsonResponseException ex){
                System.out.println("google auth token expired for dto "+googleCalenderDto.toString());
                if(ex.getStatusCode()==401){
                    String updatedToken = getUpdatedToken(tokenString);
                    userService.updateUserToken(updatedToken, googleCalenderDto.getOrganizer());
                    client = getCalendarClient(updatedToken);
                    client.events().insert(calendarId, event).execute();
                }
            }
            System.out.println(event);
        }catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("error while sending calender invitation");
        }
    }

    private Calendar getCalendarClient(String tokenString) throws Exception {
        Map<String,String> map = gson.fromJson(tokenString, Map.class);
        TokenResponse tokenResponse = new TokenResponse()
                .setAccessToken(map.get("access_token"))
                .setScope(map.get("scope"))
                .setTokenType(map.get("token_type"));
        Credential credential = createAuthorizationCodeFlow().createAndStoreCredential(tokenResponse, "userID");
        return new Calendar.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME).build();
    }

    private void refreshAccessToken(TokenResponse tokenResponse) {

    }
}