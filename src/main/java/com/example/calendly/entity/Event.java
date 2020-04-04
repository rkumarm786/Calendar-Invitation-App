package com.example.calendly.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.List;
import java.util.TimeZone;

@Getter
@Setter
@ToString
@Table(name = "event")
@Entity
@NoArgsConstructor
@SuperBuilder
public class Event extends TrackedEntity{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private Integer interval;

    @Column(nullable = false)
    private Long fromDate;

    @Column(nullable = false)
    private Long toDate;

    private String timeZone;

    private boolean isActive=true;

    private boolean isLive=false;

    @OneToMany(mappedBy="event", cascade=CascadeType.PERSIST)
    private List<EventDateMapping> eventDateMappings;
}
