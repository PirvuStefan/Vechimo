package org.example.vechimo;
import org.apache.poi.xwpf.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class Certificate {


    static void generateCertificate() throws IOException {

        String outputPath = "arhiva/" + User.DataMap.get("name") + " AdeverintaVechime.docx";

        try (InputStream templateStream = Certificate.class.getResourceAsStream("/template.docx");
             XWPFDocument document = new XWPFDocument(templateStream)) {

            modifyParagraphs(document.getParagraphs(), document, false);


            for (XWPFTable table : document.getTables()) {
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        modifyParagraphs(cell.getParagraphs(), document, true);
                    }
                }
            }

            try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                document.write(fos);
            }
        }
    }

    private static void modifyParagraphs(List<XWPFParagraph> paragraphs, XWPFDocument document, boolean table) {
        for (XWPFParagraph paragraph : paragraphs) {
            // Concatenate all runs to work on the paragraph as a whole
            StringBuilder paragraphBuilder = new StringBuilder();
            List<XWPFRun> runs = paragraph.getRuns();
            for (XWPFRun run : runs) {
                String r = run.getText(0);
                if (r != null) paragraphBuilder.append(r);
            }

            String text = paragraphBuilder.toString();
            if (!text.isEmpty()) {
                for (Map.Entry<String, String> entry : User.DataMap.entrySet()) {
                    String key = entry.getKey();
                    PlaceHolders placeholder = PlaceHolders.valueOf(key);
                    text = text.replace(placeholder.getSymbol(), entry.getValue());
                }

                if (table) {
                    int count = 1;
                    for (Map.Entry<String, List<InterventionRecord>> progEntry : User.ProgressMap.entrySet()) {

                        for (InterventionRecord record : progEntry.getValue()) {
                            String description = record.getDescription();
                            String date = progEntry.getKey();
                            String act = record.getAct();
                            String salary = String.valueOf(record.salary);
                            String job = record.getJob();

                            text = text.replace("Dat" + count + "r", date);
                            text = text.replace("Inreg" + count + "r", description);
                            text = text.replace("Act" + count + "r", act);
                            text = text.replace("Sal" + count + "r", salary);
                            text = text.replace("Mes" + count + "r", job);

                            count++;

                        }
                    }
                }

                // Replace paragraph content: set first run and remove the rest
                if (!runs.isEmpty()) {
                    XWPFRun firstRun = runs.get(0);
                    firstRun.setText(text, 0);
                    for (int i = runs.size() - 1; i >= 1; i--) {
                        paragraph.removeRun(i);
                    }
                } else {
                    XWPFRun newRun = paragraph.createRun();
                    newRun.setText(text, 0);
                }
            }

            System.out.println(paragraph.getText());
        }
    }


}
