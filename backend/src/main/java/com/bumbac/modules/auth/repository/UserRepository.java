package com.bumbac.modules.auth.repository;

import com.bumbac.modules.auth.entity.User;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

  @EntityGraph(attributePaths = "roles")
  @Cacheable(value = "users", key = "#email")
  Optional<User> findByEmail(String email);

  @Cacheable(value = "userExists", key = "#email")
  boolean existsByEmail(String email);

  @Cacheable(value = "phoneExists", key = "#phone")
  boolean existsByPhone(String phone);

  @Override
  @CacheEvict(value = { "users", "userExists", "phoneExists" }, allEntries = true)
  <S extends User> S save(S entity);

  @Override
  @CacheEvict(value = { "users", "userExists", "phoneExists" }, allEntries = true)
  void delete(User entity);

  @Override
  @CacheEvict(value = { "users", "userExists", "phoneExists" }, allEntries = true)
  void deleteById(Long id);
}
