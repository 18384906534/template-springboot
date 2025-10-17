package cn.template.web.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author licq
 * @version 1.0
 * @date 2025/10/15 11:37
 */
@Data
@TableName("zk_exam_record")
public class ZkExamRecord {

      @TableId(type = IdType.AUTO)
      private Long examId;

      private String subjectCode;

      private Integer examCategoryCode;

      private String examName;

      private String examTime;

      private Date createTime;

}
