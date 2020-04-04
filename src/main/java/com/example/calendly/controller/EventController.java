package com.example.calendly.controller;

import com.example.calendly.dto.EventDto;
import com.example.calendly.service.EventService;
import com.example.calendly.utils.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Controller
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private SessionUtil sessionUtil;

    @RequestMapping("/")
    String loginPage() {
        return "login";
    }

    @RequestMapping("/home")
    ModelAndView home(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        ModelMap modelMap = new ModelMap();
        modelMap.put("events", eventService.getEvents(sessionUtil.getLoggedIn(request)));
        modelAndView.setViewName("home");
        modelAndView.addAllObjects(modelMap);
        return modelAndView;
    }

    @PostMapping("/action/event")
    @ResponseBody
    void activateEvent(HttpServletRequest request, @RequestBody Map<String, String> data) {
        if (StringUtils.isEmpty(data.get("url")) || StringUtils.isEmpty(data.get("action"))) {
            throw new RuntimeException("Please provide valid parameters");
        }
        eventService.eventUpdate(sessionUtil.getLoggedIn(request), data.get("url"), Boolean.valueOf(data.get("action")));
    }

    @PostMapping("/save/event")
    @ResponseBody
    EventDto saveEvent(@Valid @RequestBody EventDto eventDto, HttpServletRequest request) {
        return eventService.saveEvent(sessionUtil.getLoggedIn(request), eventDto);
    }

    @GetMapping("/events")
    @ResponseBody
    List<EventDto> getEvents(HttpServletRequest request) {
        return eventService.getEvents(sessionUtil.getLoggedIn(request));
    }

}
