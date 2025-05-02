package com.aegro.projeto;

import com.aegro.projeto.model.ReciboCargaInfo;
import com.aegro.projeto.service.ExcelExporter;
import com.aegro.projeto.service.GeminiImageAnalyzer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GeminiApplication implements CommandLineRunner {

    @Autowired
    private GeminiImageAnalyzer geminiImageAnalyzer;

    @Autowired
    private ExcelExporter excelExporter;

    public static void main(String[] args) {
        SpringApplication.run(GeminiApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        ReciboCargaInfo info = geminiImageAnalyzer.analisarImagem();
        excelExporter.exportToExcel(info);
        System.out.println("Extração e exportação concluídas.");
    }
}
