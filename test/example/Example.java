package example;

import ll4j.products.addetector.ADDetector;
import me.siuank.addetector.AdvertisementDetector;

import java.io.*;

public class Example {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("ad-detector.model"));
        ADDetector detector = new ADDetector(ADDetector.Tokenizer.loadFromFile(new File("t1.tokenized.txt")), reader.lines().toArray(String[]::new));
        reader.close();

//        System.out.println(detector.predict("123456"));

        AdvertisementDetector advertisementDetector = new AdvertisementDetector(detector);
        AdvertisementDetector.Violation check = advertisementDetector.check("-Pearon Config 『小鑫&Toto再现辉煌』 『凯瑞小卖部代理出售』 已绕过花雨庭 起床&天坑~  去年 我们以远超其他内部的 暴力稳定 成功登顶T1 现在我们将再现辉煌-内部优势: 杀戮依旧和一年前一样强悍 3.9格拷打残疾配置 搭路稳定丝滑不掉 noxz保证你无击退 纹丝不动 blink随意瞬移不回弹 稳定4h未封 其余该有都有不再展示HYT最大卡网丨凯瑞.shop Buy+551254294 公益819492250T1质量 无需多言");
        System.out.printf("vl: %s, checks=%s %n", check.getVl(), check.getFlaggedChecks());
    }
}
