package me.siuank.addetector4j.learning;

import huzpsb.ll4j.model.Model;
import huzpsb.ll4j.nlp.token.Tokenizer;
import huzpsb.ll4j.utils.data.DataSet;

import java.util.*;

// what
public class Tune {
    public static void main(String[] args) throws Exception {
        Tokenizer tokenizer = Objects.requireNonNull(Tokenizer.loadFromFile("t1.tokenized.txt"));

        Model model = Model.readFrom("anti-ad.model");
        DataSet set = new DataSet();
        trainADS(set, tokenizer, model);

        set.split.clear();
        trainChatting(set, tokenizer, model);

        set.split.clear();
        System.out.println("-");
        singleLetter(set, tokenizer, model);
    }

    private static void trainChatting(DataSet set, Tokenizer tokenizer, Model model) {
        read("/me/siuank/addetector4j/learning/chatting.txt", set, tokenizer, 0);
        System.out.println("others tune");
        model.trainOn(set, 5e-8);
        model.save("anti-ad.model");
    }

    private static void trainADS(DataSet set, Tokenizer tokenizer, Model model) {
        System.out.println("ads tune");
        read("/me/siuank/addetector4j/learning/ads.txt", set, tokenizer, 1);
        model.trainOn(set, 5e-8);
        model.save("anti-ad.model");
    }

    private static void read(String name, DataSet set, Tokenizer tokenizer, int type) {
        try (Scanner scanner = new Scanner(Objects.requireNonNull(Tune.class.getResourceAsStream(name)))) {
            while (scanner.hasNextLine()) {
                set.split.add(tokenizer.tokenize(type, scanner.nextLine()));
            }scanner.close();
        }
    }

    private static void singleLetter(DataSet set, Tokenizer tokenizer, Model model) {
        // chinese letter
        for (char ch = 0x4E00; ch <= 0x9FA5; ++ch) {
            set.split.add(tokenizer.tokenize(0, String.valueOf(ch)));
        }
        set.split.add(tokenizer.tokenize(0, ""));
        model.trainOn(set, 5e-6);

        model.save("anti-ad.model");
    }
}
