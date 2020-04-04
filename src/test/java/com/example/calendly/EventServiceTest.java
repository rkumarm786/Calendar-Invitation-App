package com.example.calendly;

import com.example.calendly.controller.EventController;
import com.example.calendly.dto.UserDto;
import com.example.calendly.entity.Event;
import com.example.calendly.entity.User;
import com.example.calendly.repository.EventRepository;
import com.example.calendly.repository.UserRepository;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.AssertEquals.assertEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class EventService {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    @Test
    public void testGetUserByEmail(){
        UserDto userDto = UserDto.builder().id(1L).email("rkumarm786@gmail.com").build();
        List<Event> byUserId = eventRepository.findByUserId(userDto.getId());
        when(eventRepository.findByUserId(userDto.getId())).thenReturn(byUserId);
        assertEquals(eventService., user.getId().longValue());
    }

}
