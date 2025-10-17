package cn.template.web.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;

/**
 * @author licq
 * @version 1.0
 * @date 2025/10/15 11:30
 */
@Data
@TableName("zk_question")
public class ZkQuestion {

     @TableId(type = IdType.AUTO)
     private Long questionId;

     private String subjectCode;

     private Long paperId;

     private String chapterCode;

     private String examPoint;

     private Integer examWeight;

     private String questionCode;

     private String questionContent;

     private String rightAnswer;

     private Integer fullScore;

     private String analysis;

     @TableField(exist = false)
     private List<ZkQuestionOption> questionOptionList;
     @TableField(exist = false)
     private List<ZkQuestionPoint> questionPointList;
}
