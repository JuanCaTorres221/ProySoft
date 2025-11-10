package com.kairo_emocion.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.kairo_emocion.demo.model.Diary;
import java.time.LocalDate;
import java.util.List;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
    List<Diary> findByUserId(Long userId);
    List<Diary> findByUserIdAndEntryDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
    List<Diary> findByEmotionId(Long emotionId);
    List<Diary> findByEntryDate(LocalDate entryDate);
    List<Diary> findByUserIdAndEmotionId(Long userId, Long emotionId);
    boolean existsByUserIdAndEntryDate(Long userId, LocalDate entryDate);
}