package com.bumbac.catalog.service;

import com.bumbac.catalog.dto.YarnResponse;
import com.bumbac.catalog.entity.Yarn;
import com.bumbac.catalog.mapper.YarnMapper;
import com.bumbac.catalog.repository.YarnRepository;
import com.bumbac.catalog.specification.YarnSpecification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class YarnServiceTest {

  @Mock
  private YarnRepository yarnRepository;

  @Mock
  private YarnMapper yarnMapper;

  @InjectMocks
  private YarnService yarnService;

  private Yarn sampleYarn(String category, String brand, String material) {
    return Yarn.builder()
        .id(1L)
        .name("test")
        .category(category)
        .brand(brand)
        .material(material)
        .weight(50.0)
        .length(100.0)
        .priceMDL(10.0)
        .priceUSD(5.0)
        .build();
  }

  @Test
  void filterByCategoryDelegatesToSpecification() {
    String category = "cat";
    Yarn yarn = sampleYarn(category, "brand", "mat");
    when(yarnRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class)))
        .thenReturn(List.of(yarn));
    when(yarnMapper.toResponse(any(Yarn.class))).thenReturn(
        YarnResponse.builder()
            .id(yarn.getId())
            .category(yarn.getCategory())
            .brand(yarn.getBrand())
            .material(yarn.getMaterial())
            .build());

    try (MockedStatic<YarnSpecification> specMock = mockStatic(YarnSpecification.class)) {
      Specification<Yarn> spec = mock(Specification.class);
      specMock.when(() -> YarnSpecification.hasCategory(category)).thenReturn(spec);

      List<YarnResponse> result = yarnService.filter(category, null, null);

      specMock.verify(() -> YarnSpecification.hasCategory(category));
      specMock.verifyNoMoreInteractions();
      verify(yarnRepository).findAll(any(org.springframework.data.jpa.domain.Specification.class));
      assertEquals(1, result.size());
      YarnResponse resp = result.get(0);
      assertEquals(yarn.getId(), resp.getId());
      assertEquals(yarn.getCategory(), resp.getCategory());
      assertEquals(yarn.getBrand(), resp.getBrand());
      assertEquals(yarn.getMaterial(), resp.getMaterial());
    }
  }

  @Test
  void filterByBrandDelegatesToSpecification() {
    String brand = "brand";
    Yarn yarn = sampleYarn("cat", brand, "mat");
    when(yarnRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class)))
        .thenReturn(List.of(yarn));
    when(yarnMapper.toResponse(any(Yarn.class))).thenReturn(
        YarnResponse.builder()
            .id(yarn.getId())
            .category(yarn.getCategory())
            .brand(yarn.getBrand())
            .material(yarn.getMaterial())
            .build());

    try (MockedStatic<YarnSpecification> specMock = mockStatic(YarnSpecification.class)) {
      Specification<Yarn> spec = mock(Specification.class);
      specMock.when(() -> YarnSpecification.hasBrand(brand)).thenReturn(spec);

      List<YarnResponse> result = yarnService.filter(null, brand, null);

      specMock.verify(() -> YarnSpecification.hasBrand(brand));
      specMock.verifyNoMoreInteractions();
      verify(yarnRepository).findAll(any(org.springframework.data.jpa.domain.Specification.class));
      assertEquals(1, result.size());
      YarnResponse resp = result.get(0);
      assertEquals(yarn.getId(), resp.getId());
      assertEquals(yarn.getBrand(), resp.getBrand());
    }
  }

  @Test
  void filterByMaterialDelegatesToSpecification() {
    String material = "mat";
    Yarn yarn = sampleYarn("cat", "brand", material);
    when(yarnRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class)))
        .thenReturn(List.of(yarn));
    when(yarnMapper.toResponse(any(Yarn.class))).thenReturn(
        YarnResponse.builder()
            .id(yarn.getId())
            .category(yarn.getCategory())
            .brand(yarn.getBrand())
            .material(yarn.getMaterial())
            .build());

    try (MockedStatic<YarnSpecification> specMock = mockStatic(YarnSpecification.class)) {
      Specification<Yarn> spec = mock(Specification.class);
      specMock.when(() -> YarnSpecification.hasMaterial(material)).thenReturn(spec);

      List<YarnResponse> result = yarnService.filter(null, null, material);

      specMock.verify(() -> YarnSpecification.hasMaterial(material));
      specMock.verifyNoMoreInteractions();
      verify(yarnRepository).findAll(any(org.springframework.data.jpa.domain.Specification.class));
      assertEquals(1, result.size());
      YarnResponse resp = result.get(0);
      assertEquals(yarn.getId(), resp.getId());
      assertEquals(yarn.getMaterial(), resp.getMaterial());
    }
  }
}
