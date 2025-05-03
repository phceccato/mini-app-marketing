package com.aegro.projeto.service;

import com.aegro.projeto.model.ReciboCargaInfo;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class ExcelExporter {

    private static final String FILE_NAME = "recibo_saida.xlsx";

    public void exportToExcel(ReciboCargaInfo info) throws IOException {
        Workbook workbook;
        Sheet sheet;

        File file = new File(FILE_NAME);

        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                workbook = WorkbookFactory.create(fis);
                sheet = workbook.getSheetAt(0);
            }
        } else {
            workbook = new XSSFWorkbook();
            sheet = workbook.createSheet("Recebimento");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Data");
            header.createCell(1).setCellValue("Nome do Produtor");
            header.createCell(2).setCellValue("Tipo de Cultura");
            header.createCell(3).setCellValue("Peso Bruto");
            header.createCell(4).setCellValue("Umidade");
            header.createCell(5).setCellValue("Impureza");
            header.createCell(6).setCellValue("Peso Líquido");
        }

        int lastRow = sheet.getLastRowNum() + 1;
        Row row = sheet.createRow(lastRow);
        row.createCell(0).setCellValue(info.getData());
        row.createCell(1).setCellValue(info.getNomeProdutor());
        row.createCell(2).setCellValue(info.getTipoCultura());
        row.createCell(3).setCellValue(info.getPesoBruto());
        row.createCell(4).setCellValue(info.getUmidade());
        row.createCell(5).setCellValue(info.getImpureza());
        row.createCell(6).setCellValue(info.getPesoLiquido());


        try (FileOutputStream fos = new FileOutputStream(FILE_NAME)) {
            workbook.write(fos);
        }

        workbook.close();
    }
}
