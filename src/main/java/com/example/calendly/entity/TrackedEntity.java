package com.example.calendly.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

@Getter
@Setter
@ToString
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@SuperBuilder
public abstract class TrackedEntity extends Identifiable {
    @CreatedDate
    protected Long creationDate;

    @LastModifiedDate
    protected Long lastModifiedDate;

    @PrePersist
    protected void onCreate() {
        creationDate = System.currentTimeMillis();
    }

    @PreUpdate
    protected void onUpdate() {
        lastModifiedDate = System.currentTimeMillis();
    }
}