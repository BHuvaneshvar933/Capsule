package com.example.tracker1.repository;

import com.example.tracker1.model.entity.HabitLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HabitLogRepository extends MongoRepository<HabitLog, String> {
    Optional<HabitLog> findByUserIdAndDayKey(String userId, String dayKey);
    List<HabitLog> findByUserIdAndDayKeyIn(String userId, List<String> dayKeys);
    void deleteByUserId(String userId);
}
