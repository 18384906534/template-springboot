package cn.template.category.controller;

import cn.template.category.service.ICategoryService;
import cn.template.enums.ExamCategoryEnum;
import cn.template.enums.SubjectEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author licq
 * @version 1.0
 * @date 2025/10/16 11:47
 */
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private ICategoryService categoryService;

    /**
     * 获取科目
     * @return
     */
    /**
     * 获取科目
     */
    @GetMapping("/getSubject")
    public Map<String, Object> getSubject() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "成功");

        List<Map<String, String>> subjects = new ArrayList<>();
        for (SubjectEnum subject : SubjectEnum.values()) {
            Map<String, String> subjectMap = new HashMap<>();
            subjectMap.put("id", subject.getCode());
            subjectMap.put("name", subject.getAbbreviation());
            subjects.add(subjectMap);
        }
        result.put("data", subjects);

        return result;
    }

    /**
     * 获取测试类型
     * @return
     */
    @GetMapping("/getExamCategory")
    public Map<String, Object> getExamCategory() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "成功");

        List<Map<String, Object>> types = new ArrayList<>();
        for (ExamCategoryEnum type : ExamCategoryEnum.values()) {
            Map<String, Object> typeMap = new HashMap<>();
            typeMap.put("id", type.getCode());
            typeMap.put("name", type.getInfo());
            types.add(typeMap);
        }
        result.put("data", types);

        return result;
    }

    /**
     * 根据科目和测试类型获取测试内容
     * @param subjectCode
     * @param examCategory
     */
    @GetMapping("/getSubjectExamCategory/{subjectCode}/{examCategory}")
    public Map<String, Object> getSubjectExamCategory(
            @PathVariable("subjectCode") String subjectCode,
            @PathVariable("examCategory") Integer examCategory) throws Exception {

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "成功");

        // 这里需要你根据业务逻辑实现获取内容的方法
        // 暂时返回空列表
        result.put("data", categoryService.getSubjectExamCategory(subjectCode,examCategory));

        return result;
    }
}
