package com.kairo_emocion.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.time.LocalDate;
import com.kairo_emocion.demo.repository.DiaryRepository;
import com.kairo_emocion.demo.model.Diary;
import com.kairo_emocion.demo.exception.ResourceNotFoundException;

@Service
@Transactional
public class DiaryService {

    private final DiaryRepository repo;

    @Autowired
    public DiaryService(DiaryRepository repo) {
        this.repo = repo;
    }

    public Diary createDiaryEntry(Diary diary) {
        if (diary.getUser() == null) {
            throw new IllegalArgumentException("La entrada del diario debe estar asociada a un usuario");
        }
        if (diary.getEmotion() == null) {
            throw new IllegalArgumentException("La entrada del diario debe tener una emoción asociada");
        }
        if (diary.getEntryDate() == null) {
            throw new IllegalArgumentException("La fecha de la entrada no puede estar vacía");
        }

        return repo.save(diary);
    }

    public List<Diary> findAll() {
        return repo.findAll();
    }

    public Diary findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entrada del diario no encontrada: " + id));
    }

    public Diary updateDiaryEntry(Long id, Diary diaryData) {
        Diary diary = findById(id);
        diary.setUser(diaryData.getUser());
        diary.setEmotion(diaryData.getEmotion());
        diary.setNotes(diaryData.getNotes());
        diary.setEntryDate(diaryData.getEntryDate());
        return repo.save(diary);
    }

    public Diary save(Diary diary) {
        return repo.save(diary);
    }

    public void deleteById(Long id) {
        Diary diary = findById(id);
        repo.delete(diary);
    }

    public boolean isEmotionUsed(Long emotionId) {
        List<Diary> entries = repo.findByEmotionId(emotionId);
        return !entries.isEmpty();
    }

    public List<Diary> findByUserId(Long userId) {
        return repo.findByUserId(userId);
    }

    public List<Diary> findByUserAndDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return repo.findByUserIdAndEntryDateBetween(userId, startDate, endDate);
    }

    public List<Diary> findByEmotionId(Long emotionId) {
        return repo.findByEmotionId(emotionId);
    }

    public List<Diary> findByEntryDate(LocalDate entryDate) {
        return repo.findByEntryDate(entryDate);
    }

    public List<Diary> findByUserIdAndEmotionId(Long userId, Long emotionId) {
        return repo.findByUserIdAndEmotionId(userId, emotionId);
    }

    public boolean existsByUserIdAndEntryDate(Long userId, LocalDate entryDate) {
        return repo.existsByUserIdAndEntryDate(userId, entryDate);
    }
}
