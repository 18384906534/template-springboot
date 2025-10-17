package cn.template.exam.service;

import cn.template.exam.dto.ExamAnswerQueryDTO;
import cn.template.exam.dto.ExamCategoryDTO;
import cn.template.exam.dto.ExamDTO;
import cn.template.exam.dto.ExamRecordQueryDTO;
import cn.template.exam.vo.ExamAnswerInfoVO;
import cn.template.exam.vo.ExamInfoVO;
import cn.template.exam.vo.ExamRecordVO;
import cn.template.exam.vo.ExamResultVO;

import java.util.List;

/**
 * @author licq
 * @version 1.0
 * @date 2025/10/15 11:18
 */
public interface IExamService {

    List<ExamRecordVO> getExamList(ExamRecordQueryDTO examRecordQueryDTO);

    ExamInfoVO getExamInfo(ExamCategoryDTO examCategoryDto) throws Exception;

    ExamResultVO saveExamRecord(ExamDTO examDTO);


    ExamAnswerInfoVO getExamDetailList(ExamAnswerQueryDTO examAnswerQueryDTO);
}
