package org.kesler.pvdsorter.export;


import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.kesler.pvdsorter.domain.Record;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Корневой класс для классов формирования ведомостей
 */
public class RecordsExporter {

    private List <Record> records;

    protected SXSSFWorkbook wb = new SXSSFWorkbook(100);

    public RecordsExporter(Collection<Record> records) {
        this.records = new ArrayList<Record>(records);
    }

    public void prepare() {

        Sheet sh = wb.createSheet();

        sh.setColumnWidth(0, 256*5);
        sh.setColumnWidth(1, 256*28);
        sh.setColumnWidth(2, 256*15);

        // Создаем шапку

        CellStyle headerCellStyle = wb.createCellStyle();
        headerCellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        Font font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        headerCellStyle.setFont(font);

        Row headerRow = sh.createRow(0);
        Cell cell = headerRow.createCell(0);
        headerRow.createCell(1);
        cell.setCellValue("Ведомость");
        cell.setCellStyle(headerCellStyle);
        headerRow.createCell(2);
        CellRangeAddress range = new CellRangeAddress(0,0,0,2);
        sh.addMergedRegion(range);

        headerRow = sh.createRow(1);
        cell = headerRow.createCell(0);
        cell.setCellValue("передачи документов в филиал ");
        cell.setCellStyle(headerCellStyle);
        headerRow.createCell(1);
        headerRow.createCell(2);
        range = new CellRangeAddress(1,1,0,2);
        sh.addMergedRegion(range);

        headerRow = sh.createRow(2);
        cell = headerRow.createCell(0);
        cell.setCellValue(records.get(0).getBranch().getName());
        cell.setCellStyle(headerCellStyle);
        headerRow.createCell(1);
        headerRow.createCell(2);
        range = new CellRangeAddress(2,2,0,2);
        sh.addMergedRegion(range);

        headerRow = sh.createRow(3);
        cell = headerRow.createCell(0);
        cell.setCellValue("от " + new SimpleDateFormat("dd.MM.yyyy").format(new Date()));
        cell.setCellStyle(headerCellStyle);
        headerRow.createCell(1);
        headerRow.createCell(2);
        range = new CellRangeAddress(3,3,0,2);
        sh.addMergedRegion(range);





        int tableRowPos = 5;

        // Создаем заголовок
        Row titleRow = sh.createRow(tableRowPos);

        // Стиль

        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setWrapText(true);
        cellStyle.setBorderTop(CellStyle.BORDER_THIN);
        cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
        cellStyle.setBorderRight(CellStyle.BORDER_THIN);
        cellStyle.setBorderBottom(CellStyle.BORDER_THIN);


        cell = titleRow.createCell(0);
        cell.setCellValue("№ п/п");
        cell.setCellStyle(cellStyle);

        cell = titleRow.createCell(1);
        cell.setCellValue("Код Росреестра");
        cell.setCellStyle(cellStyle);

        cell = titleRow.createCell(2);
        cell.setCellValue("Дата приема");
        cell.setCellStyle(cellStyle);


        // формируем список основных де


        // Заполняем таблицу

        for (int rownum = 0; rownum < records.size(); rownum++) {
            Record record = records.get(rownum);
            Row row = sh.createRow(rownum + tableRowPos+1);
            for (int colnum = 0; colnum < 3; colnum++) {
                cell = row.createCell(colnum);
                String value = "";
                switch (colnum) {
                    case 0:
                        value += rownum+1;
                        break;
                    case 1:
                        value = record.getRegnum();
                        break;
                    case 2:
                        value = record.getRegdateString();
                        break;
                    default:
                        break;
                }

                cell.setCellValue(value);

                cellStyle = wb.createCellStyle();
                cellStyle.setWrapText(true);
                cellStyle.setBorderTop(CellStyle.BORDER_THIN);
                cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
                cellStyle.setBorderRight(CellStyle.BORDER_THIN);
                cellStyle.setBorderBottom(CellStyle.BORDER_THIN);

                cell.setCellStyle(cellStyle);
            }
        }

        int footerRowPos = tableRowPos + records.size() + 4;

        Row footerRow = sh.createRow(footerRowPos);

        footerRow.createCell(0);
        cell = footerRow.createCell(1);
        cell.setCellValue("передал________");
        footerRow.createCell(2);

        footerRow = sh.createRow(footerRowPos+2);

        footerRow.createCell(0);
        cell = footerRow.createCell(1);
        cell.setCellValue("принял________");
        footerRow.createCell(2);

        footerRow = sh.createRow(footerRowPos+4);

        footerRow.createCell(0);
        cell = footerRow.createCell(1);
        cell.setCellValue("курьер________");
        footerRow.createCell(2);




    }


    public void save(Window stage) {
        FileChooser fileChooser = new FileChooser();
        //fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Файл Excel",".xlsx"));

        File file = null;

        file = fileChooser.showSaveDialog(stage);

        if (file == null) {
            return;
        }

        String filePath = file.getPath();
        if(filePath.indexOf(".xlsx") == -1) {
            filePath += ".xlsx";
            file = new File(filePath);
        }

        if (file.exists()) {

        }

        try {
            file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);
            wb.write(out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        Desktop desktop = null;
        if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
        }

        //Открытие файла:

        try {
            desktop.open(file);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }


}
