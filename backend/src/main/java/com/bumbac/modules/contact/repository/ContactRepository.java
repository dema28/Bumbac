package com.bumbac.modules.contact.repository;

import com.bumbac.modules.contact.entity.ContactMessage;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<ContactMessage, Long> {

  // Поиск по email
  @Cacheable(value = "contact_messages", key = "'email_' + #email")
  List<ContactMessage> findByEmailIgnoreCase(String email);

  // Поиск по теме (частичное совпадение)
  @Cacheable(value = "contact_messages", key = "'subject_' + #subject")
  List<ContactMessage> findBySubjectContainingIgnoreCase(String subject);

  // Поиск непрочитанных сообщений
  @Cacheable(value = "contact_messages", key = "'unread'")
  List<ContactMessage> findByIsReadFalse();

  // Поиск по периоду дат
  @Cacheable(value = "contact_messages", key = "'period_' + #from + '_' + #to")
  @Query("SELECT cm FROM ContactMessage cm WHERE cm.createdAt BETWEEN :from AND :to ORDER BY cm.createdAt DESC")
  List<ContactMessage> findByCreatedAtBetween(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

  // Поиск по email и периоду дат
  @Cacheable(value = "contact_messages", key = "'email_period_' + #email + '_' + #from + '_' + #to")
  @Query("SELECT cm FROM ContactMessage cm WHERE cm.email = :email AND cm.createdAt BETWEEN :from AND :to ORDER BY cm.createdAt DESC")
  List<ContactMessage> findByEmailAndCreatedAtBetween(@Param("email") String email, @Param("from") LocalDateTime from,
      @Param("to") LocalDateTime to);

  // Подсчет непрочитанных сообщений
  @Cacheable(value = "contact_messages", key = "'count_unread'")
  long countByIsReadFalse();

  // Подсчет сообщений по email
  @Cacheable(value = "contact_messages", key = "'count_email_' + #email")
  long countByEmailIgnoreCase(String email);

  // Получение с оптимизированными запросами
  @Cacheable(value = "contact_messages", key = "#root.methodName")
  @Query("SELECT cm FROM ContactMessage cm ORDER BY cm.createdAt DESC")
  List<ContactMessage> findAllOrderByCreatedAtDesc();

  @Override
  @Cacheable(value = "contact_messages", key = "#id")
  Optional<ContactMessage> findById(Long id);

  // Инвалидация кэша при изменениях
  @Override
  @CacheEvict(value = { "contact_messages" }, allEntries = true)
  <S extends ContactMessage> S save(S entity);

  @Override
  @CacheEvict(value = { "contact_messages" }, allEntries = true)
  <S extends ContactMessage> List<S> saveAll(Iterable<S> entities);

  @Override
  @CacheEvict(value = { "contact_messages" }, allEntries = true)
  void deleteById(Long id);

  @Override
  @CacheEvict(value = { "contact_messages" }, allEntries = true)
  void delete(ContactMessage entity);

  @Override
  @CacheEvict(value = { "contact_messages" }, allEntries = true)
  void deleteAllById(Iterable<? extends Long> ids);
}
