package cn.template.category.service.impl;

import cn.template.category.service.ICategoryService;
import cn.template.enums.ExamCategoryEnum;
import cn.template.enums.SubjectEnum;
import cn.template.web.exam.entity.ZkPaper;
import cn.template.web.exam.mapper.ZkPaperMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author licq
 * @version 1.0
 * @date 2025/10/16 12:23
 */
@Service
public class ICategoryServiceImpl implements ICategoryService {

    @Autowired
    private ZkPaperMapper paperMapper;

    @Override
    public List<ZkPaper> getSubjectExamCategory(String subjectCode, Integer examCategory) throws Exception {
        SubjectEnum subjectEnum = SubjectEnum.getByCode(subjectCode);
        if(Objects.isNull(subjectEnum)){
            throw new Exception("科目代码不匹配!");
        }
        ExamCategoryEnum examCategoryEnum = ExamCategoryEnum.getByCode(examCategory);
        if(Objects.isNull(subjectEnum)){
            throw new Exception("测试类型代码不匹配!");
        }
        List<ZkPaper> examCategoryPaperList = Collections.emptyList();
        if(!Objects.equals(examCategoryEnum,ExamCategoryEnum.SIMULATION2)){
            QueryWrapper<ZkPaper> paperQueryWrapper = new QueryWrapper<>();
            paperQueryWrapper.lambda().select(ZkPaper::getPaperId,ZkPaper::getOriginPaperName,ZkPaper::getShortPaperName)
                    .eq(ZkPaper::getSubjectCode,subjectCode)
                    .eq(ZkPaper::getExamCategoryCode,examCategory);
            List<ZkPaper> paperList = paperMapper.selectList(paperQueryWrapper);
            if (!CollectionUtils.isEmpty(paperList)) {
                examCategoryPaperList = paperList;
            }
        }
        return examCategoryPaperList;
    }
}
