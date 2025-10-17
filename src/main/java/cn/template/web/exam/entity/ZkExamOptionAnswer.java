package cn.template.web.exam.entity;

import lombok.Data;

/**
 * @author licq
 * @version 1.0
 * @date 2025/10/15 11:38
 */
@Data
public class ZkExamOptionAnswer {

     private Long optionAnswerId;

     private Long examId;

     private Long questionId;

     private String examAnswer;

     private String rightAnswer;

     private Boolean answerRight;

     private Integer score;
}
