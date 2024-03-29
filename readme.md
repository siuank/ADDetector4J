# ADDetector4J

(因为有人想用所以把整个项目分离了出来.png)

### 本项目使用[LL4J](https://www.github.com/LL4J/LL4J)作训练与预测框架
部分内容有所改动，因此开源完整修改版。如果改动部分出现异常表现，请在此处提交issue

# 关于模型
### 最新更新：2/25/2024
accuracy: 98.40% (test) 98.74% (train)
- 更新数据集
- 更新模型

训练数据集中的广告主要来自MC作弊圈，部分来自于其他地方收集

## 缺陷
- 未知


## 如果我需要在其他地方使用该模型
- 保留关键词库与模型文件，将源代码文件中的`ll4j.products.addetector.ADDetector.java`提取并加入到您的项目中
- 使用`ADDetector`类即可（详见`test.example.Example.java`）

### 特殊的建议
- QQ群聊
  - 建议仅撤回被标记的信息，当且仅当连续被标记时才执行进一步的操作

## 训练
### 如果是自己训练，按顺序运行（均为main函数)
- Wash.java
- BuildTokenizer.java
- BuildModel.java
- Train.java
- Tune.java (如果需要)
- ModelCleaner.java (清除Dropout层)

### 如果是使用预训练模型，但是自行微调
- 请使用 Tune.java

### 使用模型预测或者审阅训练结果
- 请移步 TestResult.java

## 文件
### 预测用：
| 文件名               | 用途                                                          |
|-------------------|-------------------------------------------------------------|
| ad-detector.model | 移除Dropout层后的模型文件<br/>可直接在LL4J中使用                            |
| anti-ad.model     | 训练/测试用模型文件<br/>除非你自己加Dropout层，否则请使用ad-detector.model进行训练等操作 |
| t1.tokenized.txt  | 词库文件                                                        |

正常来说，预测只需要`ad-detector.model`, `t1.tokenized.txt`这两个资源文件即可

### 训练用：
| 文件名             | 用途                                                                      |
|-----------------|-------------------------------------------------------------------------|
| mc_qq_group.txt | 数据集 (由[hsn8086](https://www.github.com/hsn8086/)整理，我们进行了些增删操作以保证一定的时效性) |
| train.txt       | 训练集（由Wash.java打乱数据集生成）                                                  |
| test.txt        | 测试集（由Wash.java打乱数据集生成）                                                  |

### 一些额外的内容：
| 文件名                   | 用途                                              |
|-----------------------|-------------------------------------------------|
| ads.txt               | 对数据集进行一些额外的补充，供微调数据集用                           |
| ads-special-regex.txt | 一些特殊规则，用于直接过滤一些不适合进入数据集的广告（如同音字替换等）<br/>相当于特征库？ |

### 效果展示：
`Matcher`为项目[LingBot](https://github.com/LingBot-Project/LingBot)提供的正则表达式的匹配结果，`ML`为模型预测结果

在测试前，这些数据均未被加入到训练集中训练

```
Matcher: +, ML: +
Frank开端工具箱开启发售，现在购买只需要10元...
Matcher: +, ML: +
----------- Atry -----------[改进]☆[K...
Matcher: +, ML: +
kw。v2mcfsip。top专做mc账号，超级权威& 1...
Matcher: +, ML: +
------------------------Crush-------...
Matcher: +, ML: +
需要绕ipban/过1+加速ip，超低价of披风：→sa...
Matcher: +, ML: +
Proton公益工具箱已更新*去除盒子烦人的新手教...
Matcher: +, ML: +
Fuguas内部回归！[+]暴力杀戮入侵花雨庭[+]半...
Matcher: +, ML: +
WNF全脱离盒子 天卡 月卡 周卡 永久卡花雨庭10级...
Matcher: -, ML: +
你说你没有稳定的工艺配置？你说你没有好用的开端...
Matcher: +, ML: +
什么？owner工艺裙居然公开了noslow killaur...
Matcher: +, ML: +
纪圈，云集各路神仙大佬进群后有大哥罩着你，在外...
Matcher: +, ML: +
新春特价活动【+】vpn永久：60r【+】加速ip月卡...
Matcher: +, ML: +
长期稳定xgpu激活码供应各种区都有，淘宝咸鱼代...
Matcher: -, ML: +
出b站年度大会员 40r
Matcher: +, ML: +
超多好物都在这wqkj777.top
Matcher: -, ML: -                      // 漏报
卡
Matcher: -, ML: +                      // 单字误报
我网卡了
Matcher: -, ML: -
等卡
Matcher: -, ML: -
卡了
Matcher: -, ML: -
卡网上新...
Matcher: -, ML: +
急
Matcher: -, ML: +                      // 单字误报
急急急
Matcher: -, ML: +                      // 误报（只存在单个关键词）
急了
Matcher: -, ML: -
防止这条消息发送失败，发送1000次
Matcher: -, ML: -
前置protocollib
Matcher: -, ML: -
撤回有用吗你
Matcher: -, ML: -
起床！别过少爷生活！起床！别过少爷生活！起床...
Matcher: -, ML: -
```

## 其他
`me.siuank.addetector.AdvetisementDetector`: 写着玩的东西

# 鸣谢
* huzpsb
    * 机器学习指导
    * LL4J制作
* hsn8086
    * 制作数据集
* loyisa
    * 部分样本提供
* guimc@ltd
    * 提供测试集中的正则表达式

### 模型和数据集会不定期更新

gradle？maven package？拉倒吧

# 开源许可
### 本项目基于MIT协议开源，请在使用时务必保留版权信息
