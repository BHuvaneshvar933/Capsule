package com.example.tracker1.repository;

import com.example.tracker1.model.entity.CuratorItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CuratorItemRepository extends MongoRepository<CuratorItem, String> {
    
    List<CuratorItem> findByUserIdOrderByCreatedAtDesc(String userId);

    Page<CuratorItem> findByUserId(String userId, Pageable pageable);
}
