package org.example.vechimo;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Parsing {

    private static final String ARHIVA_DIR = "arhiva";
    private static Map<String , String> userDataMap = new HashMap<>();
    private static Map<String, String > userProgressMap = new TreeMap<>();






    private static void ensureArhivaDirectory() {
        File arhivaDir = new File(ARHIVA_DIR);
        if (!arhivaDir.exists()) {
            arhivaDir.mkdir();
        }
    }

    public static Map<String, String> extractDataFromImage(String imagePath) throws IOException {
        Parsing.ensureArhivaDirectory();
        if (imagePath == null || imagePath.isEmpty()) {
            return new HashMap<>();
        }
        return new DetectText().extractMap(imagePath);
    } // here we should have a method that returns the word pdf file after the map extraction
    // the logic of the initilialization of the map should be in the DetectText class, and here we do have a void / file return method that calls the extractMap method ( gets the placeholders and fills them in the word template )


}
