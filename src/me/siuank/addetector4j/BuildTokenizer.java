package me.siuank.addetector4j;

import huzpsb.ll4j.nlp.token.Tokenizer;
import huzpsb.ll4j.nlp.token.TokenizerBuilder;

import java.io.File;
import java.util.Scanner;

public class BuildTokenizer {
    public static void main(String[] args) throws Exception {
        TokenizerBuilder tb = new TokenizerBuilder(2048);
        tb.minLength = 1;
        tb.checkRepeat = true;
        try (Scanner scanner = new Scanner(new File("train.txt"))) {
            while (scanner.hasNextLine()) {
                String[] split = scanner.nextLine().split("==");
                if (split.length == 2) {
                    tb.update(split[0]);
                }
            }
        }
        // from jie-ba
        try (Scanner scanner = new Scanner("words.txt")) {
            while (scanner.hasNextLine()) {
                String text = scanner.nextLine().split("\t")[0];
                for (int i = 0; i < 15; i++) {
                    tb.update(text);
                }
            }
        }
        Tokenizer t1 = tb.build();
        t1.saveToFile("t1.tokenized.txt");
    }
}
