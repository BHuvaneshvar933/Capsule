package com.example.tracker1.repository;

import com.example.tracker1.model.entity.Pomodoro;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PomodoroRepository extends MongoRepository<Pomodoro, String> {
    List<Pomodoro> findByUserIdOrderByCompletedAtDesc(String userId);
}
