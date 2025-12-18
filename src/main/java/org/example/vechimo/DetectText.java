package org.example.vechimo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.github.cdimascio.dotenv.Dotenv;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.*;


import java.util.*;

public class DetectText {


    private final TextractClient textractClient;
    private final Dotenv dotenv;

    public DetectText() {
        this.dotenv = Dotenv.load();
        String awsRegion = dotenv.get("AWS_REGION") != null ? dotenv.get("AWS_REGION") : "us-east-1";

        Map<String, String> env = EnvLoader.loadEnvFromJarDirectory(".env", true);

        String awsAccessKeyId = env.get("AWS_ACCESS_KEY_ID");
        String awsSecretAccessKey = env.get("AWS_SECRET_ACCESS_KEY");
        System.out.println("AWS_ACCESS_KEY_ID: " + awsAccessKeyId);
        System.out.println("AWS_SECRET_ACCESS_KEY: " + awsSecretAccessKey);



        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(awsAccessKeyId, awsSecretAccessKey);

        this.textractClient = TextractClient.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();
    }

    public DetectText(Region region) {
        this.dotenv = Dotenv.load();
        String awsAccessKeyId = dotenv.get("AWS_ACCESS_KEY_ID");
        String awsSecretAccessKey = dotenv.get("AWS_SECRET_ACCESS_KEY");

        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(awsAccessKeyId, awsSecretAccessKey);

        this.textractClient = TextractClient.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();
    }

    public List<String> extractTextLines(String imagePath) throws IOException {
        File imageFile = new File(imagePath);
        List<String> lines = new ArrayList<>();

        try (FileInputStream imageStream = new FileInputStream(imageFile)) {
            SdkBytes sourceBytes = SdkBytes.fromInputStream(imageStream);

            Document document = Document.builder()
                    .bytes(sourceBytes)
                    .build();

            DetectDocumentTextRequest request = DetectDocumentTextRequest.builder()
                    .document(document)
                    .build();

            DetectDocumentTextResponse response = textractClient.detectDocumentText(request);

            for (Block block : response.blocks()) {
                if (block.blockType() == BlockType.LINE) {
                    String lineText = block.text().trim();
                    if (!lineText.isEmpty()) {
                        lines.add(lineText);
                    }

                    System.out.println(lineText);

                }
            }
        }

        System.out.println("\n\nExtracted lines: " + lines);

        return lines;
    }

    public void extractMap(String imagePath, Map<String, String > userMap, Map<String, InterventionRecord > ProgressMap) throws IOException {
        File imageFile = new File(imagePath);


        List < String> textBlocks = DetectText.this.extractTextLines(imagePath);

        for( int i = 0 ; i < textBlocks.size()  ; i++ ) {
            String line = textBlocks.get(i);

            if(line.contains("CNP") && line.contains("identificat")){
                String[] parts = line.split("CNP");
                if( parts.length > 1 ) {
                    String cnpValue = parts[1].trim().replaceAll("[:.]", "").replaceAll("\\s+", "");
                    userMap.put("CNP", cnpValue);
                }
            }
            else if(line.contains("Salariat:")){
                    userMap.put("name", textBlocks.get(i+1).trim());
            }
            else if (line.toLowerCase().contains("contract individual de munc") ) {
                // extract contract number (e.g. 1039/138) after "num" and date (e.g. 15.02.2018) after "data"
                int numIdx = line.toLowerCase().indexOf("num");
                if (numIdx >= 0) {
                    java.util.regex.Matcher m = java.util.regex.Pattern.compile("([0-9/\\-]+)").matcher(line.substring(numIdx));
                    if (m.find()) {
                        userMap.put("contract_number", m.group(1));
                    }
                }
                int dataIdx = line.toLowerCase().indexOf("data");
                if (dataIdx >= 0) {
                    java.util.regex.Matcher dm = java.util.regex.Pattern.compile("([0-9]{1,2}[\\.\\-/][0-9]{1,2}[\\.\\-/][0-9]{2,4})").matcher(line.substring(dataIdx));
                    if (dm.find()) {
                        userMap.put("contract_date", dm.group(1));
                    }
                }
                // fallback: check next line if not found
                if (!userMap.containsKey("contract_number") && i + 1 < textBlocks.size()) {
                    java.util.regex.Matcher m2 = java.util.regex.Pattern.compile("([0-9/\\-]+)").matcher(textBlocks.get(i + 1));
                    if (m2.find()) userMap.put("contract_number", m2.group(1));
                }
                if (!userMap.containsKey("contract_date") && i + 1 < textBlocks.size()) {
                    java.util.regex.Matcher d2 = java.util.regex.Pattern.compile("([0-9]{1,2}[\\.\\-/][0-9]{1,2}[\\.\\-/][0-9]{2,4})").matcher(textBlocks.get(i + 1));
                    if (d2.find()) userMap.put("contract_date", d2.group(1));
                } // here we do know when he signed the contract ( like when he got hired )


            }
            else if(line.contains("COR") && line.contains("Data inceput contract")){
                String[] parts = line.split("COR");
                if (parts.length > 1) {
                    String afterCor = parts[1];
                    int dashIdx = afterCor.indexOf('-');
                    if (dashIdx >= 0) {
                        afterCor = afterCor.substring(0, dashIdx);
                    }
                    int commaIdx = afterCor.indexOf(',');
                    if (commaIdx >= 0) {
                        afterCor = afterCor.substring(0, commaIdx);
                    }
                    String jobValue = afterCor.trim().replaceAll("[^0-9]", "");
                    if (!jobValue.isEmpty()) {
                        System.out.println(jobValue + " \n\n");
                        User.currentJob = jobValue;
                        userMap.put("job", jobValue);
                    }
                }
                continue;
            }



            if (line.toLowerCase().contains("salariu brut stabilit la")) {
                putInterventionRecordMajorare(line, textBlocks.get(i-1));
               // ProgressMap.put("initial_salary", Record);
            }
            else if(line.toLowerCase().contains("nceteaza contract la data") ) {
                putInterventionRecordIncetare(line, textBlocks.get(i-1));
            }

            if(User.isCOR(line) && line.contains("Ore de zi") && !line.contains("COR")){
                putInterventionRecordPromovare(line, textBlocks.get(i-1));

            }


            // Contract individual de muncă numărul 1039/138 din data 15.02.2018, pe durata Nedeterminată de la 19.02.2018,

        }

        System.out.println("Text blocks for map extraction: " + textBlocks);


        User.print();


    }

    private static void putInterventionRecordMajorare(String line, String beforeline) {
        double salary = 0;
        // extract the salary amount after "la" and before "lei"
        int laIdx = line.toLowerCase().indexOf("la");
        int leiIdx = line.toLowerCase().indexOf("lei");
        if (laIdx >= 0 && leiIdx > laIdx) {
            String salaryStr = line.substring(laIdx + 2, leiIdx).trim().replaceAll("[^0-9]", "");
            salary = Double.parseDouble(salaryStr);
        }
        InterventionRecord Record = new InterventionRecord("majorare", User.currentJob, "decizie", (int) salary);
        User.currentSalary = (int) salary;
        User.ProgressMap.put(beforeline.trim(), Record);
    }

    private static void putInterventionRecordIncetare(String line, String beforeline) {
        InterventionRecord Record = new InterventionRecord("incetare", User.currentJob, "decizie", User.currentSalary); // this is alright
        User.ProgressMap.put(beforeline.trim(), Record);


    }

    private static void putInterventionRecordPromovare(String line, String beforeline) {

        String startDate = null;
        java.util.regex.Matcher dateMatcher = java.util.regex.Pattern
                .compile("^\\s*([0-9]{1,2}[\\.\\-/][0-9]{1,2}[\\.\\-/][0-9]{2,4})")
                .matcher(line);
        if (dateMatcher.find()) {
            startDate = dateMatcher.group(1).trim();
        }

        // extract job code before hyphen (e.g. 522101 from "522101-VÂNZÄTOR")
        line = line.substring(22);
        String jobValue = null;
        java.util.regex.Matcher jobMatcher = java.util.regex.Pattern.compile("(\\d+)\\s*-").matcher(line);
        if (jobMatcher.find()) {
            jobValue = jobMatcher.group(1).trim();
        }

        if (jobValue != null && !jobValue.isEmpty()) {
            User.currentJob = jobValue;
        }

        String key = (startDate != null && !startDate.isEmpty()) ? startDate : (beforeline != null ? beforeline.trim() : "unknown_date");


        if(!User.isInitialized){
            InterventionRecord record = new InterventionRecord("inregistrare", User.currentJob, "cim ", User.currentSalary);
            User.isInitialized = true;
            // this is a problem because of the way the parsing works, when we do reach this point ( the list of the promotions and the register , we already reached the "majorare" and User.currently salary is set to the latest salary

            User.ProgressMap.put(key, record);
            return;
        }



        InterventionRecord record = new InterventionRecord("promovare", User.currentJob, "decizie", User.currentSalary);


        User.ProgressMap.put(key, record);
    }



    public void close() {
        if (textractClient != null) {
            textractClient.close();
        }
    }

    public static class TextBlock {
        private final String text;
        private final Float confidence;
        private final Geometry geometry;

        public TextBlock(String text, Float confidence, Geometry geometry) {
            this.text = text;
            this.confidence = confidence;
            this.geometry = geometry;
        }

        public String getText() {
            return text;
        }

        public Float getConfidence() {
            return confidence;
        }

        public Geometry getGeometry() {
            return geometry;
        }

        @Override
        public String toString() {
            return String.format("Text: %s (Confidence: %.2f%%)", text, confidence);
        }
    }


    public static void main(String[] args, File imageFile) {
        DetectText detector = new DetectText();

        try {
            String imagePath = imageFile.getAbsolutePath().replaceFirst("^file:", "");

            // Extract text as a single string


            // Extract text as lines
            List<String> lines = detector.extractTextLines(imagePath);
            System.out.println("\nExtracted Lines:");
            lines.forEach(System.out::println);

            // Extract with details
//            List<TextBlock> blocks = detector.extractTextWithDetails(imagePath);
//            System.out.println("\nDetailed Extraction:");
//            blocks.forEach(System.out::println);

        } catch (IOException e) {
            System.err.println("Error extracting text: " + e.getMessage());
        } finally {
            detector.close();
        }
    }

}
