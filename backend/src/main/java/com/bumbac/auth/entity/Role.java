package com.bumbac.auth.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String code; // Пример: "USER", "ADMIN", "CONTENT_MANAGER"

  private String name; // Пример: "Пользователь", "Администратор"

  @ManyToMany(mappedBy = "roles")
  @JsonIgnore
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private transient Set<User> users;

}
