package huzpsb.ll4j.nlp.token;

import huzpsb.ll4j.utils.data.*;
import ll4j.products.addetector.ADDetector;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Tokenizer {
    private final String[] vocab;

    public Tokenizer(String[] vocab) {
        this.vocab = vocab;
    }
    public Tokenizer(String[] vocab, int start, int length) {
        this.vocab = new String[length];
        System.arraycopy(vocab, start, this.vocab, 0, length);
    }

    public static Tokenizer loadFromFile(String file) throws FileNotFoundException {
        return loadFromStream(new FileInputStream(file));
    }

    public static Tokenizer loadFromFile(File file) throws FileNotFoundException {
        return loadFromStream(new FileInputStream(file));
    }

    public static Tokenizer loadFromStream(InputStream stream) {
        return load(new InputStreamReader(stream, StandardCharsets.UTF_8));
    }

    public static Tokenizer load(Reader reader) {
        return load(new BufferedReader(reader));
    }

    public static Tokenizer load(BufferedReader reader) {
        String[] vocab = null;
        try (reader) {
            String str;
            int index = 0;
            while ((str = reader.readLine()) != null) {
                if (vocab == null) {
                    int size = Integer.parseInt(str);
                    vocab = new String[size];
                    continue;
                }
                vocab[index++] = str;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (vocab == null) return null;
        return new Tokenizer(vocab);
    }

    public DataEntry tokenize(int type, String text) {
        String regularized = CharUtils.regularize(text);
        double[] values = new double[vocab.length + 1];
        values[0] = text.length();
        for (int i = 0; i < vocab.length; i++) {
            values[i + 1] = regularized.contains(vocab[i]) ? 1 : 0;
        }
        return new DataEntry(type, values);
    }

    public void saveToFile(String filename) {
        try (PrintWriter writer = new PrintWriter(filename, StandardCharsets.UTF_8)) {
            writer.println(vocab.length);
            for (String word : vocab) {
                writer.println(word);
            }
        } catch (Exception ignored) {
        }
    }
}
