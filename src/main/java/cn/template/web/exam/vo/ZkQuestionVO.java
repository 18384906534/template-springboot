package cn.template.web.exam.vo;

import lombok.Data;

import java.util.List;

/**
 * @author licq
 * @version 1.0
 * @date 2025/10/15 11:30
 */
@Data
public class ZkQuestionVO {

     private Long questionId;

     /**
      * 考点(章节)
      */
     private String examPoint;

     /**
      * QuestionCategoryEnum
      * 题目类型
      */
     private String questionCode;

     /**
      * 题目内容
      */
     private String questionContent;

     /**
      * 示例答案
      */
     private String rightAnswer;

     /**
      * 总分数
      */
     private Integer fullScore;

     /**
      * 知识点分析
      */
     private String analysis;

     /**
      * 单选题选项信息
      */
     private List<ZkQuestionOptionVO> questionOptionVOList;


     /**
      * 历史选项
      */
     private List<ExamAnswerHistoryVO> examAnswerHistoryVOList;
}
