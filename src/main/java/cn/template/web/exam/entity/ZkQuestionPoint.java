package cn.template.web.exam.entity;

import lombok.Data;

/**
 * @author licq
 * @version 1.0
 * @date 2025/10/15 11:35
 */
@Data
public class ZkQuestionPoint {

      private Long pointId;

      private Long questionId;

      private Integer serialNumber;

      private Integer pointSequence;

      private String pointContent;

      private Integer score;

      private String keywords;
}
