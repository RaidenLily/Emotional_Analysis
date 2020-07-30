# Emotional_Analysis

舆情（情感）分析  

原本是为了一个比赛而开发的功能，最后因为某些原因放弃加进去了，可惜了。

该项目使用Java语言，也使用了ansj分词库，结合了《基于词典和规则集的中文微博情感分析》这篇论文的部分内容，词库使用了大连理工大学信息检索研究室的中文情感词汇本体库。

由于时间比较紧，所以弄得比较简单，准确度也比较欠缺，本来4月份弄好的，然后有事忙到8月份才回来看这项目，当初自己写的代码又没有写注释，结果现在看自己写的代码都有点懵...

如果各位有相关比赛可以拿过去凑数玩玩，记得给颗星星......

***

**说一下大体思路：**   

1.对给的内容进行分句，按句号，问号和感叹号分别分成陈述句，疑问句和感叹句。  
2.分别对这三种句子再进行分句：按逗号分成一小句一小句那样。  
3.对步骤2分好的句子进行ansj分词处理，然后对分好的词语跟词库里面的进行匹配，记录其极性，情感值等信息。  
4.根据分好的词中判断是否有转折语气，分别处理。  
5.对所有的句子进行分析完后，由于陈述句，疑问句和感叹句表达的情感强度不同，故对不同句子的情感值进行加权。  
6.最后对所有结果进行相加，得出的值按自己设定的范围值来判断是否是中性，积极，消极等。  
具体思路可能有些区别，但是也不大，隔太久我都忘了自己写的逻辑了...

***

运行直接运行Test类就好了

**运行结果：**

![图片加载失败](https://github.com/RaidenLily/Emotional_Analysis/blob/master/image.png)  

***

论文也上传了，有需要可以看看，这个项目并不是完全按照论文来实现的，可以说挑了点简单的来实现，能基本进行舆情（情感）分析。
