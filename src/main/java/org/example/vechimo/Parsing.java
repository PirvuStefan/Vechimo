package org.example.vechimo;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Parsing {

    private static final String ARHIVA_DIR = "arhiva";




    public static void ensureArhivaDirectory() {
        File arhivaDir = new File(ARHIVA_DIR);
        if (!arhivaDir.exists()) {
            arhivaDir.mkdir();
        }
    }

    public static Map<String, String> extractDataFromImage(String imagePath) throws IOException {
        if (imagePath == null || imagePath.isEmpty()) {
            return new HashMap<>();
        }
        return new DetectText().extractMap(imagePath);
    }

    public static void main(String[] args) throws IOException {
        ensureArhivaDirectory();
        String testImagePath = "path/to/your/image.jpg"; // Replace with actual image path
        Map<String, String> extractedData = extractDataFromImage(testImagePath);
        System.out.println("Extracted Data: " + extractedData);
    }
}
