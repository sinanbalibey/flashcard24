package com.FlashCardgai.FlashCard.controller;


import com.FlashCardgai.FlashCard.entites.Flashcard;
import com.FlashCardgai.FlashCard.repos.FlashcardRepository;
import com.FlashCardgai.FlashCard.service.GoogleGeminiService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flashcards")
public class FlashcardController {

    private final FlashcardRepository flashcardRepository;
    private final GoogleGeminiService googleGeminiService;

    public FlashcardController(FlashcardRepository flashcardRepository, GoogleGeminiService googleGeminiService) {
        this.flashcardRepository = flashcardRepository;
        this.googleGeminiService = googleGeminiService;
    }

    @PostMapping("/create")
    public Flashcard createFlashcard(@RequestBody Flashcard flashcard) {
        // Call Gemini API to get example sentence and set it
        String exampleSentence = googleGeminiService.generateContent(flashcard.getQuestion());
        flashcard.setExampleSentence(exampleSentence);

        // Save to database
        return flashcardRepository.save(flashcard);
    }

    @GetMapping
    public List<Flashcard> getFlashcards() {
        return flashcardRepository.findAll();
    }

    @DeleteMapping("/delete")
    public void deleteAllFlashcards() {
        flashcardRepository.deleteAll();
    }
}
