package samples.addetector4j;

import huzpsb.ll4j.model.Model;

public class ModelCleaner {
    public static void main(String[] args) {
        // remove test-only layer
        Model.readFrom("anti-ad.model").clean().save("ad-detector.model");
    }
}
