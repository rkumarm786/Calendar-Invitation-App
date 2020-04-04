package com.example.calendly.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.sql.Time;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
@Table(name = "event_schedule")
@Entity
@NoArgsConstructor
@SuperBuilder
public class EventSchedule extends TrackedEntity{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(nullable = false)
    private Long date;

    @Column(nullable = false)
    private Integer from_time;

    @Column(nullable = false)
    private Integer to_time;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    private String message;

    private String timeZone;

    private boolean isActive = true;
}
