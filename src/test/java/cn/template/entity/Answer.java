package cn.template.entity;

import lombok.Data;

/**
 * @author licq
 * @version 1.0
 * @date 2025/10/15 12:43
 */
@Data
public class Answer {
    private String answerKey;
    private String answerText;
    private Boolean right;
}
