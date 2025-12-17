package org.example.vechimo;

public class InterventionRecord {
    String type, job, act;
    int salary;
    InterventionRecord(String type, String job, String act,int salary){
        this.type = type;
        this.job = job;
        this.act = act;
        this.salary = salary;
    }

    //

    InterventionRecord(String type, String job, int salary){
        this.type = type;
        this.job = job;
        this.salary = salary;
        this.act = "decizie";
    }

    public String getDescription(){
        return switch (type) {
            case "inregistrare" -> "Inrgistrare Contract";
            case "majorare" -> "Majorare salariu";
            case "inspection" -> "Inspection:";
            default -> "Unknown intervention type";
        };
    }

    public void print(){
        System.out.println(type + " " + job + " " + act + " " + salary + " |");
    }

}
