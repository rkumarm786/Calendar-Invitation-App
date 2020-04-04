package com.example.calendly.dto;

import com.example.calendly.utils.EventTypeEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Date;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
public class CustomDateTime {

    @Getter
    @Setter
    @Builder
    @ToString
    public static class Interval{
        private Integer from;
        private Integer to;
    }

    private EventTypeEnum type;
    private List<Interval> intervals;
    private Long timeStamp;
    private String day;
}
