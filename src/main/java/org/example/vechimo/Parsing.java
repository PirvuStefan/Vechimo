package org.example.vechimo;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


public class Parsing {



    private static final String ARHIVA_DIR = "arhiva";


    public Parsing(String imagePath1, String imagePath2) throws IOException {
         ensureArhivaDirectory();
         User.extractMap(imagePath1, imagePath2);
         Certificate.generateCertificate();
    }






    private static void ensureArhivaDirectory() {
        File arhivaDir = new File(ARHIVA_DIR);
        if (!arhivaDir.exists()) {
            arhivaDir.mkdir();
        }
    }
    // here we should have a method that returns the word pdf file after the map extraction
    // the logic of the initilialization of the map should be in the DetectText class, and here we do have a void / file return method that calls the extractMap method ( gets the placeholders and fills them in the word template )




}
