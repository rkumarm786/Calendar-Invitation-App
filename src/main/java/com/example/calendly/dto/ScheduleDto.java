package com.example.calendly.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@ToString
public class ScheduleDto {
    private String title;
    private String description;
    private Long fromDate;
    private Long toDate;
    private Integer interval;
    private Map<Long, List<CustomDateTime.Interval>> customDate;
    private Map<String, List<CustomDateTime.Interval>> customDay;
    private Map<Long, List<CustomDateTime.Interval>> unavailableTimeSlots;
}
