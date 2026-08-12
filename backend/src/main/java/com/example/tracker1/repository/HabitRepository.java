package com.example.tracker1.repository;

import com.example.tracker1.model.entity.Habit;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HabitRepository extends MongoRepository<Habit, String> {
    List<Habit> findByUserIdOrderByOrderAscCreatedAtAsc(String userId);
}
