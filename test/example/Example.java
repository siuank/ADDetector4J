package example;

import ll4j.products.addetector.ADDetector;

import java.io.*;

public class Example {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("ad-detector.model"));
        ADDetector detector = new ADDetector(ADDetector.Tokenizer.loadFromFile("t1.tokenized.txt"), reader.lines().toArray(String[]::new));
        reader.close();

        System.out.println(detector.predict("123456"));
    }
}
