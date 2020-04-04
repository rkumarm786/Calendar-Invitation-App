package com.example.calendly.repository;

import com.example.calendly.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByUserId(long userId);

    Optional<Event> findByUrlAndIsActiveAndIsLive(String url, boolean b, boolean b1);

    @Modifying
    @Query("update Event e set e.isLive = ?2 where e.url = ?1 and e.user.id=?3 and e.isActive=true")
    void updateEventByUrlAndAction(String url, Boolean action, Long userId);
}
