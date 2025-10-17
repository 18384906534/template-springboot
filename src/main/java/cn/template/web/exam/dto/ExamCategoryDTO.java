package cn.template.web.exam.dto;

import lombok.Data;

/**
 * @author licq
 * @version 1.0
 * @date 2025/10/15 16:00
 */
@Data
public class ExamCategoryDTO {

    /**
     * 科目代码
     */
    private Long paperId;

    /**
     * 科目代码
     */
    private String subjectCode;

    /**
     * 测试类型代码
     */
    private Integer examCategoryCode;


    /**
     * 选择内容
     */
    private String examContent;
}
