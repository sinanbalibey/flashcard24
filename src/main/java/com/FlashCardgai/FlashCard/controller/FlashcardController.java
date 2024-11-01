package com.FlashCardgai.FlashCard.controller;


import com.FlashCardgai.FlashCard.entites.Flashcard;
import com.FlashCardgai.FlashCard.excep.InvalidApiKeyException;
import com.FlashCardgai.FlashCard.repos.FlashcardRepository;
import com.FlashCardgai.FlashCard.service.GoogleGeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flashcards")
public class FlashcardController {


    private final FlashcardRepository flashcardRepository;
    private final GoogleGeminiService googleGeminiService;

    @Autowired
    public FlashcardController(FlashcardRepository flashcardRepository, GoogleGeminiService googleGeminiService) {
        this.flashcardRepository = flashcardRepository;
        this.googleGeminiService = googleGeminiService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createFlashcard(@RequestBody Flashcard flashcard) {
        try {
            String exampleSentence = googleGeminiService.generateContent(flashcard.getQuestion());
            flashcard.setExampleSentence(exampleSentence);
            Flashcard savedFlashcard = flashcardRepository.save(flashcard);
            return ResponseEntity.ok(savedFlashcard);  // Return the saved flashcard as the response body
        } catch (InvalidApiKeyException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("API key is invalid. Please check your API key.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while creating the flashcard.");
        }
    }

    @PostMapping("/content")
    public ResponseEntity<String> generateContent(@RequestParam String word){
        String result= googleGeminiService.generateContent(word);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public List<Flashcard> getFlashcards() {
        return flashcardRepository.findAll();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteFlashcardById(@PathVariable Long id) {
        if (flashcardRepository.existsById(id)) {
            flashcardRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/delete")
    public void deleteAllFlashcards() {
        flashcardRepository.deleteAll();
    }
}
