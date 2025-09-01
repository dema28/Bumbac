package com.bumbac.modules.media.service;

import com.bumbac.modules.media.dto.MediaAssetDTO;
import com.bumbac.modules.media.dto.UploadMediaRequest;
import com.bumbac.modules.media.entity.MediaEntityType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MediaAssetService {

  /**
   * Получить медиафайлы для сущности
   */
  List<MediaAssetDTO> getMediaForEntity(MediaEntityType type, Long entityId);

  /**
   * Загрузить медиафайл
   */
  MediaAssetDTO uploadFile(MultipartFile file, UploadMediaRequest request);

  /**
   * Удалить медиафайл
   */
  void deleteMedia(Long id);
}
