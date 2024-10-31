package com.FlashCardgai.FlashCard.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

@Service
public class GoogleGeminiService {

    private final RestTemplate restTemplate;
    private final String apiKey;

    public GoogleGeminiService(RestTemplate restTemplate, @Value("${google.api.key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
    }

    public String generateContent(String word) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=" + apiKey;

        // Başlıkları ayarlayın
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        // JSON isteğini oluşturun, kullanıcının kelimesini ekleyin
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, Object> contentMap = new HashMap<>();
        Map<String, Object> partsMap = new HashMap<>();
        partsMap.put("text", "Create an English sentence about '" + word + "' and provide a Turkish translation.");
        contentMap.put("parts", new Map[]{partsMap});
        requestBody.put("contents", new Map[]{contentMap});

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        // İsteği gönderin ve yanıtı alın
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        // Yanıtı ayrıştır ve yalnızca metin içeriğini al
        String responseBody = response.getBody();
        JSONObject jsonResponse = new JSONObject(responseBody);
        JSONArray candidates = jsonResponse.getJSONArray("candidates");
        String resultText = candidates.getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text");

        // **English Sentence:** ve **Turkish Translation:** metinlerini kaldır ve yeniden düzenle
        resultText = resultText
                .replace("**English Sentence:**", "İngilizce:")
                .replace("**Turkish Translation:**", "Türkçe:");

        return resultText;
    }

}
