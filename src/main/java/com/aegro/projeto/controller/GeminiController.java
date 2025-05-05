package com.aegro.projeto.controller;

import com.aegro.projeto.model.ReciboCargaInfo;
import com.aegro.projeto.service.ExcelExporter;
import com.aegro.projeto.service.GeminiImageAnalyzer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/recibo")  
public class GeminiController {

    @Autowired
    private GeminiImageAnalyzer geminiImageAnalyzer;

    @Autowired
    private ExcelExporter excelExporter;

    // temporary storage for extracted data
    private final Map<String, List<ReciboCargaInfo>> pendingData = new ConcurrentHashMap<>();

    static class UploadResponse {
        public List<ReciboCargaInfo> data;
        public String dataId;

        public UploadResponse(List<ReciboCargaInfo> data, String dataId) {
            this.data = data;
            this.dataId = dataId;
        }
    }

    static class ExcelResponse {
        public String excelId;

        public ExcelResponse(String excelId) {
            this.excelId = excelId;
        }
    }

    // STEP 1: Upload and extract, store result temporarily
    @PostMapping("/upload")
    public UploadResponse processarMultiplasImagens(@RequestParam("imagem") MultipartFile[] imagens) throws IOException, InterruptedException {
        List<ReciboCargaInfo> resultados = new ArrayList<>();

        if (imagens == null || imagens.length == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nenhuma imagem foi enviada!");
        }

        for (MultipartFile imagem : imagens) {
            File tempFile = File.createTempFile("upload-", ".jpeg");
            imagem.transferTo(tempFile);

            ReciboCargaInfo info = geminiImageAnalyzer.analisarImagem(tempFile.getAbsolutePath());
            resultados.add(info);

            if (!tempFile.delete()) {
                System.err.println("Falha ao deletar o arquivo temporário: " + tempFile.getAbsolutePath());
            }
        }

        String dataId = UUID.randomUUID().toString();
        pendingData.put(dataId, resultados);

        return new UploadResponse(resultados, dataId);
    }

    // STEP 2: Receive confirmed/corrected data and generate Excel
    @PostMapping("/review/{dataId}")
    public ResponseEntity<ExcelResponse> confirmarDadosCorrigidos(@PathVariable String dataId, @RequestBody List<ReciboCargaInfo> dadosCorrigidos) throws IOException {
        if (!pendingData.containsKey(dataId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        pendingData.remove(dataId); // cleanup

        String excelId = excelExporter.exportToExcel(dadosCorrigidos);
        return ResponseEntity.ok(new ExcelResponse(excelId));
    }

    // STEP 3: Download the generated Excel
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
