package cn.template.web.exam.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author licq
 * @version 1.0
 * @date 2025/10/16 15:33
 */
@Data
public class ExamAnswerHistoryVO {

    /**
     * 试卷标识
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

    /**
     * 试卷名称
     */
    private String examName;

    /**
     * 用时
     */
    private String examTime;

    /**
     * 交卷时间
     */
    private Date createTime;

    /**
     * 我的回答
     */
    private String myAnswer;

    /**
     * 回答正确
     */
    private Boolean right;
}
