package org.example.vechimo;

import java.io.IOException;
import java.util.List;

public interface DocumentExtractor {



    List<String> extractTextLines(String imagePath) throws IOException;


}
