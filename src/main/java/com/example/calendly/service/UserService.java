package com.example.calendly.service;

import com.example.calendly.entity.User;
import com.example.calendly.repository.UserRepository;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private static Gson gson = new Gson();

    public User registerUser(String email, String token){
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            String token1 = user.getToken();
            Map map = gson.fromJson(token, Map.class);
            Map map1 = gson.fromJson(token1, Map.class);
            map.put("refresh_token",map1.get("refresh_token"));
            user.setToken(gson.toJson(map));
            return userRepository.save(user);
        }
        return userRepository.save(User.builder().token(token).email(email).isActive(true).build());
    }

    public void updateUserToken(String updateTokenString, String email) {
        userRepository.updateToken(updateTokenString, email);
    }
}
