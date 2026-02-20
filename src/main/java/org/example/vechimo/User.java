package org.example.vechimo;

import org.example.screens.WindowController;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;


public class User { // static class to hold user data across different screens ( no abstract class since we only have one user during runtime )

    static String currentJob ; // default value if for some reason the parsing fails to initialize it
    static int currentSalary = 0;
    static int count = 0;
    static boolean isInitialized = false;
    private static final String DATE_FORMAT = "dd.MM.yyyy";
    static Map<String , String> DataMap = new HashMap<>();
    static TreeMap<String, List<InterventionRecord>> ProgressMap = new TreeMap<>(new org.example.vechimo.YearComparator());

    public static void extractMap(String imagePath1,String imagePath2) throws IOException {

        boolean test = false;

        List < String > textBlocks = ProcessorFactory.getProcessor(test).extractTextLines(imagePath1);

        if (imagePath2 != null && !imagePath2.isEmpty() && !imagePath2.equals("Niciun fișier selectat...")) {
            List<String> textBlocks2 = ProcessorFactory.getProcessor(test).extractTextLines(imagePath2);
            textBlocks = Stream.concat(textBlocks.stream(), textBlocks2.stream()).toList();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        String today = LocalDate.now().format(formatter);
        DataMap.put("today", today);






        for( int i = 0 ; i < textBlocks.size()  ; i++ ) {
            String line = textBlocks.get(i);

            if(line.contains("CNP") && line.contains("identificat")){
                String[] parts = line.split("CNP");
                if( parts.length > 1 ) {
                    String cnpValue = parts[1].trim().replaceAll("[:.]", "").replaceAll("\\s+", "");
                    DataMap.put("CNP", cnpValue);
                }
            }
            else if(line.contains("Salariat:")){
                if(!textBlocks.get(i+1).trim().contains("identificat")) DataMap.put("name", textBlocks.get(i+1).trim());
                else if(!textBlocks.get(i+2).trim().contains("identificat")) DataMap.put("name", textBlocks.get(i+2).trim());
                else DataMap.put("name", "nume_nedefinit");
            }
            else if (line.toLowerCase().contains("contract individual de munc") ) {
                // extract contract number (e.g. 1039/138) after "num" and date (e.g. 15.02.2018) after "data"
                int numIdx = line.toLowerCase().indexOf("num");
                if (numIdx >= 0) {
                    java.util.regex.Matcher m = java.util.regex.Pattern.compile("([0-9/\\-]+)").matcher(line.substring(numIdx));
                    if (m.find()) {
                        DataMap.put("contractNumber", m.group(1));
                    }
                }
                int dataIdx = line.toLowerCase().indexOf("data");
                if (dataIdx >= 0) {
                    java.util.regex.Matcher dm = java.util.regex.Pattern.compile("([0-9]{1,2}[\\.\\-/][0-9]{1,2}[\\.\\-/][0-9]{2,4})").matcher(line.substring(dataIdx));
                    if (dm.find()) {
                        DataMap.put("contractDate", dm.group(1));
                    }
                }
                // fallback: check next line if not found
                if (!DataMap.containsKey("contractNumber") && i + 1 < textBlocks.size()) {
                    java.util.regex.Matcher m2 = java.util.regex.Pattern.compile("([0-9/\\-]+)").matcher(textBlocks.get(i + 1));
                    if (m2.find()) DataMap.put("contractNumber", m2.group(1));
                }
                if (!DataMap.containsKey("contractDate") && i + 1 < textBlocks.size()) {
                    java.util.regex.Matcher d2 = java.util.regex.Pattern.compile("([0-9]{1,2}[\\.\\-/][0-9]{1,2}[\\.\\-/][0-9]{2,4})").matcher(textBlocks.get(i + 1));
                    if (d2.find()) DataMap.put("contractDate", d2.group(1));
                } // here we do know when he signed the contract ( like when he got hired )


            }
            else if(line.contains("COR") && line.contains("Data inceput contract")){
                String[] parts = line.split("COR");
                getIntregistrationCOR(parts);
                continue;
            }
            else if(line.contains("Data inceput") && textBlocks.get(i-1).contains("COR")){
                String[] parts = textBlocks.get(i-1).split("COR");
                getIntregistrationCOR(parts);
                continue;
            }



            if(line.contains("Suspendari in timpul contractului") || line.contains("Suspendări în timpul contractului")){

                //Suspendări în timpul contractului
                // from this line we should extract the suspension periods and reasons
                // this is a bit more complex and may require additional parsing logic based on the expected format
                // this handles all the suspension records and adds them to the progress map with the type "suspension" and the reason as description
                // further swaping in the InterventionRecord array might be needed if the intervention share the same key (date) with another intervention, but this should be handled in the updateInterventionRecord function
               AtomicInteger pos = new AtomicInteger(i);
               handleSuspensions(textBlocks, pos);
               i = pos.get();


            }


            if(line.contains("Salariu brut") && textBlocks.get(i-1).contains("COR") && textBlocks.get(i-1).contains("Data inceput")){
                // here we have the initial salary and the initial job ( inregistrare )
                InterventionRecord.putInterventionRecordInregistrare(line, textBlocks.get(i-1));
                continue;
            }
            else if(line.contains("Salariu brut") && textBlocks.get(i-2).contains("COR") && textBlocks.get(i-1).contains("Data inceput")){
                // exceptional case when the job title is way too long ( e.g. "Consilier/Expert/Inspector/Referent/Economist în Comerţ şi Marketing") and it is split in two lines, so we need to check the line before the previous one for the job title
                InterventionRecord.putInterventionRecordInregistrare(line, textBlocks.get(i-2));
                continue;
            }

            if (line.toLowerCase().contains("salariu brut stabilit la")) {
                InterventionRecord.putInterventionRecordMajorare(line, textBlocks.get(i-1));

            }
            else if(line.toLowerCase().contains("nceteaza contract la data") ) {
                InterventionRecord.putInterventionRecordIncetare(line, textBlocks.get(i-1));
            }



            if(User.isCOR(line) && line.contains("Ore de zi") && !line.contains("COR")){
                InterventionRecord.putInterventionRecordPromovare(line, textBlocks.get(i-1));

            }


            // Contract individual de muncă numărul 1039/138 din data 15.02.2018, pe durata Nedeterminată de la 19.02.2018,

        }

        System.out.println("Text blocks for map extraction: " + textBlocks);

        updateInterventionRecord();
        updateSuspensions();
        DataMap.put("currentSalary", Integer.toString(currentSalary));
        DataMap.put("currentJob", getCurrentJob());


        User.print();


    }

    private static void getIntregistrationCOR(String[] parts) {
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
            }
        }
    }

    static void handleSuspensions(List<String> textBlocks, AtomicInteger pos){
        System.out.println("Handling suspensions starting \n\n\n\n" );
        pos.incrementAndGet();; // move to the next line after the "Suspendari in timpul contractului" line
        while(pos.get() < textBlocks.size() && !textBlocks.get(pos.get()).trim().isEmpty()){
            String[] dates = new String[2];

            if(isSuspensionRecord(textBlocks.get(pos.get()), dates)){
                InterventionRecord.putInterventionRecordSuspendare(textBlocks, pos, dates);

            }

            pos.incrementAndGet();

        }
    }

    static boolean isSuspensionRecord(String line, String[] dates) {
        if (line == null) return false;

        java.util.regex.Matcher m = java.util.regex.Pattern.compile(
                "\\b([0-9]{1,2}[\\.\\-/][0-9]{1,2}[\\.\\-/][0-9]{2,4})\\s*(?:-|–|—)?\\s*([0-9]{1,2}[\\.\\-/][0-9]{1,2}[\\.\\-/][0-9]{2,4})\\b"
        ).matcher(line);

        if (m.find()) {
            dates[0] = m.group(1); // First capturing group from the match
            dates[1] = m.group(2); // Second capturing group from the SAME match
            System.out.println("Found: " + dates[0] + " to " + dates[1]);
            return true;
        }

        return false;
    }

    public static void resetData(){
        currentJob = "522101";
        DataMap.clear();
        ProgressMap.clear();
        currentSalary = 0;
        isInitialized = false;
        WindowController.setRun(false);
    }

    static void printProgressMap(){
        for (Map.Entry<String, List<InterventionRecord> > entry : ProgressMap.entrySet()) {
            System.out.print(entry.getKey() + " | ");

            for(InterventionRecord record : entry.getValue()){
                record.print();
            }
            System.out.println();

        }
    }

    static void addInterventionRecord(String key, InterventionRecord Record){
        if(User.ProgressMap.containsKey(key.trim())){
            List< InterventionRecord > records = User.ProgressMap.get(key.trim());
            records.add(Record);
            return;
        }
        List < InterventionRecord > records = new ArrayList<>();
        records.add(Record);
        User.ProgressMap.put(key.trim(), records);
    }

    static void updateInterventionRecord(){
        String job = null;
        for( int i = 0 ; i < ProgressMap.size() ; i++ ) {
            Map.Entry<String, List<InterventionRecord> > entry = (Map.Entry<String, List<InterventionRecord> >) ProgressMap.entrySet().toArray()[i];
            count = count + entry.getValue().size();
            for(int j = 0 ; j < entry.getValue().size() ; j++ ) {
                InterventionRecord record = entry.getValue().get(j);
                if(record.type.equals("promovare")){
                    job = record.job;
                    for(int k = j+1 ; k < entry.getValue().size() ; k++ ) {
                        InterventionRecord nextRecord = entry.getValue().get(k);

                        if(nextRecord.type.equals("incetare")){
                            nextRecord.job = job;

                            return;
                        }
                        if(!nextRecord.type.equals("promovare")){
                            nextRecord.job = job;
                        }
                        if(nextRecord.type.equals("promovare")) job = nextRecord.job;
                    }

                }
                else{
                  if(job!=null) record.job = job;
                }
            }
        }


    }

    static void updateSuspensions(){
        InterventionRecord lastSuspension = null;
        for(int i = 0 ; i < ProgressMap.size() ; i++ ) {
            Map.Entry<String, List<InterventionRecord> > entry = (Map.Entry<String, List<InterventionRecord> >) ProgressMap.entrySet().toArray()[i];
            for(int j = 0 ; j < entry.getValue().size() ; j++ ) {
                if(lastSuspension == null) {
                    lastSuspension = entry.getValue().get(j);
                    continue;
                }
                if(entry.getValue().get(j).type.equals("suspendare")){
                    entry.getValue().get(j).job = lastSuspension.job;

                }
                lastSuspension = entry.getValue().get(j);

            }
        }
    }

    static void printDataMap(){
        for (Map.Entry<String, String> entry : DataMap.entrySet()) {
            System.out.println("Key: " + entry.getKey() + " | Value: " + entry.getValue());
        }
    }

    static void print(){
        printDataMap();
        System.out.println("\n");
        printProgressMap();
        System.out.println("\n");
        System.out.println("At this time, the job is : " + currentJob + " which has the salary of " + currentSalary);
        System.out.println("Total intervention records: " + count);
    }

    static String getCurrentJob(){

        if(currentJob == null) return "VANZATOR";
        return switch (currentJob) {
                        case "522101" -> "VANZATOR";
                        case "142008" -> "MANAGER DE ZONA";
                        case "112018" -> "DIRECTOR VANZARI";
                        case "311519" -> "TEHNICIAN MECANIC";
                        case "243103" -> "SPECIALIST MARKETING";
                        case "263104" -> """
                             CONSILIER/EXPERT/INSPECTOR/REFERENT/ECONOMIST\s
                              ÎN COMERŢ ŞI MARKETING""";
                        default -> "GESTIONAR DEPOZIT";
                    };
    }

    static boolean isCOR(String line){
        if(line.contains("522101")) return true;
        if(line.contains("142008")) return true;
        if(line.contains("112018")) return true;
        if(line.contains("311519")) return true;
        if(line.contains("243103")) return true;
        if(line.contains("263104")) return true;
        return false;
    }

    public static void addNumber(String number){

        System.out.println(number + " is being added to the data map");
        DataMap.put("number", number);
    }


}
