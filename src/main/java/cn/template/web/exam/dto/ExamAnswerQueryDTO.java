package cn.template.web.exam.dto;

import lombok.Data;

/**
 * @author licq
 * @version 1.0
 * @date 2025/10/16 14:54
 */
@Data
public class ExamAnswerQueryDTO {

    /**
     * 考试标识
     */
    private Long examId;

    /**
     * 科目代码
     */
    private String subjectCode;

    /**
     * 测试类型代码
     */
    private Integer examCategoryCode;

}
