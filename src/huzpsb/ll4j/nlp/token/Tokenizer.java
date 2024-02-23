package huzpsb.ll4j.nlp.token;

import huzpsb.ll4j.utils.data.*;

import java.io.File;
import java.io.PrintWriter;
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

    public static Tokenizer loadFromFile(String filename) {
        try (Scanner scanner = new Scanner(new File(filename))) {
            String[] vocab;
            int size = Integer.parseInt(scanner.nextLine());
            vocab = new String[size];
            for (int i = 0; i < size; i++) {
                vocab[i] = CharUtils.regularize(scanner.nextLine());
            }
            return new Tokenizer(vocab);
        } catch (Exception ignored) {}
        return null;
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
        try (PrintWriter writer = new PrintWriter(filename)) {
            writer.println(vocab.length);
            for (String word : vocab) {
                writer.println(word);
            }
        } catch (Exception ignored) {
        }
    }
}
