package com.FlashCardgai.FlashCard.repos;

import com.FlashCardgai.FlashCard.entites.Flashcard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlashcardRepository extends JpaRepository<Flashcard, Long> {
}
