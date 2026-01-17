package org.example.vechimo;
import org.apache.poi.xwpf.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class Certificate {


    static void generateCertificate() throws IOException {

        String outputPath = "arhiva/" + User.DataMap.get("name") + ".docx";

        try (InputStream templateStream = Certificate.class.getResourceAsStream("/template.docx");
             XWPFDocument document = new XWPFDocument(templateStream)) {

            if (templateStream == null) {
                throw new IOException("Template file not found in resources");
            }

            // Replace placeholders in paragraphs
            modifyParagraphs(document.getParagraphs(), document, false);


            // Replace placeholders in tables too (many contracts use tables)
            for (XWPFTable table : document.getTables()) {
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        modifyParagraphs(cell.getParagraphs(), document, true);
                    }
                }
            }

            // Save the new document
            try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                document.write(fos);
            }
        }
    }

    private static void modifyParagraphs(List<XWPFParagraph> paragraphs, XWPFDocument document, boolean table) {
        for (XWPFParagraph paragraph : paragraphs) {
            for (XWPFRun run : paragraph.getRuns()) {
                String text = run.getText(0);
                if (text != null) {
                    for (Map.Entry<String, String> entry : User.DataMap.entrySet()) {
                        String key = entry.getKey();
                        PlaceHolders placeholder = PlaceHolders.valueOf(key);
                        text = text.replace(placeholder.getSymbol(), entry.getValue());
                    }

                    if(table){
                        int count = 1;
                        for(Map.Entry<String, List< InterventionRecord >> entry : User.ProgressMap.entrySet()) {
                            for( InterventionRecord record : entry.getValue() ) {
                                String description = record.getDescription();
                                String date = entry.getKey();
                                String act = record.getAct();
                                String salary = String.valueOf(record.salary);
                                String job = record.getJob();
                                PlaceHolders placeholder = PlaceHolders.valueOf("date");
                                text = text.replace(count + placeholder.getSymbol(), date);
                                placeholder = PlaceHolders.valueOf("intervention");
                                text = text.replace(count + placeholder.getSymbol(), description);
                                placeholder = PlaceHolders.valueOf("act");
                                text = text.replace(count + placeholder.getSymbol(), act);
                                placeholder = PlaceHolders.valueOf("salary");
                                text = text.replace(count + placeholder.getSymbol(), salary);
                                placeholder = PlaceHolders.valueOf("job");
                                text = text.replace(count + placeholder.getSymbol(), job);
                                count++;
                                // TODO: numbers do not work when we concatenate them with the placeholder symbol
                                // in order to fix this , we choose 1-15 distinct symbols from latin extended-b and use them to map the numbers from 1 to 15
                                // and additional function is needed to get the symbol from number ( int -> symbol )
                                // or just put the symbols in the enum and get them by their ordinal value
                            }
                        }
                    }
                    run.setText(text, 0);
                }
                System.out.println(run.getText(0)); // Debugging line to print run text
            }
        }
    }

}
