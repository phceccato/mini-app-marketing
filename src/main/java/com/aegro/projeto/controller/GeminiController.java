package com.aegro.projeto.controller;

import com.aegro.projeto.model.RomaneioInfo;
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

// Permite requisições de uma origem específica (frontend Angular rodando localmente)
@CrossOrigin(origins = "http://localhost:4200")
@RestController // Define a classe como um controller REST do Spring
@RequestMapping("/api/recibo")  // Prefixo para todas as rotas deste controller
public class GeminiController {

    @Autowired // Injeta automaticamente a dependência do serviço de análise de imagem
    private GeminiImageAnalyzer geminiImageAnalyzer;

    @Autowired // Injeta automaticamente a dependência do serviço de exportação para Excel
    private ExcelExporter excelExporter;

    // Armazena temporariamente os dados extraídos (ID único)
    private final Map<String, List<RomaneioInfo>> pendingData = new ConcurrentHashMap<>();

    // Classe auxiliar para resposta do upload
    static class UploadResponse {
        public List<RomaneioInfo> data;
        public String dataId;

        public UploadResponse(List<RomaneioInfo> data, String dataId) {
            this.data = data;
            this.dataId = dataId;
        }
    }

    // Classe auxiliar para resposta com ID do Excel
    static class ExcelResponse {
        public String excelId;

        public ExcelResponse(String excelId) {
            this.excelId = excelId;
        }
    }

    // Etapa 1: Upload de imagens, análise e armazenamento temporário dos dados extraídos
    @PostMapping("/upload")
    public UploadResponse processarMultiplasImagens(@RequestParam("imagem") MultipartFile[] imagens) throws IOException, InterruptedException {
        List<RomaneioInfo> resultados = new ArrayList<>();

        // Validação: garante que imagens foram enviadas
        if (imagens == null || imagens.length == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nenhuma imagem foi enviada!");
        }

        // Processa cada imagem individualmente
        for (MultipartFile imagem : imagens) {
            File tempFile = File.createTempFile("upload-", ".jpeg");
            imagem.transferTo(tempFile); 

            RomaneioInfo info = geminiImageAnalyzer.analisarImagem(tempFile.getAbsolutePath());
            resultados.add(info); 

            if (!tempFile.delete()) {
                System.err.println("Falha ao deletar o arquivo temporário: " + tempFile.getAbsolutePath());
            }
        }

        // Gera um ID único 
        String dataId = UUID.randomUUID().toString();
        pendingData.put(dataId, resultados); // Armazena os dados na memória temporariamente

        return new UploadResponse(resultados, dataId); // Retorna os dados extraídos e o ID
    }

    // Etapa 2: Recebe os dados confirmados ou corrigidos e gera o Excel
    @PostMapping("/review/{dataId}")
    public ResponseEntity<ExcelResponse> confirmarDadosCorrigidos(@PathVariable String dataId, @RequestBody List<RomaneioInfo> dadosCorrigidos) throws IOException {
        // Verifica se o ID é válido
        if (!pendingData.containsKey(dataId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        pendingData.remove(dataId); // Remove os dados temporários após uso

        String excelId = excelExporter.exportToExcel(dadosCorrigidos); // Gera o arquivo Excel
        return ResponseEntity.ok(new ExcelResponse(excelId)); // Retorna o ID do Excel
    }

    // Etapa 3: Endpoint para download do Excel gerado
    @GetMapping("/download/{id}")
    public ResponseEntity<InputStreamResource> downloadExcel(@PathVariable String id) throws IOException {
        File excelFile = excelExporter.getExcelFile(id); // Recupera o arquivo Excel

        // Verifica se o arquivo existe
        if (excelFile == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        InputStreamResource resource = new InputStreamResource(new FileInputStream(excelFile));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + excelFile.getName())
                .contentLength(excelFile.length())
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(resource); // Retorna o arquivo para download
    }
}
