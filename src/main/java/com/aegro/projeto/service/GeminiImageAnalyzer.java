package com.aegro.projeto.service;

// Importa a classe modelo que representa os dados estruturados extraídos da imagem
import com.aegro.projeto.model.RomaneioInfo;

// Importa classes para manipulação de JSON
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

// Importa anotações e classes do Spring
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

// Importa classes de IO e rede
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

// Define a classe como um serviço do Spring, permitindo sua injeção em outros componentes
@Service
public class GeminiImageAnalyzer {

    // Injeta a chave da API do Gemini a partir do application.properties
    @Value("${gemini.api.key}")
    private String apiKey;

    // Método principal que realiza a análise da imagem com base no caminho do arquivo
    public RomaneioInfo analisarImagem(String imagePath) throws IOException, InterruptedException {
        // Lê os bytes da imagem a partir do caminho fornecido
        byte[] imageBytes = Files.readAllBytes(Paths.get(imagePath));
        
        // Converte os bytes da imagem em uma string Base64 para envio via JSON
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        // Define o prompt que será enviado à API Gemini, solicitando extração estruturada de informações
        String prompt = """
            Extraia as seguintes informações deste recibo de carga agrícola:
            - data (formato: dd/MM/yyyy)
            - nome do produtor (caso não tenha, coloque o nome da fazenda)
            - tipo de cultura (retorne SOMENTE o nome da cultura, exemplo: se a cultura for "milho em grãos", retorne apenas "milho".)
            - peso bruto (se estiver em kg, coloque o valor + "kg", se não estiver, converta para kg e adicione "kg" após o valor)
            - umidade (coloque em porcentagem, ex: 12.5%)
            - Impureza (coloque em porcentagem, ex: 1%)
            - peso liquido (se estiver em kg, coloque o valor + "kg", se não estiver, converta para kg e adicione "kg" após o valor)

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

        // Cria um ObjectMapper para serialização e desserialização de JSON
        ObjectMapper mapper = new ObjectMapper();

        // Serializa o prompt como texto JSON escapado
        String escapedPrompt = mapper.writeValueAsString(prompt);

        // Monta o corpo da requisição para a API Gemini usando o modelo Gemini Pro
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

        // Cria o cliente HTTP e a requisição POST
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://generativelanguage.googleapis.com/v1/models/gemini-1.5-pro:generateContent?key=" + apiKey))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        // Envia a requisição e obtém a resposta como string
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        // Lê a resposta como árvore JSON
        JsonNode root = mapper.readTree(response.body());

        // Verifica se a resposta possui candidatos válidos
        if (!root.has("candidates") || !root.get("candidates").isArray() || root.get("candidates").isEmpty()) {
            throw new RuntimeException("Resposta inesperada de Gemini: " + response.body());
        }

        // Navega até o campo de texto com a resposta da IA
        JsonNode textNode = root
            .path("candidates").get(0)
            .path("content").path("parts").get(0)
            .path("text");

        // Extrai o JSON bruto da resposta
        String rawJson = textNode.asText().trim();

        // Remove marcações markdown caso presentes (ex: ```json)
        if (rawJson.startsWith("```")) {
            rawJson = rawJson.replaceAll("(?s)```json\\s*(\\{.*?\\})\\s*```", "$1");
            rawJson = rawJson.replaceAll("(?s)```\\s*(\\{.*?\\})\\s*```", "$1");
        }

        // Converte o JSON limpo para um objeto RomaneioInfo e retorna
        return mapper.readValue(rawJson, RomaneioInfo.class);
    }

    // Sobrecarga auxiliar que analisa uma imagem padrão para facilitar testes
    public RomaneioInfo analisarImagem() throws IOException, InterruptedException {
        return analisarImagem("src/main/resources/imagens/Milho 1.jpeg");
    }
}
