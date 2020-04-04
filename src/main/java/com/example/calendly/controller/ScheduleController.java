package com.example.calendly.controller;

import com.example.calendly.dto.ScheduleCreationDto;
import com.example.calendly.dto.ScheduleDto;
import com.example.calendly.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
public class ScheduleController {

    @Autowired
    private EventService eventService;

    @GetMapping("/schedule-event")
    public String scheduleEventPage(@RequestParam(name = "key", required = true) String key) {
        return "schedule";
    }

    @GetMapping("/schedule-data")
    @ResponseBody
    public ScheduleDto scheduleEvent(@RequestParam(name = "key", required = true) String key) {
        return eventService.getEventByUrl(key);
    }

    @PostMapping("/schedule-meeting")
    @ResponseBody
    public void createSchedule(@Valid @RequestBody ScheduleCreationDto scheduleCreationDto) {
        eventService.scheduleMeeting(scheduleCreationDto);
    }
}
