package ll4j.products.addetector;

import java.io.*;
import java.util.Scanner;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class ADDetector {
    private final Tokenizer tokenizer;
    private final String[] script;
    private Pattern pattern = null;

    public ADDetector(Tokenizer tokenizer, String script) {
        this.tokenizer = tokenizer;
        this.script = script.split("\n");
    }

    public ADDetector(Tokenizer tokenizer, String[] script) {
        this.tokenizer = tokenizer;
        this.script = script;
    }

    public boolean predict(String str) {
        return patternPredict(str) || predict(tokenizer.tokenize(str), script) == 1;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public boolean patternPredict(String str) {
        return pattern != null && pattern.matcher(str).find();
    }

    static int predict(double[] input, String[] script) {
        double[] current = new double[input.length];
        System.arraycopy(input, 0, current, 0, input.length);
        for (String str : script) {
            if (str.length() < 2) {
                continue;
            }
            String[] tokens = str.split(" ");
            switch (tokens[0]) {
                case "D":
                    int ic = Integer.parseInt(tokens[1]);
                    int oc = Integer.parseInt(tokens[2]);
                    if (current.length != ic) {
                        throw new RuntimeException("Wrong input size for Dense layer (expected " + ic + ", got " + current.length + ")");
                    }
                    double[] tmp = new double[oc];
                    for (int i = 0; i < oc; i++) {
                        double sum = 0;
                        for (int j = 0; j < ic; j++) {
                            sum += current[j] * Double.parseDouble(tokens[3 + i + j * oc]);
                        }
                        tmp[i] = sum;
                    }
                    current = tmp;
                    break;
                case "L": {
                    int n = Integer.parseInt(tokens[1]);
                    if (current.length != n) {
                        throw new RuntimeException("Wrong input size for LeakyRelu layer (expected " + n + ", got " + current.length + ")");
                    }
                    for (int i = 0; i < n; i++) {
                        current[i] = current[i] > 0 ? current[i] : current[i] * 0.01;
                    }
                    break;
                }
                case "S": {
                    int n = Integer.parseInt(tokens[1]);
                    if (current.length != n) {
                        throw new RuntimeException("Wrong input size for Sigmoid layer (expected " + n + ", got " + current.length + ")");
                    }
                    for (int i = 0; i < n; i++) {
                        current[i] = 1 / (1 + Math.exp(-current[i]));
                    }
                    break;
                }
                case "Th": {
                    int n = Integer.parseInt(tokens[1]);
                    if (current.length != n) {
                        throw new RuntimeException("Wrong input size for Tanh layer (expected " + n + ", got " + current.length + ")");
                    }
                    for (int i = 0; i < n; i++) {
                        current[i] = Math.tanh(current[i]);
                    }
                    break;
                }
                case "Softmax": {
                    int n = Integer.parseInt(tokens[1]);
                    if (current.length != n) {
                        throw new RuntimeException("Wrong input size for Softmax layer (expected " + n + ", got " + current.length + ")");
                    }
                    double sum = 0;
                    for (int i = 0; i < n; i++) {
                        sum += Math.exp(current[i]);
                    }
                    for (int i = 0; i < n; i++) {
                        current[i] = Math.exp(current[i]) / sum;
                    }
                    break;
                }
                case "J":
                    int m = Integer.parseInt(tokens[1]);
                    if (current.length != m) {
                        throw new RuntimeException("Wrong input size for Judge layer (expected " + m + ", got " + current.length + ")");
                    }
                    int idx = 0;
                    for (int i = 1; i < m; i++) {
                        if (current[i] > current[idx]) {
                            idx = i;
                        }
                    }
                    return idx;
                case "Dropout":
                    break;
                default:
                    throw new RuntimeException("Unknown layer[minRt] type");
            }
        }
        throw new RuntimeException("No output layer");
    }

    public static class Tokenizer {
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
                    vocab[i] = regularize(scanner.nextLine());
                }
                return new Tokenizer(vocab);
            } catch (Exception ignored) {}
            return null;
        }

        public double[] tokenize(String text) {
            String regularized = regularize(text);
            double[] values = new double[vocab.length + 1];
            values[0] = text.length();
            for (int i = 0; i < vocab.length; i++) {
                values[i + 1] = regularized.contains(vocab[i]) ? 1 : 0;
            }
            return values;
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
        static char regularize(char input) {
            if (input == 0x3000) {
                return 32;
            }
            else if (input > 0xff00 && input < 0xff5f) {
                return (char) (input - 0xfee0);
            }
            else if (input >= 'A' && input <= 'Z') {
                input += 32;
            }
            return input;
        }

        static String regularize(String input) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < input.length(); i++) {
                char c = regularize(input.charAt(i));
                if (ccFind(c)) sb.append(c);
            }
            return sb.toString();
        }

        static boolean ccFind(char ch) {
            if (isChineseLetter(ch))
                return true;
            if (isEnglishLetter(ch))
                return true;
            if (isDigit(ch))
                return true;
            return isConnector(ch);
        }
        static final char[] connectors = new char[] { '+', '#', '&', '.', '_', '-' };


        static boolean isChineseLetter(char ch) {
            return ch >= 0x4E00 && ch <= 0x9FA5;
        }


        static boolean isEnglishLetter(char ch) {
            return (ch >= 0x0041 && ch <= 0x005A) || (ch >= 0x0061 && ch <= 0x007A);
        }


        static boolean isDigit(char ch) {
            return ch >= 0x0030 && ch <= 0x0039;
        }


        static boolean isConnector(char ch) {
            for (char connector : connectors)
                if (ch == connector)
                    return true;
            return false;
        }
    }
}
