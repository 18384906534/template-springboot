package cn.template.web.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author licq
 * @version 1.0
 * @date 2025/10/15 11:33
 */
@Data
@TableName("zk_question_option")
public class ZkQuestionOption {

    @TableId(type = IdType.AUTO)
    private Long optionId;

    private Long questionId;

    private Integer optionSequence;

    private String optionKey;

    private String optionText;

    private Boolean rightOption;

    private Integer score;
}
