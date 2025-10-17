package cn.template.web.exam.vo;

import lombok.Data;

/**
 * @author licq
 * @version 1.0
 * @date 2025/10/15 11:33
 */
@Data
public class ZkQuestionOptionVO {

    /**
     * 选项标识
     */
    private Long optionId;

    /**
     * 所属问题
     */
    private Long questionId;

    /**
     * 选项序列(排序)
     */
    private Integer optionSequence;

    /**
     * 选项代码(A、B...)
     */
    private String optionKey;

    /**
     * 选项内容
     */
    private String optionText;

    /**
     * 正确选项
     */
    private Boolean rightOption;

    /**
     * 分数(如果当前是正确选项，选中后的得分)
     */
    private Integer score;
}
