package cn.template.web.exam.service;

import cn.template.web.exam.dto.ExamAnswerQueryDTO;
import cn.template.web.exam.dto.ExamCategoryDTO;
import cn.template.web.exam.dto.ExamDTO;
import cn.template.web.exam.dto.ExamRecordQueryDTO;
import cn.template.web.exam.vo.ExamAnswerInfoVO;
import cn.template.web.exam.vo.ExamInfoVO;
import cn.template.web.exam.vo.ExamRecordVO;

import java.util.List;

/**
 * @author licq
 * @version 1.0
 * @date 2025/10/15 11:18
 */
public interface IExamService {

    List<ExamRecordVO> getExamList(ExamRecordQueryDTO examRecordQueryDTO);

    ExamInfoVO getExamInfo(ExamCategoryDTO examCategoryDto) throws Exception;

    ExamAnswerInfoVO saveExamRecord(ExamDTO examDTO);


    ExamAnswerInfoVO getExamDetailList(ExamAnswerQueryDTO examAnswerQueryDTO);
}
