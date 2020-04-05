package com.example.calendly.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
public class GoogleCalenderDto {
    private String summary;
    private String location;
    private String description;
    private Long startTime;
    private Long endTime;
    private String timeZone;
    private List<String> attendees;
    private String organizer;
}
