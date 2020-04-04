package com.example.calendly.service;

import com.example.calendly.dto.*;
import com.example.calendly.entity.*;
import com.example.calendly.repository.*;
import com.example.calendly.utils.EventTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventScheduleRepository eventScheduleRepository;

    @Autowired
    private EventTimeMappingRepository eventTimeMappingRepository;

    @Autowired
    private EventDateMappingRepository eventDateMappingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CalenderService calenderService;

    public EventDto saveEvent(UserDto userDto, EventDto eventDto) {
        Event event = Event.builder().description(eventDto.getDescription())
                .title(eventDto.getTitle())
                .timeZone(null)
                .interval(eventDto.getInterval())
                .url(UUID.randomUUID().toString())
                .isLive(eventDto.isLive())
                .isActive(true)
                .fromDate(eventDto.getFromDate())
                .toDate(eventDto.getToDate())
                .user(User.builder().id(userDto.getId()).build())
                .build();

        if (CollectionUtils.isEmpty(eventDto.getCustomDateTime())) {
            List<EventDateMapping> defaultDateMappings = getDefaultDateMappings(event);
            event.setEventDateMappings(defaultDateMappings);
        } else {
            List<CustomDateTime> customDateTimeList = eventDto.getCustomDateTime();
            List<EventDateMapping> eventDateMappings = customDateTimeList.stream().map(customDateTime -> {
                EventDateMapping eventDateMap = EventDateMapping.builder()
                        .day(EventTypeEnum.DAY.equals(customDateTime.getType()) ? DayOfWeek.valueOf(customDateTime.getDay()) : null)
                        .event(event)
                        .isActive(true)
                        .timeStamp(EventTypeEnum.DATE.equals(customDateTime.getType()) ? customDateTime.getTimeStamp() : null)
                        .type(customDateTime.getType())
                        .build();

                List<EventTimeMapping> eventTimeMappings = customDateTime.getIntervals().stream().map(eventTimeMap -> EventTimeMapping.builder().eventDateMapping(eventDateMap).isAvailable(true).isActive(true).from_time(eventTimeMap.getFrom()).to_time(eventTimeMap.getTo()).build()).collect(Collectors.toList());
                eventDateMap.setEventTimeMappings(eventTimeMappings);
                return eventDateMap;
            }).collect(Collectors.toList());
            event.setEventDateMappings(eventDateMappings);
        }

        Event savedEvent = eventRepository.save(event);
        return eventDtoBuilder(savedEvent);
    }

    private List<EventDateMapping> getDefaultDateMappings(Event event) {
        List<EventDateMapping> eventDateMappings = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            EventDateMapping eventDateMapping = EventDateMapping.builder()
                    .day(DayOfWeek.of(i))
                    .event(event)
                    .isActive(true)
                    .type(EventTypeEnum.DAY)
                    .build();
            EventTimeMapping eventTimeMapping = EventTimeMapping.builder()
                    .eventDateMapping(eventDateMapping)
                    .from_time(9 * 60)
                    .to_time(17 * 60)
                    .isActive(true)
                    .isAvailable(true)
                    .build();
            eventDateMapping.setEventTimeMappings(Collections.singletonList(eventTimeMapping));
            eventDateMappings.add(eventDateMapping);
        }
        return eventDateMappings;
    }

    public List<EventDto> getEvents(UserDto userDto) {
        List<Event> events = eventRepository.findByUserId(userDto.getId());

        if (CollectionUtils.isEmpty(events)) {
            return new ArrayList<>();
        }

        events.sort(new Comparator<Event>() {
            @Override
            public int compare(Event o1, Event o2) {
                if (o1.getLastModifiedDate() == null && o2.getLastModifiedDate() == null) {
                    return o2.getCreationDate().compareTo(o1.getCreationDate());
                }
                if (o2.getLastModifiedDate() == null) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });

        return events.stream().map(this::eventDtoBuilder).collect(Collectors.toList());
    }

    private EventDto eventDtoBuilder(Event event) {
        return EventDto.builder()
                .title(event.getTitle())
                .description(event.getDescription())
                .fromDate(event.getFromDate())
                .toDate(event.getToDate())
                .interval(event.getInterval())
                .url(event.getUrl())
                .live(event.isLive())
                .isActive(event.isActive())
                .customDateTime(event.getEventDateMappings().stream().map(eventDateMapping -> CustomDateTime.builder().day(eventDateMapping.getDay().name()).timeStamp(eventDateMapping.getTimeStamp()).type(eventDateMapping.getType())
                        .intervals(eventDateMapping.getEventTimeMappings().stream().map(eventTimeMapping -> CustomDateTime.Interval.builder().from(eventTimeMapping.getFrom_time()).to(eventTimeMapping.getTo_time()).build()).collect(Collectors.toList())).build()).collect(Collectors.toList()))
                .build();
    }


    public ScheduleDto getEventByUrl(String url) {
        Optional<Event> OptionalEvent = eventRepository.findByUrlAndIsActiveAndIsLive(url, true, true);
        if (!OptionalEvent.isPresent()) {
            throw new RuntimeException("No event found for given url");
        }

        Event event = OptionalEvent.get();

        Map<String, List<CustomDateTime.Interval>> customAvailabilityDay = event.getEventDateMappings().isEmpty() ? new HashMap<>() : event.getEventDateMappings().stream().filter(eventDateMapping -> eventDateMapping.getType().equals(EventTypeEnum.DAY)).collect(Collectors.toMap(e -> e.getDay().name(), e -> e.getEventTimeMappings().stream().filter(EventTimeMapping::isAvailable).map(t -> {
            return CustomDateTime.Interval.builder().from(t.getFrom_time()).to(t.getTo_time()).build();
        }).collect(Collectors.toList())));

        Map<Long, List<CustomDateTime.Interval>> customAvailabilityDate = event.getEventDateMappings().isEmpty() ? new HashMap<>() : event.getEventDateMappings().stream().filter(eventDateMapping -> eventDateMapping.getType().equals(EventTypeEnum.DATE)).collect(Collectors.toMap(EventDateMapping::getTimeStamp, e -> e.getEventTimeMappings().stream().filter(EventTimeMapping::isAvailable).map(t -> {
            return CustomDateTime.Interval.builder().from(t.getFrom_time()).to(t.getTo_time()).build();
        }).collect(Collectors.toList())));

        List<EventSchedule> eventSchedule = eventScheduleRepository.findByEventIdAndIsActive(event.getId(), true);

        Map<Long, List<CustomDateTime.Interval>> unavailableTimeSlots = new HashMap<>();

        if (!CollectionUtils.isEmpty(eventSchedule)) {
            for (EventSchedule schedule : eventSchedule) {
                if (!unavailableTimeSlots.containsKey(schedule.getDate())) {
                    unavailableTimeSlots.put(schedule.getDate(), new ArrayList<>());
                }
                unavailableTimeSlots.get(schedule.getDate()).add(CustomDateTime.Interval.builder().from(schedule.getFrom_time()).to(schedule.getTo_time()).build());
            }
        }

        return ScheduleDto.builder()
                .title(event.getTitle())
                .description(event.getDescription())
                .fromDate(event.getFromDate())
                .toDate(event.getToDate())
                .interval(event.getInterval())
                .unavailableTimeSlots(unavailableTimeSlots)
                .customDate(customAvailabilityDate)
                .customDay(customAvailabilityDay)
                .build();
    }

    @Transactional(propagation = Propagation.NESTED)
    public EventSchedule scheduleMeeting(ScheduleCreationDto scheduleCreationDto) {
        Optional<Event> optionalEvent = eventRepository.findByUrlAndIsActiveAndIsLive(scheduleCreationDto.getUrl(), true, true);

        if (!optionalEvent.isPresent()) {
            throw new RuntimeException("No active event found for provided data");
        }

        Event event = optionalEvent.get();

        if (!((scheduleCreationDto.getDate() + 86400000 >= event.getFromDate()) && (scheduleCreationDto.getFromTime() < scheduleCreationDto.getToTime()))) {
            throw new RuntimeException("Please provide valid date and time for meeting");
        }

        //validate schedule data
        validateScheduleCreationDto(scheduleCreationDto, event.getEventDateMappings(), event.getInterval());

        EventSchedule eventSchedule = EventSchedule.builder()
                .from_time(scheduleCreationDto.getFromTime())
                .to_time(scheduleCreationDto.getToTime())
                .date(scheduleCreationDto.getDate())
                .email(scheduleCreationDto.getEmail())
                .event(event)
                .name(scheduleCreationDto.getName())
                .isActive(true)
                .build();

        event.setActive(false);
        event.setLive(false);

        eventRepository.save(event);
        EventSchedule savedEventSchedule = eventScheduleRepository.save(eventSchedule);
        calenderService.scheduleCalenderEvent(createGoogleCalenderDto(event, scheduleCreationDto), event.getUser().getToken());
        return savedEventSchedule;
    }

    private GoogleCalenderDto createGoogleCalenderDto(Event event, ScheduleCreationDto scheduleCreationDto) {
        List<String> attendees = new ArrayList<>();
        attendees.add(event.getUser().getEmail());
        attendees.add(scheduleCreationDto.getEmail());

        return GoogleCalenderDto.builder().summary(event.getTitle())
                .description(event.getDescription())
                .startTime(scheduleCreationDto.getDate() + scheduleCreationDto.getFromTime() * 60000)
                .endTime(scheduleCreationDto.getDate() + scheduleCreationDto.getToTime() * 60000)
                .location("")
                .timeZone("Asia/Kolkata")
                .attendees(attendees)
                .build();
    }

    private void validateScheduleCreationDto(ScheduleCreationDto scheduleCreationDto, List<EventDateMapping> eventDateMappings, Integer interval) {
        Long date = scheduleCreationDto.getDate();
        DayOfWeek dayOfWeek = DayOfWeek.valueOf((new SimpleDateFormat("EEEE")).format(date).toUpperCase());

        EventDateMapping eventDayMap = null;
        EventDateMapping eventDateMap = null;

        for (EventDateMapping eventDateMapping : eventDateMappings) {
            if (eventDateMapping.getDay().equals(dayOfWeek)) {
                eventDayMap = eventDateMapping;
            } else if (eventDateMapping.getDay().equals(scheduleCreationDto.getDate())) {
                eventDateMap = eventDateMapping;
            }
        }

        //if custom day is available
        boolean isDateTimeValid = false;
        if (eventDayMap != null) {
            isDateTimeValid = validateDateAndTimeSlot(scheduleCreationDto, interval, eventDayMap);
        }
        if (eventDateMap != null) {
            isDateTimeValid = validateDateAndTimeSlot(scheduleCreationDto, interval, eventDateMap);
        }

        if (!isDateTimeValid) {
            throw new RuntimeException("Date or Time slots are not available");
        }
    }

    private boolean validateDateAndTimeSlot(ScheduleCreationDto scheduleCreationDto, Integer interval, EventDateMapping eventDayMap) {
        boolean isValidTime = true;
        boolean isValidDate = true;
        List<EventTimeMapping> eventTimeMappings = eventDayMap.getEventTimeMappings();
        if (!eventTimeMappings.isEmpty()) {
            Set<Integer> timeSlot = new HashSet<>();
            for (EventTimeMapping eventTimeMapping : eventTimeMappings) {
                timeSlot.add(eventTimeMapping.getFrom_time());
                int start = eventTimeMapping.getFrom_time() + interval;
                while (start < eventTimeMapping.getTo_time()) {
                    timeSlot.add(start);
                    start += interval;
                }
            }
            if (!timeSlot.contains(scheduleCreationDto.getFromTime()) || !timeSlot.contains(scheduleCreationDto.getToTime())) {
                isValidTime = false;
            }
        } else {
            isValidDate = false;
        }
        return isValidDate && isValidTime;
    }

    @Transactional
    public void eventUpdate(UserDto userDto, String url, Boolean action) {
        eventRepository.updateEventByUrlAndAction(url, action, userDto.getId());
    }
}
