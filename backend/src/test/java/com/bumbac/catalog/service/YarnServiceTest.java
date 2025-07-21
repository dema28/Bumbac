package com.bumbac.catalog.service;

import com.bumbac.catalog.dto.YarnResponse;
import com.bumbac.catalog.entity.Brand;
import com.bumbac.catalog.entity.Category;
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

  private Category sampleCategory(String name) {
    return Category.builder().id(1L).name(name).build();
  }

  private Brand sampleBrand(String name) {
    return Brand.builder().id(1L).name(name).build();
  }

  private Yarn sampleYarn(Category category, Brand brand, String material) {
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
    Category category = sampleCategory("cotton");
    Brand brand = sampleBrand("brandX");
    Yarn yarn = sampleYarn(category, brand, "wool");

    when(yarnRepository.findAll(any(Specification.class))).thenReturn(List.of(yarn));
    when(yarnMapper.toResponse(any(Yarn.class))).thenReturn(
            YarnResponse.builder()
                    .id(yarn.getId())
                    .category(yarn.getCategory().getName())
                    .brand(yarn.getBrand().getName())
                    .material(yarn.getMaterial())
                    .build());

    try (MockedStatic<YarnSpecification> specMock = mockStatic(YarnSpecification.class)) {
      Specification<Yarn> spec = mock(Specification.class);
      specMock.when(() -> YarnSpecification.hasCategory("cotton")).thenReturn(spec);

      List<YarnResponse> result = yarnService.filter("cotton", null, null);

      specMock.verify(() -> YarnSpecification.hasCategory("cotton"));
      verify(yarnRepository).findAll(any(Specification.class));
      assertEquals(1, result.size());
      YarnResponse resp = result.get(0);
      assertEquals(yarn.getId(), resp.getId());
      assertEquals("cotton", resp.getCategory());
    }
  }

  @Test
  void filterByBrandDelegatesToSpecification() {
    Category category = sampleCategory("cat");
    Brand brand = sampleBrand("brandY");
    Yarn yarn = sampleYarn(category, brand, "linen");

    when(yarnRepository.findAll(any(Specification.class))).thenReturn(List.of(yarn));
    when(yarnMapper.toResponse(any(Yarn.class))).thenReturn(
            YarnResponse.builder()
                    .id(yarn.getId())
                    .category(yarn.getCategory().getName())
                    .brand(yarn.getBrand().getName())
                    .material(yarn.getMaterial())
                    .build());

    try (MockedStatic<YarnSpecification> specMock = mockStatic(YarnSpecification.class)) {
      Specification<Yarn> spec = mock(Specification.class);
      specMock.when(() -> YarnSpecification.hasBrand("brandY")).thenReturn(spec);

      List<YarnResponse> result = yarnService.filter(null, "brandY", null);

      specMock.verify(() -> YarnSpecification.hasBrand("brandY"));
      verify(yarnRepository).findAll(any(Specification.class));
      assertEquals(1, result.size());
      YarnResponse resp = result.get(0);
      assertEquals("brandY", resp.getBrand());
    }
  }

  @Test
  void filterByMaterialDelegatesToSpecification() {
    Category category = sampleCategory("lux");
    Brand brand = sampleBrand("brandZ");
    Yarn yarn = sampleYarn(category, brand, "acrylic");

    when(yarnRepository.findAll(any(Specification.class))).thenReturn(List.of(yarn));
    when(yarnMapper.toResponse(any(Yarn.class))).thenReturn(
            YarnResponse.builder()
                    .id(yarn.getId())
                    .category(yarn.getCategory().getName())
                    .brand(yarn.getBrand().getName())
                    .material(yarn.getMaterial())
                    .build());

    try (MockedStatic<YarnSpecification> specMock = mockStatic(YarnSpecification.class)) {
      Specification<Yarn> spec = mock(Specification.class);
      specMock.when(() -> YarnSpecification.hasMaterial("acrylic")).thenReturn(spec);

      List<YarnResponse> result = yarnService.filter(null, null, "acrylic");

      specMock.verify(() -> YarnSpecification.hasMaterial("acrylic"));
      verify(yarnRepository).findAll(any(Specification.class));
      assertEquals(1, result.size());
      YarnResponse resp = result.get(0);
      assertEquals("acrylic", resp.getMaterial());
    }
  }
}
