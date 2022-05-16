package com.adg.api.department.InternationalPayment.handler.office.word;

import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.05.01 16:55
 */
public class WordUtils {

    public static class Table {
        public static List<XWPFRun> setCell(XWPFTableCell cell, List<String> values) {
            List<XWPFRun> runs = new ArrayList<>();
            for (String value : values) {
                XWPFRun run = Table.setCell(cell, value, false);
                runs.add(run);
            }
            return runs;
        }

        public static XWPFRun setCell(XWPFTableCell cell, String value) {
           return Table.setCell(cell, value, true);
        }


        public static XWPFRun setCell(XWPFTableCell cell, String value, boolean isClear) {
            List<XWPFParagraph> paragraphs = cell.getParagraphs();
            XWPFParagraph paragraph;

            if (isClear) {
                if (paragraphs == null || paragraphs.isEmpty()) {
                    paragraph = cell.addParagraph();
                } else {
                    paragraph = paragraphs.get(0);
                }
            } else {
                paragraph = cell.addParagraph();
            }

            XWPFRun run = paragraph.createRun();
            run.setText(value);
            return run;
        }

        public static void makeCenter(XWPFTableCell cell) {
           cell.getParagraphs().forEach(paragraph -> paragraph.setAlignment(ParagraphAlignment.CENTER));
        }

        public static XWPFTableCell mergeCell(XWPFTableRow row, int colPos, int colSpan) {
            XWPFTableCell cell = row.getCell(colPos);
            if (cell == null) {
                cell = row.createCell();
            }
            CTTc ctTc = cell.getCTTc();
            CTTcPr ctTcPr = ctTc.getTcPr();
            if (ctTcPr == null) {
                ctTcPr = ctTc.addNewTcPr();
            }
            ctTcPr.addNewGridSpan().setVal(new BigInteger(String.valueOf(colSpan)));
            if (colPos + 1 == colSpan) {
                row.removeCell(colPos + 1);
            } else {
                for (int i = colPos + 1; i < colSpan; i++) {
                    row.removeCell(colPos + 1);
                }
            }

            for (int i = 0; i < row.getCtRow().sizeOfTcArray(); i++) {
                Table.setCell(row.getCell(i), "");
            }
            return cell;
        }
    }
}
