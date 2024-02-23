package me.siuank.addetector4j;

import huzpsb.ll4j.nlp.token.CharUtils;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Wash {
    public static void main(String[] args) throws Exception {
        List<String> contents = new ArrayList<>();
        Scanner scanner = new Scanner(new File("mc_qq_group.txt"));
        while (scanner.hasNextLine()) {
            String[] line = scanner.nextLine().split("==");
            if (line.length != 2) {
                continue;
            }
            if (!(line[1].equals("0") || line[1].equals("1"))) {
                continue;
            }
            contents.add(CharUtils.regularize(line[0]) + "==" + line[1]);
        }
        scanner.close();
        Collections.shuffle(contents);
        String[] train = contents.toArray(new String[0]);
        int trainSize = (int) (train.length * 0.8);
        PrintWriter trainWriter = new PrintWriter("train.txt");
        for (int i = 0; i < trainSize; i++) {
            trainWriter.println(train[i]);
        }
        trainWriter.close();
        PrintWriter testWriter = new PrintWriter("test.txt");
        for (int i = trainSize; i < train.length; i++) {
            testWriter.println(train[i]);
        }
        testWriter.close();
    }
}
