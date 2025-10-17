package cn.template.category.service;

import cn.template.web.exam.entity.ZkPaper;

import java.util.List;

/**
 * @author licq
 * @version 1.0
 * @date 2025/10/16 12:22
 */
public interface ICategoryService {

    /**
     *
     * @param subjectCode
     * @param examCategory
     * @return
     */
    List<ZkPaper> getSubjectExamCategory(String subjectCode, Integer examCategory) throws Exception;
}
