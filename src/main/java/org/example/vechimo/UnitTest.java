package org.example.vechimo;

public class UnitTest implements DocumentExtractor{


    // in the base implemenation ( the real one ) this method extracts text lines from an image located at imagePath
    // in this case we do not need to actually scan an image and call an OCR library,
    // we read the text lines List<String> from a .txt file

    @Override
    public java.util.List<String> extractTextLines(String imagePath) {
        java.util.List<String> lines = new java.util.ArrayList<>();

        // try classpath resource "test.txt"
        java.io.InputStream is = getClass().getClassLoader().getResourceAsStream("test.txt");
        if (is == null) {
            // fallback to file system: "test" or "test.txt"
            java.io.File f = new java.io.File("test");
            if (!f.exists()) {
                f = new java.io.File("test.txt");
            }
            if (f.exists()) {
                try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(f))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        lines.add(line);
                    }
                } catch (java.io.IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return lines;
        }

        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(is))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }

        return lines;
    }
}
