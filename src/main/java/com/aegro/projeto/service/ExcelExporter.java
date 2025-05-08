package com.aegro.projeto.service;

import com.aegro.projeto.model.RomaneioInfo;

// Apache POI para manipulação de arquivos Excel
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;
import java.util.UUID;

// Anotação @Service define esta classe como um bean de serviço gerenciado pelo Spring
@Service
public class ExcelExporter {

    // Constante que define o diretório onde os arquivos Excel serão salvos
    private static final String FOLDER = "excel_output";

    // Método responsável por exportar uma lista de objetos RomaneioInfo para um arquivo Excel
    public String exportToExcel(List<RomaneioInfo> infoList) throws IOException {
        // Cria o diretório de saída
        File dir = new File(FOLDER);
        if (!dir.exists()) dir.mkdirs();

        // Gera um identificador único para o arquivo
        String fileId = UUID.randomUUID().toString();
        String filename = "recibo_" + fileId + ".xlsx";
        File file = new File(dir, filename);

        // Cria um novo workbook Excel (formato .xlsx)
        Workbook workbook = new XSSFWorkbook();
        // Cria uma nova planilha com o nome "Recebimento"
        Sheet sheet = workbook.createSheet("Recebimento");

        // Cria a primeira linha (cabeçalho) e define os títulos das colunas
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Data");
        header.createCell(1).setCellValue("Nome do Produtor");
        header.createCell(2).setCellValue("Tipo de Cultura");
        header.createCell(3).setCellValue("Peso Bruto");
        header.createCell(4).setCellValue("Umidade");
        header.createCell(5).setCellValue("Impureza");
        header.createCell(6).setCellValue("Peso Líquido");

        int rowIdx = 1;
        // Itera sobre a lista de objetos RomaneioInfo e preenche a planilha
        for (RomaneioInfo info : infoList) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(info.getData());
            row.createCell(1).setCellValue(info.getNomeProdutor());
            row.createCell(2).setCellValue(info.getTipoCultura());
            row.createCell(3).setCellValue(info.getPesoBruto());
            row.createCell(4).setCellValue(info.getUmidade());
            row.createCell(5).setCellValue(info.getImpureza());
            row.createCell(6).setCellValue(info.getPesoLiquido());
        }

        // Escreve os dados no arquivo Excel e fecha o workbook
        try (FileOutputStream fos = new FileOutputStream(file)) {
            workbook.write(fos);
        }
        workbook.close();

        // Retorna o identificador do arquivo gerado
        return fileId;
    }

    // Método para recuperar um arquivo Excel com base em seu identificador
    public File getExcelFile(String id) {
        File file = new File(FOLDER, "recibo_" + id + ".xlsx");
        // Retorna o arquivo, caso ele exista, senão retorna null
        return file.exists() ? file : null;
    }
}
