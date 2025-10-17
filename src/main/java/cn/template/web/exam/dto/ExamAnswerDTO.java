package cn.template.web.exam.dto;

import lombok.Data;

/**
 * @author licq
 * @version 1.0
 * @date 2025/10/15 22:30
 */
@Data
public class ExamAnswerDTO {

    /**
     * 题型
     */
    private String questionStyle;

    /**
     * 问题ID
     */
    private Long questionId;

    /**
     * 选项/回答
     */
    private String answer;
}
