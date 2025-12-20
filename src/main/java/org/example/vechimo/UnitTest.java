package org.example.vechimo;

public class UnitTest implements DocumentExtractor{


    // in the base implemenation ( the real one ) this method extracts text lines from an image located at imagePath
    // in this case we do not need to actually scan an image and call an OCR library,
    // we read the text lines List<String> from a .txt file

    @Override
    public java.util.List<String> extractTextLines(String imagePath) {
        // we do not need to use the imagePath parameter here


        return null;
    }
}
