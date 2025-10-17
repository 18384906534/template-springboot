package cn.template.web.exam.entity;

import lombok.Data;

/**
 * @author licq
 * @version 1.0
 * @date 2025/10/15 11:40
 */
@Data
public class ZkExamPointAnswer {

     private Long pointAnswerId;

     private Long examId;

     private Long questionId;

     private String examAnswer;

     private Integer score;
}
