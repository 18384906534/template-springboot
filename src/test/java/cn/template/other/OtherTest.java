package cn.template.other;

import cn.template.CommonTest;
import cn.template.entity.Question;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.Test;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author licq
 * @version 1.0
 * @date 2025/10/15 11:10
 */
public class OtherTest extends CommonTest {


    @Test
    public void readJsonData() throws Exception {
         /*String mathPrefix = "15040习思想概论-";
        String directory = "D:\\Users\\86131\\Desktop\\自考-整理\\2510自考整理\\题集\\15040习思想概论\\";*/
        String mathPrefix = "15043中近现史纲要-";
        String directory = "D:\\Users\\86131\\Desktop\\自考-整理\\2510自考整理\\题集\\15043中近现史纲要\\";
        File resourcesDirectory = new File(directory);
        if(!resourcesDirectory.exists() || !resourcesDirectory.isDirectory()){
            throw new Exception("资源不存在!");
        }

        List<File> mathFileList = Arrays.stream(resourcesDirectory.listFiles())
                .filter(file -> file.getName().contains(mathPrefix))
                .collect(Collectors.toList());
        if (mathFileList == null || mathFileList.isEmpty()) {
            throw new Exception("无匹配资源!");
        }
        Map<Long, List<String>> duplicateQuestionsMap = mathFileList.stream()
                .map(file -> {
                    System.out.println(file.getName());
                    String fileJsonString = fileJsonStringReader(file.getAbsolutePath());
                    if (StringUtils.isEmpty(fileJsonString)) {
                        return null;
                    }
                    JSONObject dataJSONObject = JSONObject.parseObject(fileJsonString);

                    JSONObject dataObject = dataJSONObject.getJSONObject("data");
                    JSONArray groupsJSONArray = dataObject.getJSONArray("groups");
                    String paperName = dataObject.getString("paperName");

                    // 返回一个Map，包含试卷名称和该试卷中的所有题目ID
                    Map<String, List<Long>> paperQuestions = new HashMap<>();
                    List<Long> questionIds = groupsJSONArray.stream()
                            .map(jsonArray -> JSONObject.parseObject(JSONObject.toJSONString(jsonArray)).getJSONArray("questions"))
                            .flatMap(Collection::stream).map(JSONObject::toJSONString)
                            .map(json -> {
                                JSONObject jsonObject = JSONObject.parseObject(json);
                                return jsonObject.getLong("id");
                            })
                            .collect(Collectors.toList());

                    paperQuestions.put(paperName, questionIds);
                    return paperQuestions;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList())
                .stream()
                // 将所有试卷的题目合并，找出重复的题目
                .flatMap(paperMap -> paperMap.entrySet().stream())
                .flatMap(entry -> entry.getValue().stream()
                        .map(questionId -> new AbstractMap.SimpleEntry<>(questionId, entry.getKey())))
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey, // 按题目ID分组
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList()) // 收集包含该题目的试卷名称
                ))
                .entrySet().stream()
                // 只保留重复的题目（出现在多个试卷中的题目）
                .filter(entry -> entry.getValue().size() > 1)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));

        // 打印重复题目及其所在的试卷
        duplicateQuestionsMap.forEach((questionId, paperNames) -> {
            System.out.println("题目ID " + questionId + " 出现在以下试卷中: " + paperNames);
        });
    }


    @Test
    public void readFilePaperName() throws Exception {
        String mathPrefix = "15040习思想概论-";
        String directory = "D:\\Users\\86131\\Desktop\\自考-整理\\2510自考整理\\题集\\15040习思想概论\\";
        /*String mathPrefix = "15043中近现史纲要-";
        String directory = "D:\\Users\\86131\\Desktop\\自考-整理\\2510自考整理\\题集\\15043中近现史纲要\\";*/
        File resourcesDirectory = new File(directory);
        if(!resourcesDirectory.exists() || !resourcesDirectory.isDirectory()){
            throw new Exception("资源不存在!");
        }

        List<File> mathFileList = Arrays.stream(resourcesDirectory.listFiles())
                .filter(file -> file.getName().contains(mathPrefix))
                .collect(Collectors.toList());
        if (mathFileList == null || mathFileList.isEmpty()) {
            throw new Exception("无匹配资源!");
        }
        mathFileList.stream().map(file -> {
            System.out.println(file.getName());
            String fileJsonString = fileJsonStringReader(file.getAbsolutePath());
            if (StringUtils.isEmpty(fileJsonString)) {
                return null;
            }
            JSONObject dataJSONObject = JSONObject.parseObject(fileJsonString);
            JSONObject dataObject = dataJSONObject.getJSONObject("data");
            return dataObject.getString("paperName");
        }).forEach(paperName-> System.out.println(/*paperName*/));


    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class ExamFile{
        private String subject;
        private String []category;
        private List<String[]> categoryFileName;
    }

    @Test
    public void createFile() throws IOException {

        String directory = "D:\\Users\\86131\\Desktop\\自考-整理\\2510自考整理\\题集2\\";
        String suffix = ".json";
        List<ExamFile> examFiles =
                Arrays.asList(new ExamFile("15040习思想概论", new String[]{"【1.1】章节练习", "【2.1】每课一练", "【3.1】模拟考试", "【4.1】历年真题"}
                        , Arrays.asList(new String[]{"001", "002-3", "004-5", "006-7", "008-9", "010-11", "012-13", "014-15", "016-17"}
                , new String[]{"1", "2", "3", "4", "5", "6"}
                , new String[]{"1", "2", "3"}
                , new String[]{"2024年10月", "2025年4月"}))
                ,
                new ExamFile("15043中近现史纲要", new String[]{"【1.1】章节练习", "【2.1】每课一练", "【3.1】模拟考试", "【4.1】历年真题"}
                        , Arrays.asList(new String[]{"1-2", "3-4", "5-6", "7-8", "9-10"}
                        , new String[]{"1", "2", "3", "4", "5", "6"}
                        , new String[]{"1", "2", "3"}
                        , new String[]{"2021年4月", "2021年10月", "2022年4月", "2022年10月", "2023年4月", "2023年10月", "2024年4月", "2025年4月"})));

        for (ExamFile examFile : examFiles) {
            String subject = examFile.getSubject();
            String newDirectory = directory + subject + "\\";
            File newDirectoryFile = new File(newDirectory);
            if(!newDirectoryFile.exists()){
                newDirectoryFile.mkdirs();
            }
            String[] category = examFile.getCategory();
            List<String[]> categoryFileName = examFile.getCategoryFileName();
            for (int i = 0; i < category.length; i++) {
                String currentCategory =  category[i];
                String[] fileName = categoryFileName.get(i);
                for (String f : fileName) {
                    String newFileName = newDirectory + subject + "-" + currentCategory +  f + suffix;
                    File newFile = new File(newFileName);
                    if(!newFile.exists()){
                        newFile.createNewFile();
                    }
                }
            }

        }
    }


    @Test
    public void incorrectCollectionHandler() throws Exception {
        String directory = "D:\\Users\\86131\\Desktop\\自考-整理\\2510自考整理\\错题集\\";
        File resourcesDirectory = new File(directory);
        if(!resourcesDirectory.exists() || !resourcesDirectory.isDirectory()){
            throw new Exception("资源不存在!");
        }
        String mathPrefix = "15043中近现史纲【1.1】";
        List<File> mathFileList = Arrays.stream(resourcesDirectory.listFiles())
                .filter(file -> file.getName().contains(mathPrefix))
                .collect(Collectors.toList());
        if (mathFileList == null || mathFileList.isEmpty()) {
            throw new Exception("无匹配资源!");
        }
        List<Question> mathIncorrectList = mathFileList.stream().map(file -> {
            System.out.println(file.getName());
            String fileJsonString = fileJsonStringReader(file.getAbsolutePath());
            JSONObject dataJSONObject = JSONObject.parseObject(fileJsonString);
            JSONObject dataObject = dataJSONObject.getJSONObject("data");
            String subListStr = dataObject.getString("subList");

            List<Question> questionList = JSONObject.parseArray(subListStr, Question.class);
            System.out.println("错题数："+questionList.size());
            return questionList;
        }).flatMap(Collection::stream).collect(Collectors.toList());
        System.out.println("错题总数："+mathIncorrectList.size());
        String newIncorrectFileName = "15043中近现史纲-章节练习-错题集.txt";
        String newIncorrectFilePath = directory + newIncorrectFileName;
        File incorrectFile = new File(newIncorrectFilePath);
        if (incorrectFile.exists()) {
            incorrectFile.delete();
        }
        incorrectFile.createNewFile();
        try {
            FileWriter fileWriter = new FileWriter(incorrectFile);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            Integer index = 0;
            for (Question question : mathIncorrectList) {
                bufferedWriter.write(++index + question.toString());
                bufferedWriter.newLine();
            }
            // 关闭流
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
