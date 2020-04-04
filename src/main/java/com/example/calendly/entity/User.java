package com.example.calendly.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;
import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@ToString
@Table(name = "users")
@Entity
@NoArgsConstructor
@SuperBuilder
public class User extends TrackedEntity {
    private String firstName;
    private String lastName;

    @Column(nullable = false)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String token;

    private boolean isActive = true;

    @OneToMany(mappedBy="user", cascade=CascadeType.PERSIST)
    private List<Event> events;
}
