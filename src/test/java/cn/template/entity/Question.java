package cn.template.entity;

import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author licq
 * @version 1.0
 * @date 2025/10/15 12:43
 */
@Data
public class Question {

    private String topic;
    private String answer;
    private String rightAnswer;
    private String analysis;

    private List<Answer> answerItemList;
    private List<KnowledgeSystem> knowledgeSystemList;

    @Override
    public String toString() {
        String answerStr = String.join("   ",this.answerItemList.stream()
                .map(answer -> answer.getAnswerKey() + "."
                        + answer.getAnswerText() + "  "
                        + "正确选项：" + answer.getRight())
                .collect(Collectors.toList()));
        String knowledgeSystemStr = String.join("   ",this.knowledgeSystemList.stream()
                .map(knowledgeSystem -> knowledgeSystem.getTypeName() + "  "
                        + "目录：" + knowledgeSystem.getNamePath())
                .collect(Collectors.toList()));
        return  new StringBuilder("题目：")
                .append(topic)
                .append("\n")
                .append("选项：")
                .append(answerStr)
                .append("\n")
                .append("我的选项：")
                .append(answer)
                .append("  ")
                .append("正确选项：")
                .append(rightAnswer)
                .append("\n")
                .append("解析：")
                .append(analysis)
                .append("\n")
                .append("考点：")
                .append(knowledgeSystemStr)
                .append("\n")
                .toString();
    }
}
