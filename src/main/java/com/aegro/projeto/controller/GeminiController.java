package com.aegro.projeto.controller;

import com.aegro.projeto.model.ReciboCargaInfo;
import com.aegro.projeto.service.ExcelExporter;
import com.aegro.projeto.service.GeminiImageAnalyzer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@RestController
@RequestMapping("/api/recibo")
public class GeminiController {

    @Autowired
    private GeminiImageAnalyzer geminiImageAnalyzer;

    @Autowired
    private ExcelExporter excelExporter;

    static class UploadResponse {
        public List<ReciboCargaInfo> data;
        public String excelId;

        public UploadResponse(List<ReciboCargaInfo> data, String excelId) {
            this.data = data;
            this.excelId = excelId;
        }
    }

    @PostMapping("/upload")
    public UploadResponse processarMultiplasImagens(@RequestParam("imagem") MultipartFile[] imagens) throws IOException, InterruptedException {
        List<ReciboCargaInfo> resultados = new ArrayList<>();

        for (MultipartFile imagem : imagens) {
            File tempFile = File.createTempFile("upload-", ".jpeg");
            imagem.transferTo(tempFile);

            ReciboCargaInfo info = geminiImageAnalyzer.analisarImagem(tempFile.getAbsolutePath());
            resultados.add(info);
            tempFile.delete();
        }

        String excelId = excelExporter.exportToExcel(resultados);
        return new UploadResponse(resultados, excelId);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<InputStreamResource> downloadExcel(@PathVariable String id) throws IOException {
        File excelFile = excelExporter.getExcelFile(id);

        if (excelFile == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        InputStreamResource resource = new InputStreamResource(new FileInputStream(excelFile));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + excelFile.getName())
                .contentLength(excelFile.length())
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(resource);
    }
}
