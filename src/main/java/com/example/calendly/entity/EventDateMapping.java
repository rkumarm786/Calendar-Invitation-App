package com.example.calendly.entity;

import com.example.calendly.utils.EventTypeEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.util.List;

@Getter
@Setter
@ToString
@Table(name = "event_date_mapping")
@Entity
@NoArgsConstructor
@SuperBuilder
public class EventDateMapping extends TrackedEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(nullable = false)
    private EventTypeEnum type;

    private Long timeStamp;

    private DayOfWeek day;

    private boolean isActive = true;

    @OneToMany(mappedBy = "eventDateMapping", cascade = CascadeType.PERSIST)
    private List<EventTimeMapping> eventTimeMappings;
}
