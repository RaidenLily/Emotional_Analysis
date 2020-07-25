package com;

import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import com.util.getData;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Split {

    private static HashMap<String,Float> level_advMap=new HashMap<>();
    private static HashMap<String,EmotionalWords> emotionalWordsHashMap=new HashMap<>();
    private static HashMap<String,Integer> transitionHashMap=new HashMap<>();
    private static HashSet<String> rhetorical=new HashSet<>();

    //程度词
    private final static String EXTREME_PATH="extreme.txt";
    private final static String VERY_PATH="very.txt";
    private final static String MORE_PATH="more.txt";
    private final static String ISH_PATH="ish.txt";
    private final static String INSUFFICIENTLY_PATH="insufficiently.txt";
    private final static String DENY_ADV_PATH="deny_adv.txt";

    //情感词汇
    private final static String EmotionalWords_PATH="Affective lexicon.xlsx";

    public static void doAll(String content) throws IOException {

        getData();

        //疑问句
        ArrayList<String> question=new ArrayList<>();
        //感叹句
        ArrayList<String> exclamatory=new ArrayList<>();
        //陈述句
        ArrayList<String> declarative=new ArrayList<>();

        //对给的字段进行分句
        SplitComplexSentences(content,question,exclamatory,declarative);

        int size1=question.size();
        int size2=exclamatory.size();
        int size3=declarative.size();

        float val=0;

        //对疑问句进行分析
        for (int i=0;i<size1;i++){
            ArrayList<String> clauseList = SplitClause(question.get(i));
            ArrayList<ClauseVal> clauseValList=new ArrayList<>();
            for(int t=0;t<clauseList.size();t++){
/*                clauseValList.add(ClauseVal(clauseList.get(t),clauseValList));*/
                clauseValList.add(ClauseVal(clauseList.get(t)));
            }
            val = val+countAllClauseVal(clauseValList);
        }

        //对感叹句进行分析
        for (int i=0;i<size2;i++){
            ArrayList<String> clauseList = SplitClause(exclamatory.get(i));
            ArrayList<ClauseVal> clauseValList=new ArrayList<>();
            for(int t=0;t<clauseList.size();t++){
                clauseValList.add(ClauseVal(clauseList.get(t)));
                /*ClauseVal(clauseList.get(t),clauseValList);*/
            }
            val = (float) (val+countAllClauseVal(clauseValList)*1.5);
        }

        //对陈述句进行分析
        for (int i=0;i<size3;i++){
            ArrayList<String> clauseList = SplitClause(declarative.get(i));
            ArrayList<ClauseVal> clauseValList=new ArrayList<>();
            for(int t=0;t<clauseList.size();t++){
                clauseValList.add(ClauseVal(clauseList.get(t)));
                /*ClauseVal(clauseList.get(t),clauseValList);*/
            }
            val = val+countAllClauseVal(clauseValList);
        }

        //情感结果
        System.out.println("情感值为："+val);
    }


    //对所给的字段进行一个句子一个句子的分割
    public static void SplitComplexSentences(String content,ArrayList<String> question,ArrayList<String> exclamatory,ArrayList<String> declarative){

        int index = 0;

        for (int i=0;i<content.length();i++){
            char temp=content.charAt(i);

/*            if(temp=='【'){
                index=i;
                continue;
            }*/
            if(temp=='？'||temp=='?'){
                String str = content.substring(index,++i);
                question.add(str);
                index=i;
            }else if(temp=='！'||temp=='!'){
                String str = content.substring(index,++i);
                exclamatory.add(str);
                index=i;
            }else if(temp=='。'|temp=='.'){
                String str = content.substring(index,++i);
                declarative.add(str);
                index=i;
            }else if(i==content.length()-2){
                String str = content.substring(index,++i);
                declarative.add(str);
            }
        }
    }

    //对分好的句子按逗号进行分句
    public static ArrayList<String> SplitClause(String complexSentences){

        int index = 0;

        ArrayList ClauseList = new ArrayList<String>();

        for (int i=0;i<complexSentences.length();i++){
            char temp=complexSentences.charAt(i);

            if(temp=='，'|temp==','){
                String str = complexSentences.substring(index,++i);
                ClauseList.add(str);
                index=i;
            }else if(i==complexSentences.length()-1){
                String str = complexSentences.substring(index,++i);
                ClauseList.add(str);
            }
        }
        return ClauseList;
    }

    //对句子进行分析
    public static ClauseVal ClauseVal(String clause){
        HashSet<String> testMap=new HashSet<>();
        Result a= ToAnalysis.parse(clause);
        ClauseVal clauseVal=new ClauseVal();
        Iterator<Term> segTerms=a.iterator();
        float sen=0;
        float temp=1;

        boolean rhetoricalSignal=false;

        while (segTerms.hasNext()) {
            Term tm = segTerms.next();
            String strNs = tm.getNatureStr();//获取词性
            if (strNs == "null") continue;
            char cns = strNs.charAt(0);//取词性第一个字母
            if (cns == 'n' || cns=='a'||cns=='i' || cns=='v'||cns=='j') {
                EmotionalWords emotionalWords = emotionalWordsHashMap.get(tm.getName());
                if (emotionalWords!=null){
/*                    System.out.println(tm.getName()+": "+"极性："+emotionalWords.getPolarity()+"强度："+emotionalWords.getEmotionalIntensity());*/
                    //判断是否负面词汇
                    if(emotionalWords.getPolarity()==2){
                        testMap.add(tm.getName());
                        sen=sen+emotionalWords.getEmotionalIntensity()*temp*-1;
                    }else {
                        testMap.add(tm.getName());
                        sen=sen+emotionalWords.getEmotionalIntensity()*emotionalWords.getPolarity()*temp;
                    }
                }
                //判断程度修饰和反问句
            }else if(cns=='d'||cns=='l'){
                Float value = level_advMap.get(tm.getName());
                if (value!=null){
                    testMap.add(tm.getName());
/*                    System.out.println(tm.getName()+"副词: "+value);*/
                    temp=temp*value;
                }else if(rhetorical.contains(tm.getName())) {
/*                    System.out.println(tm.getName());*/
                    rhetoricalSignal=true;
                }
                //判断转折句
            }else if(cns=='c'){
                Integer value=transitionHashMap.get(tm.getName());
                if (value!=null){
                    clauseVal.setSignal(value);
                }
                //判断反问句
            }else if(cns=='y'){
                if(rhetorical.contains(tm.getName())) {
                    rhetoricalSignal=true;
                }
            }
        }
        if(rhetoricalSignal){
            sen=sen*-1;
        }

        clauseVal.setSen(sen);
        return clauseVal;
    }

    //计算结果
    private static float countAllClauseVal(ArrayList<ClauseVal> clauseValList){

        float val=0;

        ArrayList<Integer> temp=new ArrayList<>();
        for(int i=0;i<clauseValList.size();i++){
            if(clauseValList.get(i).getSignal()!=0){
                temp.add(clauseValList.get(i).getSignal());
            }
        }
        if(temp.size()>2||temp.size()==0){
            for (int i=0;i<clauseValList.size();i++){
                val=val+clauseValList.get(i).getSen();
            }
        }else if (temp.size()==1){
            if (temp.get(0)==1){
                for (int i=0;i<clauseValList.size();i++){
                    if(clauseValList.get(i).getSignal()==0)
                        val=val+clauseValList.get(i).getSen();
                    else
                        break;
                }
            }else {
                boolean b=false;
                for (int i=0;i<clauseValList.size();i++){
                    if (clauseValList.get(i).getSignal()==2)
                        b=true;
                    if(b){
                        val=val+clauseValList.get(i).getSen();
                    }
                }
            }
        }else {
            boolean b=false;
            if(!temp.get(0).equals(temp.get(1))){
                for (int i=0;i<clauseValList.size();i++){
                    if (clauseValList.get(i).getSignal()==2){
                        b=true;
                    }
                    if(b){
                        val=val+clauseValList.get(i).getSen();
                    }
                }
            }
        }
        return val;
    }

    //移除网页标签
    public static String htmlRemoveTag(String htmlStr) {
        final String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // 定义script的正则表达式
        final String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // 定义style的正则表达式
        final String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
        final String regEx_space = "\\s*|\t|\r|\n";// 定义空格回车换行符
        final String regEx_w = "<w[^>]*?>[\\s\\S]*?<\\/w[^>]*?>";//定义所有w标签

        Pattern p_w = Pattern.compile(regEx_w, Pattern.CASE_INSENSITIVE);
        Matcher m_w = p_w.matcher(htmlStr);
        htmlStr = m_w.replaceAll(""); // 过滤script标签


        Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(htmlStr);
        htmlStr = m_script.replaceAll(""); // 过滤script标签


        Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_style.matcher(htmlStr);
        htmlStr = m_style.replaceAll(""); // 过滤style标签


        Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(htmlStr);
        htmlStr = m_html.replaceAll(""); // 过滤html标签


        Pattern p_space = Pattern.compile(regEx_space, Pattern.CASE_INSENSITIVE);
        Matcher m_space = p_space.matcher(htmlStr);
        htmlStr = m_space.replaceAll(""); // 过滤空格回车标签


        htmlStr = htmlStr.replaceAll(" ", ""); //过滤
        return htmlStr.trim(); // 返回文本字符串
    }

    //读取各种文件
    public static void getData() throws IOException {
        File directory = new File("");
        String path =directory.getAbsolutePath();
        String PATH=path+"\\src\\main\\resources\\file\\";

        getData.getDataFromtxt(PATH+EXTREME_PATH,level_advMap,2.0f);
        getData.getDataFromtxt(PATH+VERY_PATH,level_advMap,1.5f);
        getData.getDataFromtxt(PATH+MORE_PATH,level_advMap,0.8f);
        getData.getDataFromtxt(PATH+ISH_PATH,level_advMap,0.5f);
        getData.getDataFromtxt(PATH+INSUFFICIENTLY_PATH,level_advMap, 0.2f);
        getData.getDataFromtxt(PATH+DENY_ADV_PATH,level_advMap,-1);

        //临时加进去的转折词汇
        emotionalWordsHashMap=getData.getDataFromExcel(PATH+EmotionalWords_PATH);
        transitionHashMap.put("虽然",1);
        transitionHashMap.put("但是",2);
        transitionHashMap.put("但",2);

        //疑问词汇
        rhetorical.add("难道");
        rhetorical.add("吗");
    }


    //用来写入自定义分词的字典的
    public static void writeData(){
        try {
            File writename = new File("library\\default.dic"); // 自定义分词的字典
            BufferedWriter out = new BufferedWriter(new FileWriter(writename));
            for(Map.Entry<String,EmotionalWords> entry: emotionalWordsHashMap.entrySet())
            {
                String word=entry.getKey();
                String PartSpeech=entry.getValue().getPartSpeech();
                if(PartSpeech.equals("adj")){
                    out.write(word+"\t"+"a"+"\t"+"1000"+"\n");
                }else if(PartSpeech.equals("noun")){
                    out.write(word+"\t"+"n"+"\t"+"1000"+"\n");
                }else if(PartSpeech.equals("verb")){
                    out.write(word+"\t"+"v"+"\t"+"1000"+"\n");
                }else if(PartSpeech.equals("idiom")){
                    out.write(word+"\t"+"i"+"\t"+"1000"+"\n");
                }
                out.flush();
            }// 把缓存区内容压入文件
            out.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
