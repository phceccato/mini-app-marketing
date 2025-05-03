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

    // Método principal com caminho da imagem como parâmetro
    public ReciboCargaInfo analisarImagem(String imagePath) throws IOException, InterruptedException {
        byte[] imageBytes = Files.readAllBytes(Paths.get(imagePath));
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        String prompt = """
            Extraia as seguintes informações deste recibo de carga agrícola:
            - data (formato: dd/MM/yyyy)
            - nome do produtor (caso não tenha, coloque o nome da fazenda)
            - tipo de cultura (exemplo: milho, soja, etc.)
            - peso bruto em kg
            - umidade
            - Impureza
            - peso liquido em kg

            Responda em JSON com os seguintes campos:
            {
              "data": "...", 
              "nomeProdutor": "...",
              "tipoCultura": "...",
              "pesoBruto": "...",
              "umidade": "...",
              "impureza": "...",
              "pesoLiquido": "..."
            }
            """;

        ObjectMapper mapper = new ObjectMapper();
        String escapedPrompt = mapper.writeValueAsString(prompt);

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
        JsonNode root = mapper.readTree(response.body());

        if (!root.has("candidates") || !root.get("candidates").isArray() || root.get("candidates").isEmpty()) {
            throw new RuntimeException("Resposta inesperada de Gemini: " + response.body());
        }

        JsonNode textNode = root
            .path("candidates").get(0)
            .path("content").path("parts").get(0)
            .path("text");

        String rawJson = textNode.asText().trim();

        if (rawJson.startsWith("```")) {
            rawJson = rawJson.replaceAll("(?s)```json\\s*(\\{.*?\\})\\s*```", "$1");
            rawJson = rawJson.replaceAll("(?s)```\\s*(\\{.*?\\})\\s*```", "$1");
        }

        return mapper.readValue(rawJson, ReciboCargaInfo.class);
    }

    // Método auxiliar opcional (mantido para compatibilidade)
    public ReciboCargaInfo analisarImagem() throws IOException, InterruptedException {
        return analisarImagem("src/main/resources/imagens/Milho 1.jpeg");
    }
}
