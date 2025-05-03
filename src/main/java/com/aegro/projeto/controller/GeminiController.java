package com.aegro.projeto.controller;

import com.aegro.projeto.model.ReciboCargaInfo;
import com.aegro.projeto.service.ExcelExporter;
import com.aegro.projeto.service.GeminiImageAnalyzer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/recibo")
public class GeminiController {

    @Autowired
    private GeminiImageAnalyzer geminiImageAnalyzer;

    @Autowired
    private ExcelExporter excelExporter;

    // para um unico arquivo 
    // testado! 
    @PostMapping("/upload")
    public ReciboCargaInfo processarImagem(@RequestParam("imagem") MultipartFile imagem) throws IOException, InterruptedException {
        File tempFile = File.createTempFile("upload-", ".jpeg");
        imagem.transferTo(tempFile);

        ReciboCargaInfo info = geminiImageAnalyzer.analisarImagem(tempFile.getAbsolutePath());
        excelExporter.exportToExcel(info);

        tempFile.delete(); // ou mantenha se quiser inspecionar

        return info;
    }

    // não testado!
    // para multiplos arquivos
    @PostMapping("/upload-multiplos")
    public List<ReciboCargaInfo> processarMultiplasImagens(@RequestParam("imagem") MultipartFile[] imagens) throws IOException, InterruptedException {
        List<ReciboCargaInfo> resultados = new ArrayList<>();

        for (MultipartFile imagem : imagens) {
            File tempFile = File.createTempFile("upload-", ".jpeg");
            imagem.transferTo(tempFile);

            ReciboCargaInfo info = geminiImageAnalyzer.analisarImagem(tempFile.getAbsolutePath());
            excelExporter.exportToExcel(info);  // Se quiser gerar vários arquivos

            resultados.add(info);
            tempFile.delete();
        }

        return resultados;
    }

}

