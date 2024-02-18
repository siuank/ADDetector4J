package samples.addetector4j;

import huzpsb.ll4j.utils.data.*;
import huzpsb.ll4j.model.Model;
import huzpsb.ll4j.nlp.token.Tokenizer;
import huzpsb.ll4j.utils.pair.Pair;

import java.io.File;
import java.util.Collections;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class Train {
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

//        Model model = new Model(
//                new DenseLayer(1024, 20)
//                , new LeakyRelu(20)
//                , new DenseLayer(20, 100)
//                , new LeakyRelu(100)
//                , new DenseLayer(100, 2)
//                , new JudgeLayer(2) // MSELoss
//        );

        int size = tokenizer.size();
//        Model model = new Model.ContextBuilder(size)
//                .DenseLayer(20).LeakyReluLayer()
//                .DenseLayer(50).LeakyReluLayer()
//                .DropoutLayer(0.6)
//                .DenseLayer(100).LeakyReluLayer()
//                .DenseLayer(2)
//                .build();
        Model model = Model.readFrom("anti-ad.model");
        System.out.println(size);
        int trainSize = trainSplit.split.size();
        for (int i = 0; i < 128; i++) {
            Pair<Integer, Integer> pair = model.trainOn(trainSplit, 9e-6);
            System.out.printf("%s > %s (acc:%.2f%%) %n", i + 1, pair, pair.first() * 100.0 / trainSize);

//            DataSet set = new DataSet();
//            set.split.addAll(model.testAndGetWA(trainSplit).stream().filter(ignored -> ThreadLocalRandom.current().nextBoolean()).toList());
//            if (!set.split.isEmpty()) {
//                Collections.shuffle(set.split);
//                model.trainOn(set, 5e-9);
//            }
            model.save("anti-ad.model");
        }

        System.out.println("Using test data!");
        System.out.println(model.testOn(testSplit));

        model.save("anti-ad.model");

        System.out.println("-exit-");
    }
}
