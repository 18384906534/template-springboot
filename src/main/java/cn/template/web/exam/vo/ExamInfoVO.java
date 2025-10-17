package cn.template.web.exam.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author licq
 * @version 1.0
 * @date 2025/10/15 16:01
 */
@Data
public class ExamInfoVO {

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
     * 开始时间
     */
    private Date startTime;

    /**
     * 题目信息
     */
    private List<ZkQuestionVO> questionVOList;
}
