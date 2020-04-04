package com.example.calendly;

import com.example.calendly.dto.UserDto;
import com.example.calendly.entity.Event;
import com.example.calendly.repository.EventRepository;
import com.example.calendly.service.EventService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class EventServiceTest {
    @Autowired
    private MockMvc mockMvc;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    @Test
    public void testEventsForUser() {
        UserDto userDto = UserDto.builder().id(1L).email("rkumarm786@gmail.com").build();
        List<Event> byUserId = eventRepository.findByUserId(userDto.getId());
        when(eventRepository.findByUserId(1L)).thenReturn(byUserId);
        assertEquals(eventService.getEvents(userDto).size(), byUserId.size());
    }

    @Test
    public void testEventByUrl() throws RuntimeException {
        UserDto userDto = UserDto.builder().id(1L).email("rkumarm786@gmail.com").build();
        when(eventRepository.findByUrlAndIsActiveAndIsLive("", true, true)).thenReturn(null);
        assertThrows(NullPointerException.class, () -> eventService.getEventByUrl(""));
    }

}
