package com.aegro.projeto;


import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.json.JSONObject;

@SpringBootApplication
public class GeminiApplication implements CommandLineRunner {

    @Autowired
    private GeminiImageAnalyzer geminiImageAnalyzer;

    public static void main(String[] args) {
        SpringApplication.run(GeminiApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Aqui você pode executar o código para analisar a imagem
        String response = geminiImageAnalyzer.analisarImagem();
        System.out.println("Resposta do Gemini: ");
        // System.out.println(resposta);

		JSONObject json = new JSONObject(response);

        String textoExtraido = json
            .getJSONArray("candidates")
            .getJSONObject(0)
            .getJSONObject("content")
            .getJSONArray("parts")
            .getJSONObject(0)
            .getString("text");

        System.out.println("Texto extraído do Gemini:");
        System.out.println(textoExtraido);
    }
}