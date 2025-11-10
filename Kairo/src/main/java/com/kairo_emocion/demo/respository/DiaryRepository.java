package com.kairo_emocion.demo.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.kairo_emocion.demo.model.Diary;

public interface DiaryRepository extends JpaRepository<Diary, Long> { }
