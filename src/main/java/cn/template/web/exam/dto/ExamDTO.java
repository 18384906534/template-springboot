package cn.template.web.exam.dto;

import lombok.Data;

import java.util.List;

/**
 * @author licq
 * @version 1.0
 * @date 2025/10/15 22:30
 */
@Data
public class ExamDTO {

    /**
     * 科目代码
     */
    private String subjectCode;

    /**
     * 测试类型代码
     */
    private Integer examCategoryCode;

    /**
     * 考试名称
     */
    private String examName;

    /**
     * 答题
     */
    private List<ExamAnswerDTO> answers;

    /**
     * 用时
     */
    private String examTime;
}
