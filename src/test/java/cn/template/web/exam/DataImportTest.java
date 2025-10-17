package cn.template.web.exam;

import cn.template.enums.DataCategoryEnum;
import cn.template.enums.ExamCategoryEnum;
import cn.template.enums.QuestionCategoryEnum;
import cn.template.web.WebTest;
import cn.template.web.exam.entity.ZkPaper;
import cn.template.web.exam.entity.ZkQuestion;
import cn.template.web.exam.entity.ZkQuestionOption;
import cn.template.web.exam.service.IPaperService;
import cn.template.web.exam.service.IZkQuestionOptionService;
import cn.template.web.exam.service.IZkQuestionService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author licq
 * @version 1.0
 * @date 2025/10/15 12:14
 */

public class DataImportTest extends WebTest {

    @Autowired
    private IPaperService paperService;
    @Autowired
    private IZkQuestionService questionService;
    @Autowired
    private IZkQuestionOptionService questionOptionService;




    @Rollback(value = false)
    @Test
    public void readJsonImportData() throws Exception {
        @Data
        @AllArgsConstructor
        class TempSubject{
            private String subjectCode;
            private String subjectDirectory;
            private String mathName;
        }
        List<TempSubject> subjectList = Arrays.asList(new TempSubject("15040", "习思想概论", "习思想概论-")
                , new TempSubject("15043", "中近现史纲要", "中近现史纲要-"));

        for (TempSubject tempSubject : subjectList) {
            String subjectCode = tempSubject.getSubjectCode();
            String subjectDirectory = tempSubject.getSubjectDirectory();
            String mathName = tempSubject.getMathName();

            String chooseDirectory = subjectCode+subjectDirectory;
            String directory = "D:\\Users\\86131\\Desktop\\自考-整理\\2510自考整理\\题集\\"+chooseDirectory;

            File resourcesDirectory = new File(directory);
            if(!resourcesDirectory.exists() || !resourcesDirectory.isDirectory()){
                throw new Exception("资源不存在!");
            }
            String mathPrefix = subjectCode + mathName;
            List<File> mathFileList = Arrays.stream(resourcesDirectory.listFiles())
                    .filter(file -> file.getName().contains(mathPrefix))
                    .collect(Collectors.toList());
            if (mathFileList == null || mathFileList.isEmpty()) {
                throw new Exception("无匹配资源!");
            }
            Map<Long,String> originQuestionPaperNap = new HashMap<>(mathFileList.size()*16);
            List<ZkPaper> paperList = mathFileList.stream().map(file -> {
                String fileName = file.getName();
                System.out.println(fileName);
                String fileJsonString = fileJsonStringReader(file.getAbsolutePath());
                if(StringUtils.isEmpty(fileJsonString)){
                    return null;
                }
                JSONObject dataJSONObject = JSONObject.parseObject(fileJsonString);
                JSONObject dataObject = dataJSONObject.getJSONObject("data");
                String paperName = dataObject.getString("paperName");

                ZkPaper paper = new ZkPaper();
                paper.setSubjectCode(subjectCode);

                Integer examCategoryCode = null;
                String chapterCode = null;

                int dotIndex = fileName.lastIndexOf(".");
                DataCategoryEnum dataCategoryEnum = DataCategoryEnum.match(fileName);
                if(Objects.nonNull(dataCategoryEnum)){
                    String info = dataCategoryEnum.getInfo();
                    ExamCategoryEnum examCategoryEnum = ExamCategoryEnum.getByMatchName(info);
                    if(Objects.nonNull(examCategoryEnum)){
                        examCategoryCode = examCategoryEnum.getCode();
                    }
                    chapterCode = fileName.substring(fileName.indexOf(info) + info.length(), dotIndex);
                }

                paper.setExamCategoryCode(examCategoryCode);
                paper.setChapterCode(chapterCode);
                paper.setOriginPaperName(paperName);
                paper.setShortPaperName(fileName.substring(fileName.indexOf(mathPrefix) + mathPrefix.length(), dotIndex));
                paper.setFileUrl(file.getAbsolutePath());

                JSONArray groupsJSONArray = dataObject.getJSONArray("groups");

                String finalChapterCode = chapterCode;
                List<ZkQuestion> currentPaperQuestionList = groupsJSONArray.stream()
                        .map(jsonArray -> JSONObject.parseObject(JSONObject.toJSONString(jsonArray)).getJSONArray("questions"))
                        .flatMap(Collection::stream).map(JSONObject::toJSONString)
                        .map(json->{
                            JSONObject jsonObject = JSONObject.parseObject(json);
                            Long originQuestionId = jsonObject.getLong("id");
                            if (originQuestionPaperNap.containsKey(originQuestionId)) {
                                System.out.println("重题："+originQuestionId
                                        +",出现试卷：" +originQuestionPaperNap.get(originQuestionId)
                                        +",当前试卷：" +paperName
                                );
                                return null;
                            }
                            originQuestionPaperNap.put(originQuestionId,paperName);

                            ZkQuestion question = new ZkQuestion();
                            question.setSubjectCode(subjectCode);
                            question.setChapterCode(finalChapterCode);
                            question.setExamPoint("");
                            question.setExamWeight(0);
                            String questionType = jsonObject.getString("questionType");
                            Integer fullScore = jsonObject.getInteger("fullScore");
                            question.setQuestionCode(questionType);
                            question.setQuestionContent(jsonObject.getString("topic"));
                            question.setRightAnswer(jsonObject.getString("answer"));
                            question.setFullScore(fullScore);
                            question.setAnalysis(jsonObject.getString("analysis"));
                            if(Objects.equals(questionType,QuestionCategoryEnum.DX.getCode())){
                                JSONArray answerItems = jsonObject.getJSONArray("answerItems");
                                question.setQuestionOptionList(answerItems.stream().map(answerItem -> {
                                    JSONObject answerItemJson = JSONObject.parseObject(JSONObject.toJSONString(answerItem));
                                    ZkQuestionOption questionOption = new ZkQuestionOption();
                                    //questionOption.setOptionSequence();
                                    questionOption.setOptionKey(answerItemJson.getString("answerKey"));
                                    questionOption.setOptionText(answerItemJson.getString("answerText"));
                                    Boolean isRight = answerItemJson.getBoolean("isRight");
                                    questionOption.setRightOption(isRight);
                                    questionOption.setScore(isRight?fullScore:0);
                                    return questionOption;
                                }).collect(Collectors.toList()));
                            }
                            return question;
                        }).filter(Objects::nonNull).collect(Collectors.toList());
                paper.setQuestionList(currentPaperQuestionList);

                return paper;
            }).collect(Collectors.toList());

            paperService.saveBatch(paperList);
            paperList.stream().forEach(paper->{
                List<ZkQuestion> questionList = paper.getQuestionList();
                if (CollectionUtils.isEmpty(questionList)) {
                    return;
                }
                questionList.stream().forEach(question->{
                    question.setPaperId(paper.getPaperId());
                });
            });
            List<ZkQuestion> questionList = paperList.stream().map(ZkPaper::getQuestionList).filter(Objects::nonNull)
                    .flatMap(Collection::stream).collect(Collectors.toList());

            questionService.saveBatch(questionList);
            List<ZkQuestionOption> questionOptionList = questionList.stream().filter(question -> Objects.equals(question.getQuestionCode(), QuestionCategoryEnum.DX.getCode()))
                    .map(question -> {
                        Long questionId = question.getQuestionId();
                        List<ZkQuestionOption> currentQuestionOptionList = question.getQuestionOptionList();
                        currentQuestionOptionList.stream().forEach(questionOption -> questionOption.setQuestionId(questionId));
                        return currentQuestionOptionList;
                    }).flatMap(Collection::stream).collect(Collectors.toList());
            questionOptionService.saveBatch(questionOptionList);
        }
    }
}


