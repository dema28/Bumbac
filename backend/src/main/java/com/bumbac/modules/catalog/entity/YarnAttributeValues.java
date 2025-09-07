package com.bumbac.modules.catalog.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "yarn_attribute_values", indexes = {
    @Index(name = "idx_yarn_attribute_values_yarn_id", columnList = "yarn_id"),
    @Index(name = "idx_yarn_attribute_values_attribute_id", columnList = "attribute_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class YarnAttributeValues {

  @EmbeddedId
  private YarnAttributeValuesId id;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("yarnId")
  @JoinColumn(name = "yarn_id", nullable = false)
  private Yarn yarn;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("attributeId")
  @JoinColumn(name = "attribute_id", nullable = false)
  private Attribute attribute;

  @Column(name = "value_text")
  private String valueText;

  @Column(name = "value_number", precision = 12, scale = 4)
  private BigDecimal valueNumber;

  @Column(name = "value_bool")
  private Boolean valueBool;

  @Column(name = "value_enum", length = 128)
  private String valueEnum;

  // Utility method to get the actual value regardless of type
  public Object getValue() {
    if (valueText != null)
      return valueText;
    if (valueNumber != null)
      return valueNumber;
    if (valueBool != null)
      return valueBool;
    if (valueEnum != null)
      return valueEnum;
    return null;
  }

  // Utility method to set value based on type
  public void setValue(Object value) {
    // Reset all values first
    valueText = null;
    valueNumber = null;
    valueBool = null;
    valueEnum = null;

    if (value == null)
      return;

    if (value instanceof String) {
      valueText = (String) value;
    } else if (value instanceof Number) {
      valueNumber = BigDecimal.valueOf(((Number) value).doubleValue());
    } else if (value instanceof Boolean) {
      valueBool = (Boolean) value;
    } else {
      valueText = value.toString();
    }
  }
}