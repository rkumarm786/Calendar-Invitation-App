package com.example.calendly.repository;

import com.example.calendly.entity.EventTimeMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventTimeMappingRepository extends JpaRepository<EventTimeMapping, Long> {
}
