package com.bumbac.modules.media.repository;

import com.bumbac.modules.media.entity.MediaAsset;
import com.bumbac.modules.media.entity.MediaEntityType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MediaAssetRepository extends JpaRepository<MediaAsset, Long> {
    List<MediaAsset> findByEntityTypeAndEntityIdOrderBySortOrder(
            MediaEntityType entityType, Long entityId);
}
