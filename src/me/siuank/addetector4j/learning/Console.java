package me.siuank.addetector4j.learning;

import huzpsb.ll4j.model.Model;
import huzpsb.ll4j.nlp.token.Tokenizer;
import huzpsb.ll4j.utils.data.DataEntry;
import huzpsb.ll4j.utils.data.DataSet;
import huzpsb.ll4j.utils.pair.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.Scanner;

public class Console {
    public static void main(String[] args) throws FileNotFoundException {
        Tokenizer tokenizer = Objects.requireNonNull(Tokenizer.loadFromFile("t1.tokenized.txt"));
        Model model = Model.readFrom("anti-ad.model");

        Scanner scanner = new Scanner(System.in);
        LOOP: while (true) {
            String s = scanner.nextLine();
            String[] split = s.split(" ", 2);
            switch (split[0]) {
                case "exit" -> {
                    break LOOP;
                }
                case "predict" -> System.out.println(model.predict(tokenizer.tokenize(1, split[1])) == 1);
                case "train" -> {
                    String sub = split[1];
                    double learningRate = 1e-5;
                    int epoch = 50;
                    int label = 1;
                    TRAIN: while (true) {
                        String[] subSp = sub.split(" ", 2);
                        String[] subSub = new String[] {" "};
                        if (subSp.length == 2) subSub = subSp[1].split(" ", 2);
                        switch (subSp[0]) {
                            case "lr" -> learningRate = Double.parseDouble(subSub[0]);
                            case "epoch" -> epoch = Integer.parseInt(subSub[0]);
                            case "label" -> label = Integer.parseInt(subSub[0]);
                            case "text" -> {
                                DataSet set = new DataSet();
                                String text = subSp[1];
                                System.out.printf("training with learningRate %s label %d epoch %d text %s%n", learningRate, label, epoch, text);

                                DataEntry tokenized = tokenizer.tokenize(label, text);
                                for (int i = 0; i < epoch; i++) {
                                    set.split.add(tokenized);
                                }
                                model.trainOn(set, learningRate);
                                System.out.printf("result: %s%n", model.predict(tokenized) == label ? "success" : "failed");
                                model.save("anti-ad.model");
                                break TRAIN;
                            }
                        }
                        if (subSub.length == 1) break;
                        sub = subSub[1];
                    }
                }
                case "test" -> {
                    DataSet set = new DataSet();
                    try (Scanner scanner1 = new Scanner(new File("test.txt"))) {
                        while (scanner1.hasNext()) {
                            String[] data = scanner1.nextLine().split("==");
                            DataEntry tokenized = tokenizer.tokenize(Integer.parseInt(data[1]), data[0]);
                            set.split.add(tokenized);
                        }
                    } catch (Exception e) {
                        System.err.println("exception");
                        e.printStackTrace(System.err);
                        continue;
                    }
                    Pair<Integer, Integer> pair = model.testOn(set);
                    System.out.printf("acc: %.2f | %s%n", pair.first() * 100.0 / set.split.size(), pair);
                }
            }
        }
    }
}
