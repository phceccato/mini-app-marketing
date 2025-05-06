package com.aegro.projeto.service;

import com.aegro.projeto.model.RomaneioInfo;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;
import java.util.UUID;

@Service
public class ExcelExporter {

    private static final String FOLDER = "excel_output";

    public String exportToExcel(List<RomaneioInfo> infoList) throws IOException {
        File dir = new File(FOLDER);
        if (!dir.exists()) dir.mkdirs();

        String fileId = UUID.randomUUID().toString();
        String filename = "recibo_" + fileId + ".xlsx";
        File file = new File(dir, filename);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Recebimento");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Data");
        header.createCell(1).setCellValue("Nome do Produtor");
        header.createCell(2).setCellValue("Tipo de Cultura");
        header.createCell(3).setCellValue("Peso Bruto");
        header.createCell(4).setCellValue("Umidade");
        header.createCell(5).setCellValue("Impureza");
        header.createCell(6).setCellValue("Peso Líquido");

        int rowIdx = 1;
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

        try (FileOutputStream fos = new FileOutputStream(file)) {
            workbook.write(fos);
        }
        workbook.close();

        return fileId;
    }

    public File getExcelFile(String id) {
        File file = new File(FOLDER, "recibo_" + id + ".xlsx");
        return file.exists() ? file : null;
    }
}
