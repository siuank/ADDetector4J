package me.siuank.addetector4j.learning;

import huzpsb.ll4j.utils.data.*;
import huzpsb.ll4j.model.Model;
import huzpsb.ll4j.nlp.token.Tokenizer;
import huzpsb.ll4j.utils.pair.Pair;

import java.io.File;
import java.util.Collections;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

public class Train {
    static final ExecutorService executor = Executors.newSingleThreadExecutor();
    public static void main(String[] args) throws Exception {
        Scanner scanner;
        Tokenizer tokenizer = Tokenizer.loadFromFile("t1.tokenized.txt");
        assert tokenizer != null : "tokenizer = null!";
        DataSet trainSplit = new DataSet();
        DataSet testSplit = new DataSet();

        scanner = new Scanner(new File("train.txt"));
        trainSplit.split.add(tokenizer.tokenize(0, ""));
        while (scanner.hasNextLine()) {
            String[] line = scanner.nextLine().split("==");
            if (line.length != 2) continue;
            int type = Integer.parseInt(line[1]);
            trainSplit.split.add(tokenizer.tokenize(type, line[0]));
            if (type == 1 && ThreadLocalRandom.current().nextBoolean()) {
                trainSplit.split.add(tokenizer.tokenize(type, line[0]));
            }
        }
        scanner.close();

        scanner = new Scanner(new File("test.txt"));
        while (scanner.hasNextLine()) {
            String[] line = scanner.nextLine().split("==");
            if (line.length != 2) continue;
            int type = Integer.parseInt(line[1]);
            testSplit.split.add(tokenizer.tokenize(type, line[0]));
        }
        scanner.close();

        Model model = Model.readFrom("anti-ad.model");
        int trainSize = trainSplit.split.size();
        double lr = 5e-6;
        for (int i = 0; i < 256; ++i) {
            Pair<Integer, Integer> pair = model.trainOn(trainSplit, lr);
            double acc = pair.first() * 100.0 / trainSize;
            final int I = i;
            executor.submit(() -> System.out.printf("%s > %s (acc:%.2f%%) %n", I + 1, pair, acc));
            if (acc > 99.3 && lr == 5e-6) {
                executor.submit(() -> System.out.println("update learning rate"));
                lr = 9e-7;
            }
            DataSet set = new DataSet();
            set.split.addAll(model.testAndGetWA(trainSplit).stream().filter(ignored -> ThreadLocalRandom.current().nextBoolean()).toList());
            if (!set.split.isEmpty()) {
                Collections.shuffle(set.split);
                model.trainOn(set, 5e-8);
            }
            // 事实上...我想说什么呢
            executor.submit(() -> model.save("anti-ad.model"));
        }

        System.out.println("Using test data!");
        System.out.println(model.testOn(testSplit));

        executor.shutdown();
        model.save("anti-ad.model");

        System.out.println("-exit-");
    }
}
