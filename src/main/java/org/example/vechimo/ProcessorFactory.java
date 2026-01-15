package org.example.vechimo;

public class ProcessorFactory {

    public static DocumentExtractor getProcessor(boolean isTest) {
       if(isTest) return new UnitTest();
       else return new DetectText();
    }
}
