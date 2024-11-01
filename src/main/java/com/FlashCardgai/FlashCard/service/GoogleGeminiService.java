package com.FlashCardgai.FlashCard.service;


import com.FlashCardgai.FlashCard.excep.InvalidApiKeyException;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

@Service
public class GoogleGeminiService {

    private final RestTemplate restTemplate;
    private final String apiKey;

    @Autowired
    public GoogleGeminiService(RestTemplate restTemplate, @Value("${google.api.key}") String apiKey) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
    }

    public String generateContent(String word) {

        try {
            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=" + apiKey;

            // Başlıkları ayarlayın
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            // JSON isteğini oluşturun, kullanıcının kelimesini ekleyin
            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> contentMap = new HashMap<>();
            Map<String, Object> partsMap = new HashMap<>();
            partsMap.put("text", "Create an English sentence about '" + word + "' in no more than 10 words and provide a Turkish translation.");
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

            // Yanıtı kontrol edin ve ayrıştırın
            String responseBody = response.getBody();
            //System.out.println("Response Body: " + responseBody);  // Yanıtı incelemek için konsola yazdırın

            JSONObject jsonResponse = new JSONObject(responseBody);

            // "candidates" alanını kontrol edin
            if (!jsonResponse.has("candidates") || jsonResponse.getJSONArray("candidates").isEmpty()) {
                throw new JSONException("The response does not contain candidates.");
            }

            JSONArray candidates = jsonResponse.getJSONArray("candidates");
            JSONObject firstCandidate = candidates.getJSONObject(0);

            // "content" alanını kontrol edin
            if (!firstCandidate.has("content")) {
                throw new JSONException("The response does not contain the 'content' field.");
            }

            String resultText = firstCandidate
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text");

            // Belirtilen metinleri değiştir ve `*` karakterlerini kaldır
            resultText = resultText
                    .replace("**English Sentence:**", "İngilizce:")
                    .replace("**Turkish Translation:**", "Türkçe:")
                    .replace("*", "")
                    .replace(":", "")
                    .replace("Turkish Translation:", "")
                    .replace("Turkish translation:", "")
                    .replace("Turkish translation ", "")
                    .replace("Turkish", "")
                    .replace("Turkish:", "")
                    .replace("Türkçe", "")
                    .replace("English:", "")
                    .replace("English", "");

            return resultText;
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new InvalidApiKeyException("Invalid API Key. Please check and try again.");
            }
            throw e;

        }
    }

}
