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

  @JsonIgnore
  @ManyToMany(mappedBy = "roles")
  private Set<User> users;
}
