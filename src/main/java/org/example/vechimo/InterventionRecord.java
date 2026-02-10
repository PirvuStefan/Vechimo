package org.example.vechimo;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class InterventionRecord {
    String type, job, act;
    String date = "neither";
    int salary;
    InterventionRecord(String type, String job, String act,int salary){
        this.type = type;
        this.job = job;
        this.act = act;
        this.salary = salary;
    }

    InterventionRecord(String type, String job, String act, int salary, String date){
        this.type = type;
        this.job = job;
        this.act = act;
        this.salary = salary;
        this.date = date;
    }//this is aimed for suspendare

    public void setSuspendareAct(String date){
        this.date = date;
    }


    //

    InterventionRecord(String type, String job, int salary){
        this.type = type;
        this.job = job;
        this.salary = salary;
        this.act = "decizie";
    }

    public String getAct(){
        return switch (type) {
            case "inregistrare" -> "CIM " + User.DataMap.get("contractNumber");
            case "majorare", "incetare", "promovare" -> "DECIZIE";
            default -> "Unknown intervention type";
        };
    }
    // la medical sa fie CM
    // la restul decizilor DECIZIE, in cazul de suspendare

    public String getDescription(){
        return switch (type){
            case "inregistrare" -> "Inregistrare contract";
            case "majorare" -> "Majorare salariu";
            case "incetare" -> "Incetare contract";
            case "promovare" -> "Act aditional";
            default -> "Unknown intervention type";
        };
    }



    public void print(){
        System.out.print(type + " " + job + " " + act + " " + salary + (!date.equals("neither") ? " " + date : "") + " ; ");
    }

    static void putInterventionRecordMajorare(String line, String beforeline) {
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
        User.addInterventionRecord(beforeline, Record);
    }

    static void putInterventionRecordIncetare(String line, String beforeline) {
        InterventionRecord Record = new InterventionRecord("incetare", User.currentJob, "decizie", User.currentSalary); // this is alright
        User.addInterventionRecord(beforeline, Record);


    }

    static void putInterventionRecordPromovare(String line, String beforeline) {

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

        if(!canbePromoted(User.currentJob)){
            return;
        }

        String key = (startDate != null && !startDate.isEmpty()) ? startDate : (beforeline != null ? beforeline.trim() : "unknown_date");







        InterventionRecord record = new InterventionRecord("promovare", User.currentJob, "decizie", getSalaryPresent(key));

        User.addInterventionRecord(key, record);
    }

    static void putInterventionRecordInregistrare(String line, String beforeline){
        String key = "01.01.2004";
        int salary = 0;
        // extract salary after "Salariu brut" and before "Lei"
        int sbIdx = line.indexOf("Salariu brut");
        int leiIdx = line.indexOf("Lei");
        if(sbIdx >= 0 && leiIdx > sbIdx){
            int start = sbIdx + "Salariu brut".length();
            String salaryStr = line.substring(start, leiIdx).trim().replaceAll("[^0-9]", "");
            if(!salaryStr.isEmpty()){
                salary = Integer.parseInt(salaryStr);
            }
        }

        // extract date key after "Data inceput contract" (expected 10 chars like 01.09.2025)
        if (beforeline != null) {
            int diIdx = beforeline.indexOf("Data inceput contract");
            if (diIdx >= 0) {
                int start = diIdx + "Data inceput contract".length();
                String rest = beforeline.substring(start).trim();
                if (rest.length() >= 10) {
                    key = rest.substring(0, 10);
                } else if (!rest.isEmpty()) {
                    key = rest;
                }
            }
        }



        InterventionRecord record = new InterventionRecord("inregistrare", User.currentJob, "cim ", salary);
        User.addInterventionRecord(key, record);
    }

    static void putInterventionRecordSuspendare(List< String > textBlocks, AtomicInteger position, String[] dates){
            // String line is tehnically redundant parameter here, since line is also in textBlocks, but we keep it for consistency

//        Se suspenda contract intre 24.03.2020 si 31.05.2020 in baza temeiului legal Legea 53/2003 art. 52 alin.(1)
//        litera c) In cazul întreruperii sau reducerii temporare a activităţii, fără încetarea raportului de muncă,
//        pentru motive economice, tehnologice, structurale sau similare
        boolean medical = false;
        int finish = position.get() + 3;

        while(position.get() < finish){
            String line = textBlocks.get(position.get()).toLowerCase();
            if(line.contains("pentru incapacitate temporară de muncă") || line.contains("pentru incapacitate temporara de munca") || line.contains("pentru incapacitate temporara de muncă")){
                medical = true;
            }
            position.getAndIncrement();
        }



        InterventionRecord record = new InterventionRecord("suspendare", User.currentJob, "decizie", getSalaryPresent(dates[0]), dates[2]);
        User.addInterventionRecord(dates[0], record);


    }

    private static int getSalaryPresent(String key){
        int salary = 0;

        // this in intented to be used when we have a promotion or a registration ( most likely a registration )
        // we need to find the greatest salary up to that point in time ( assuming the salary never decreases )


        if(User.ProgressMap.get(key) != null) {
            for (InterventionRecord record : User.ProgressMap.get(key)) {
                if (record.salary > salary) salary = record.salary;
            }
        }
        if(User.ProgressMap.lowerKey(key) == null) return salary;
        for(InterventionRecord record : User.ProgressMap.get(User.ProgressMap.lowerKey(key))){
            if(record.salary > salary) salary = record.salary;

        }


        return salary;
    }

    private static boolean canbePromoted(String job){
        // test if the promotion can happen ( if we alraedy have that job, then no promotion )
        for(String key : User.ProgressMap.keySet()){
            for(InterventionRecord record : User.ProgressMap.get(key)){
                if(record.job.equals(job) && record.type.equals("inregistrare")){
                    return false;
                }
            }
        }
        return true;
    }

     String getJob(){
        if(job == null) return "VANZATOR";
        return switch (job) {
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

}
