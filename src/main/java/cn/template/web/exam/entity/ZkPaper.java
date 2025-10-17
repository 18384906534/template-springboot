package cn.template.web.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.List;

/**
 * @author licq
 * @version 1.0
 * @date 2025/10/16 22:41
 */
@Data
public class ZkPaper {

    @TableId(type = IdType.AUTO)
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
     * old.试卷标识(科目-测试类型-板块)
     * new.板块
     */
    private String chapterCode;

    /**
     * 原试卷名
     */
    private String originPaperName;

    /**
     * 试卷简称
     */
    private String shortPaperName;

    /**
     * 对应文件路径
     */
    private String fileUrl;

    @TableField(exist = false)
    private List<ZkQuestion> questionList;
}
