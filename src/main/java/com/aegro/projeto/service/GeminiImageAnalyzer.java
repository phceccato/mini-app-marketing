package com.aegro.projeto.service;

import com.aegro.projeto.model.ReciboCargaInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

@Service
public class GeminiImageAnalyzer {

    @Value("${gemini.api.key}")
    private String apiKey;

    private static final String IMAGE_PATH = "src/main/resources/imagens/Milho 1.jpeg";

    public ReciboCargaInfo analisarImagem() throws IOException, InterruptedException {
        byte[] imageBytes = Files.readAllBytes(Paths.get(IMAGE_PATH));
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            String prompt = """
                Extraia as seguintes informações deste recibo de carga agrícola:
                - peso
                - umidade
                - tipo de carga
                - folhagem
                - nome do motorista

                Responda em JSON com os seguintes campos:
                {
                  "peso": "...",
                  "umidade": "...",
                  "tipoCarga": "...",
                  "folhagem": "...",
                  "nomeMotorista": "..."
                }
                """;


            ObjectMapper mapper = new ObjectMapper();
            String escapedPrompt = mapper.writeValueAsString(prompt);  // adds quotes and escapes line breaks
            
            String requestBody = """
                {
                  "contents": [
                    {
                      "parts": [
                        { "text": %s },
                        {
                          "inlineData": {
                            "mimeType": "image/jpeg",
                            "data": "%s"
                          }
                        }
                      ]
                    }
                  ]
                }
            """.formatted(escapedPrompt, base64Image);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://generativelanguage.googleapis.com/v1/models/gemini-1.5-pro:generateContent?key=" + apiKey))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Extract the actual JSON from the Gemini response

        JsonNode root = mapper.readTree(response.body());
        
        if (!root.has("candidates") || !root.get("candidates").isArray() || root.get("candidates").isEmpty()) {
            throw new RuntimeException("Resposta inesperada de Gemini: " + response.body());
        }
        
        JsonNode textNode = root
            .path("candidates").get(0)
            .path("content").path("parts").get(0)
            .path("text");
        
        String rawJson = textNode.asText().trim();
        
        // Remove o markdown do retorno do gemini pois isso quebra a biblioteca jackson
        if (rawJson.startsWith("```")) {
            rawJson = rawJson.replaceAll("(?s)```json\\s*(\\{.*?\\})\\s*```", "$1");
            rawJson = rawJson.replaceAll("(?s)```\\s*(\\{.*?\\})\\s*```", "$1");
        }
        
        System.out.println("Conteúdo limpo extraído:");
        System.out.println(rawJson);
        
        return mapper.readValue(rawJson, ReciboCargaInfo.class);
        

    }
}
