package com.aegro.projeto.service;

import com.aegro.projeto.model.ReciboCargaInfo;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class ExcelExporter {

    public void exportToExcel(ReciboCargaInfo info) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Recebimento");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Peso");
        header.createCell(1).setCellValue("Umidade");
        header.createCell(2).setCellValue("Tipo de Carga");
        header.createCell(3).setCellValue("Folhagem");
        header.createCell(4).setCellValue("Nome do Motorista");

        Row row = sheet.createRow(1);
        row.createCell(0).setCellValue(info.getPeso());
        row.createCell(1).setCellValue(info.getUmidade());
        row.createCell(2).setCellValue(info.getTipoCarga());
        row.createCell(3).setCellValue(info.getFolhagem());
        row.createCell(4).setCellValue(info.getNomeMotorista());

        try (FileOutputStream fileOut = new FileOutputStream("recibo_saida.xlsx")) {
            workbook.write(fileOut);
        }

        workbook.close();
    }
}
