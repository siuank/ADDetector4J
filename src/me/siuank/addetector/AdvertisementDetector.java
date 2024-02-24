package me.siuank.addetector;

import ll4j.products.addetector.ADDetector;

import java.util.HashSet;
import java.util.Set;

// just for fun xD
public class AdvertisementDetector {
    private final ADDetector heuDetector;

    public AdvertisementDetector(ADDetector detector) {
        this.heuDetector = detector;
    }

    public Violation check(String text) {
        Violation violation = new Violation(text);
        if (text.length() > 1024) {
            violation.flag(10, "ad.length.limit");
        }
        if (text.split("\n").length > 20) {
            violation.flag(10, "ad.line.limit");
        }
        text = text.replaceAll("\n", "");

        if (text.matches(".*[0-9]{5,11}.*")) {
            violation.flag(2, "ad.q_number");
        }


        if (heuDetector != null && heuDetector.predict(text)) {
            violation.flag(25, "ad.heu");
        }
        return violation;
    }

    public static class Violation {
        public final String text;
        double vl = 0;
        Set<String> flaggedChecks = new HashSet<>();
        public Violation(String text) {
            this.text = text;
        }

        public double getVl() {
            return vl;
        }

        public void flag(double vl, String check) {
            this.vl += vl;
            flaggedChecks.add(check);
        }

        public Set<String> getFlaggedChecks() {
            return flaggedChecks;
        }
    }
}
