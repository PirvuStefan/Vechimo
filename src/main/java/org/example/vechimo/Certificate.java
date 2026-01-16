package org.example.vechimo;
import org.apache.poi.xwpf.usermodel.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Certificate {


    static void generateCertificate() throws IOException {

        String templatePath = "template.docx";
        String outputPath = "arhiva/" + User.DataMap.get("name") + ".docx";
        try (FileInputStream fis = new FileInputStream(templatePath);
             XWPFDocument document = new XWPFDocument(fis)) {

            // Replace placeholders in paragraphs
            modifyParagraphs(document.getParagraphs(), document);

            //System.out.println("Finished replacing placeholders in paragraphs.\n\n\n\n"); // Debugging line

            // Replace placeholders in tables too (many contracts use tables)
            for (XWPFTable table : document.getTables()) {
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        modifyParagraphs(cell.getParagraphs(), document);
                    }
                }
            }

            // Save the new document
            try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                document.write(fos);
            }
        }
    }

    private static void modifyParagraphs(List<XWPFParagraph> paragraphs, XWPFDocument document) {
        for (XWPFParagraph paragraph : paragraphs) {
            for (XWPFRun run : paragraph.getRuns()) {
                String text = run.getText(0);
                if (text != null) {
                    for (Map.Entry<String, String> entry : User.DataMap.entrySet()) {
                        String key = entry.getKey();
                        PlaceHolders placeholder = PlaceHolders.valueOf(key);
                        text = text.replace(placeholder.getSymbol(), entry.getValue());
                    }
                    run.setText(text, 0);
                }
                System.out.println(run.getText(0)); // Debugging line to print run text
            }
        }
    }

}
