package samples.addetector4j;

import huzpsb.ll4j.model.Model;
import huzpsb.ll4j.nlp.token.Tokenizer;
import huzpsb.ll4j.utils.data.DataEntry;
import huzpsb.ll4j.utils.data.DataSet;

import java.io.File;
import java.util.*;

// what
public class Tune {
    public static void main(String[] args) throws Exception {
        Tokenizer tokenizer = Objects.requireNonNull(Tokenizer.loadFromFile("t1.tokenized.txt"));

        Model model = Model.readFrom("anti-ad.model");
        DataSet set = new DataSet();
        try (Scanner scanner = new Scanner(Objects.requireNonNull(Tune.class.getResourceAsStream("/samples/addetector4j/ads.txt")))) {
            while (scanner.hasNextLine()) {
                set.split.add(tokenizer.tokenize(1, scanner.nextLine()));
            }scanner.close();
        }

        model.trainOn(set, 5e-8);
        model.save("anti-ad.model");
    }
}
