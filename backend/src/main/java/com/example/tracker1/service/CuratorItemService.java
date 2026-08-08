package com.example.tracker1.service;

import com.example.tracker1.exception.ResourceNotFoundException;
import com.example.tracker1.model.dto.CuratorItemRequest;
import com.example.tracker1.model.dto.CuratorItemResponse;
import com.example.tracker1.model.entity.CuratorItem;
import com.example.tracker1.model.entity.User;
import com.example.tracker1.repository.CuratorItemRepository;
import com.example.tracker1.repository.UserRepository;
import com.example.tracker1.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CuratorItemService {

    private final CuratorItemRepository curatorItemRepository;
    private final UserRepository userRepository;
    private User getCurrentUser() {
        String email = SecurityUtil.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public List<CuratorItemResponse> getAllForCurrentUser() {
        String userId = getCurrentUser().getId();
        return curatorItemRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public CuratorItemResponse createItem(CuratorItemRequest request) {
        String userId = getCurrentUser().getId();
        Instant now = Instant.now();

        CuratorItem item = CuratorItem.builder()
                .userId(userId)
                .url(request.getUrl())
                .type(request.getType())
                .title(request.getTitle())
                .description(request.getDescription())
                .tags(request.getTags())
                .favorite(request.isFavorite())
                .createdAt(now)
                .updatedAt(now)
                .build();

        CuratorItem saved = curatorItemRepository.save(item);
        return mapToResponse(saved);
    }

    public CuratorItemResponse updateItem(String id, CuratorItemRequest request) {
        String userId = getCurrentUser().getId();
        
        CuratorItem item = curatorItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curator item not found"));

        if (!item.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Curator item not found");
        }

        item.setUrl(request.getUrl());
        item.setType(request.getType());
        item.setTitle(request.getTitle());
        item.setDescription(request.getDescription());
        item.setTags(request.getTags());
        item.setFavorite(request.isFavorite());
        item.setUpdatedAt(Instant.now());

        CuratorItem saved = curatorItemRepository.save(item);
        return mapToResponse(saved);
    }

    public void deleteItem(String id) {
        String userId = getCurrentUser().getId();
        
        CuratorItem item = curatorItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curator item not found"));

        if (!item.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Curator item not found");
        }

        curatorItemRepository.delete(item);
    }

    public CuratorItemResponse toggleFavorite(String id) {
        String userId = getCurrentUser().getId();
        
        CuratorItem item = curatorItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curator item not found"));

        if (!item.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Curator item not found");
        }

        item.setFavorite(!item.isFavorite());
        item.setUpdatedAt(Instant.now());

        CuratorItem saved = curatorItemRepository.save(item);
        return mapToResponse(saved);
    }

    private CuratorItemResponse mapToResponse(CuratorItem item) {
        return CuratorItemResponse.builder()
                .id(item.getId())
                .url(item.getUrl())
                .type(item.getType())
                .title(item.getTitle())
                .description(item.getDescription())
                .tags(item.getTags())
                .favorite(item.isFavorite())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }
}
