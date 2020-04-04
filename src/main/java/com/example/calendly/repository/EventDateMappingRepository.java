package com.example.calendly.repository;

import com.example.calendly.entity.EventDateMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventDateMappingRepository extends JpaRepository<EventDateMapping, Long> {
}
