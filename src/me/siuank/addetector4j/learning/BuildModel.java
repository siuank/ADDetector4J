package me.siuank.addetector4j.learning;

import huzpsb.ll4j.model.Model;

public class BuildModel {
    public static void main(String[] args) {
        //        Model model = new Model(
//                new DenseLayer(1024, 20)
//                , new LeakyRelu(20)
//                , new DenseLayer(20, 100)
//                , new LeakyRelu(100)
//                , new DenseLayer(100, 2)
//                , new JudgeLayer(2) // MSELoss
//        );
        new Model.ContextBuilder(2048 + 1) // +1 for char size
                .DenseLayer(20).LeakyReluLayer()
                .DenseLayer(96).LeakyReluLayer()
                .DropoutLayer(0.6)
                .DenseLayer(50).LeakyReluLayer()
                .DenseLayer(2)
                .build()
                .save("anti-ad.model");
    }
}
