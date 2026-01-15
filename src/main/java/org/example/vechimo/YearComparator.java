package org.example.vechimo;

import javafx.beans.binding.NumberExpression;

import java.util.Comparator;

public class YearComparator implements Comparator< String > {
    // the compare method should compare years in the format "DD.MM.YYYY"
    // the return type is int , not like c++ being bool
    // compares o1 and o2,
    // if retuns a negative integer , then o1 < o2
    // if returns zero , then o1 == o2
    // if returns a positive integer , then o1 > o2
    // so if it is negative, o1 comes before o2, if positive, o1 comes after o2


    @Override
    public int compare(String year1, String year2) {
        year1 = year1.trim();
        year2 = year2.trim();

        String parts1[] = year1.split("\\.");
        String parts2[] = year2.split("\\.");

        if (parts1.length < 3 || parts2.length < 3) {
            // Fallback to string comparison if the format is not as expected
            return year1.compareTo(year2);
        }

        try {
            Integer.parseInt(parts1[0]);
            Integer.parseInt(parts1[1]);
            Integer.parseInt(parts1[2]);
            Integer.parseInt(parts2[0]);
            Integer.parseInt(parts2[1]);
            Integer.parseInt(parts2[2]);
        } catch (NumberFormatException e) {
            System.out.println("Invalid date format for the progres map key");
            System.out.println("This should not happen: " + year1 + " or " + year2);
            return year1.compareTo(year2);
        }



        if( Integer.parseInt(parts1[2]) != Integer.parseInt(parts2[2]) ) return Integer.parseInt(parts1[2]) - Integer.parseInt(parts2[2]);
        if( Integer.parseInt(parts1[1]) != Integer.parseInt(parts2[1]) ) return Integer.parseInt(parts1[1]) - Integer.parseInt(parts2[1]);
        return Integer.parseInt(parts1[0]) - Integer.parseInt(parts2[0]);



    }
}
