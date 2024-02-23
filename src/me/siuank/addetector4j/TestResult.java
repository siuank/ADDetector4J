package me.siuank.addetector4j;

import huzpsb.ll4j.model.Model;
import huzpsb.ll4j.nlp.token.Tokenizer;
import huzpsb.ll4j.utils.data.DataEntry;
import huzpsb.ll4j.utils.data.DataSet;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class TestResult {
    // 福利：超老的正则一枚
    public static final Pattern AD_DETECTOR = Pattern.compile("送福利|定制水影|魔改水影|加群.*[0-9]{5,10}|.*内部|\n元|破甲|天花板|工具箱|绕.*更新|绕.*实名|开端|不封号|.* toolbox|替换au|绕过(盒子)vape检测|内部|防封|封号|waibu|外部|.*公益|晋商|禁商|盒子更新后|小号机|群.*[0-9]{5,10}|d{2,4}红利项目|躺赚|咨询(\\+)|捡钱(模式)|(个人)创业|带价私聊|出.*号|裙.*[0-9]{5,10}|君羊.*[0-9]{5,10}|q(:)[0-9]{5,10}|(限时)免费(获取)|.*launcher|3xl?top|.*小卖铺|cpd(d)|暴打|对刀|不服|稳定奔放|qq[0-9]{5,10}|定制.*|小卖铺|老婆不在家(刺激)|代购.*|出.*|vape");
    public static final String TEST_PATHNAME = "test.txt";

    public static void main(String[] args) throws FileNotFoundException {
        System.out.println("Loading model");
        Model model;

        Tokenizer tokenizer = Tokenizer.loadFromFile("t1.tokenized.txt");
        try (Scanner scanner1 = new Scanner(new File("anti-ad.model"))) {
            model = Model.read(scanner1);
        }

//        Compiler.compileSource(model.clean().toScript(), "anti_ad.c");

        if (tokenizer == null) throw new NullPointerException("tokenizer");
        DataSet testSplit = new DataSet();
        HashMap<double[], String> testMap = new HashMap<>();

        try (Scanner scanner = new Scanner(new File(TEST_PATHNAME))) {
            while (scanner.hasNextLine()) {
                String[] line = scanner.nextLine().split("==");
                if (line.length != 2) continue;
                int type = Integer.parseInt(line[1]);
                DataEntry tokenized = tokenizer.tokenize(type, line[0]);
                testSplit.split.add(tokenized);
                testMap.put(tokenized.values, line[0]);
            }
        }
        if (!testSplit.split.isEmpty()) {
            List<DataEntry> entries = model.testAndGetWA(testSplit);
            int totalSize = testSplit.split.size();
            int waSize = entries.size();
            System.out.printf("acc: %.2f%% (wa: %s) %n", (totalSize - waSize) * 100.0 / (totalSize), waSize);
            entries.forEach((de) -> System.out.printf("wrong type (should be %s), text: %s%n", de.type, testMap.get(de.values)));
            entries.clear();
        }

        System.out.println("Success!");

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String text = scanner.nextLine();
            double[] values = tokenizer.tokenize(0, text).values;
            System.out.printf("Matcher: %s, ML: %s%n", sb(AD_DETECTOR.matcher(text).find()), sb(model.predict(values) == 1));
        }
    }

    private static String sb(boolean b) {
        return b ? "+" : "-";
    }
}
