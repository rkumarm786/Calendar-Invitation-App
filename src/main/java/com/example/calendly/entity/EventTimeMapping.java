package com.example.calendly.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.sql.Time;

@Getter
@Setter
@ToString
@Table(name = "event_time_mapping")
@Entity
@NoArgsConstructor
@SuperBuilder
public class EventTimeMapping extends TrackedEntity{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_date_mapping_id", nullable = false)
    private EventDateMapping eventDateMapping;

    @Column(nullable = false)
    private Integer from_time;

    @Column(nullable = false)
    private Integer to_time;

    private boolean isAvailable=true;

    private boolean isActive=true;

}
