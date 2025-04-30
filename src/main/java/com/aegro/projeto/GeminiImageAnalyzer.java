package com.aegro.projeto;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GeminiImageAnalyzer {

    // Injetando a chave da API do application.properties
    @Value("${gemini.api.key}")
    private String apiKey;

    private static final String IMAGE_PATH = "imagens/teste_imagem.png";


    public String analisarImagem() throws IOException, InterruptedException {
        // Lê e converte a imagem para Base64
        byte[] imageBytes = Files.readAllBytes(Paths.get(IMAGE_PATH));
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        // Corpo JSON da requisição
        String requestBody = """
            {
              "contents": [
                {
                  "parts": [
                    { "text": "Extraia informações como data, tipo de cultura, peso bruto, peso liquido, umidade, impurezas e peso liquido final da imagem." },
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
        """.formatted(base64Image);

        // Cria o cliente e a requisição
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://generativelanguage.googleapis.com/v1/models/gemini-1.5-pro:generateContent?key=" + apiKey))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        // Envia a requisição e imprime a resposta
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }
}