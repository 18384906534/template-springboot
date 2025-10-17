package cn.template.web.exam.service.impl;

import cn.template.enums.ExamCategoryEnum;
import cn.template.enums.QuestionCategoryEnum;
import cn.template.enums.SubjectEnum;
import cn.template.web.exam.dto.*;
import cn.template.web.exam.entity.*;
import cn.template.web.exam.mapper.*;
import cn.template.web.exam.service.IExamService;
import cn.template.web.exam.service.IZkExamOptionAnswerService;
import cn.template.web.exam.service.IZkExamPointAnswerService;
import cn.template.web.exam.vo.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author licq
 * @version 1.0
 * @date 2025/10/15 16:03
 */
@Service
public class IExamServiceImpl implements IExamService {

    @Autowired
    private ZkQuestionMapper questionMapper;
    @Autowired
    private ZkQuestionOptionMapper questionOptionMapper;

    @Autowired
    private ZkExamRecordMapper examRecordMapper;
    @Autowired
    private ZkExamOptionAnswerMapper examOptionAnswerMapper;
    @Autowired
    private ZkExamPointAnswerMapper examPointAnswerMapper;

    @Autowired
    private IZkExamOptionAnswerService examOptionAnswerService;
    @Autowired
    private IZkExamPointAnswerService examPointAnswerService;

    @Override
    public List<ExamRecordVO> getExamList(ExamRecordQueryDTO examRecordQueryDTO) {
        QueryWrapper<ZkExamRecord> examRecordQueryWrapper = new QueryWrapper<>();
        examRecordQueryWrapper.lambda().select();
        String subjectCode = examRecordQueryDTO.getSubjectCode();
        Integer examCategoryCode = examRecordQueryDTO.getExamCategoryCode();
        if(StringUtils.hasText(subjectCode)){
            examRecordQueryWrapper.lambda().eq(ZkExamRecord::getSubjectCode,subjectCode);
        }
        if(Objects.nonNull(examCategoryCode)){
            examRecordQueryWrapper.lambda().eq(ZkExamRecord::getExamCategoryCode,examCategoryCode);
        }
        List<ZkExamRecord> examRecordList = examRecordMapper.selectList(examRecordQueryWrapper);
        List<ExamRecordVO> examRecordVOList = Collections.emptyList();
        if (!CollectionUtils.isEmpty(examRecordList)) {
            examRecordVOList = examRecordList.stream().map(examRecord->{
                ExamRecordVO examRecordVO = new ExamRecordVO();
                BeanUtils.copyProperties(examRecord,examRecordVO);
                return examRecordVO;
            }).collect(Collectors.toList());
        }
        return examRecordVOList;
    }

    @Override
    public ExamInfoVO getExamInfo(ExamCategoryDTO examCategoryDTO) throws Exception {
        SubjectEnum subjectEnum = SubjectEnum.getByCode(examCategoryDTO.getSubjectCode());
        if(Objects.isNull(subjectEnum)){
            throw new Exception("科目代码不匹配!");
        }
        ExamCategoryEnum examCategoryEnum = ExamCategoryEnum.getByCode(examCategoryDTO.getExamCategoryCode());
        if(Objects.isNull(subjectEnum)){
            throw new Exception("测试类型代码不匹配!");
        }
        ExamInfoVO examVO = new ExamInfoVO();
        String subjectCode = examCategoryDTO.getSubjectCode();
        examVO.setSubjectCode(subjectCode);
        examVO.setExamCategoryCode(examCategoryDTO.getExamCategoryCode());

        QueryWrapper<ZkQuestion> questionQueryWrapper = new QueryWrapper();
        questionQueryWrapper.lambda().eq(ZkQuestion::getSubjectCode,subjectCode);
        //指定试卷ID
        Long paperId = examCategoryDTO.getPaperId();
        //指定试卷名称
        String examContent = examCategoryDTO.getExamContent();
        examVO.setExamName(examContent);
        if(Objects.nonNull(paperId)){
            //选择指定试卷
            questionQueryWrapper.lambda().eq(ZkQuestion::getPaperId,paperId);
        }else {
            // 获取今天开始时间
            QueryWrapper<ZkExamRecord> examRecordQueryWrapper = new QueryWrapper<>();
            examRecordQueryWrapper.lambda().select()
                    .ge(ZkExamRecord::getCreateTime,LocalDate.now().atStartOfDay());
            Long currentCount = examRecordMapper.selectCount(examRecordQueryWrapper);
            String formatted = String.format("%03d", Objects.isNull(currentCount)?1:++currentCount);

            //自动生成试卷名称
            examVO.setExamName(Objects.equals(examCategoryEnum,ExamCategoryEnum.SIMULATION2)
                    ?subjectEnum.getAbbreviation()+ "-" +examCategoryEnum.getInfo()+formatted
                    :examContent);
            // TODO: 2025/10/17 自动生成试卷选题查询逻辑

        }
        //查询题目
        List<ZkQuestion> questionList = questionMapper.selectList(questionQueryWrapper);
        if(!CollectionUtils.isEmpty(questionList)){
            List<ZkQuestionVO> questionVOList = questionList.stream().map(question -> {
                ZkQuestionVO questionVO = new ZkQuestionVO();
                BeanUtils.copyProperties(question, questionVO);
                return questionVO;
            }).collect(Collectors.toList());
            dxHandler(questionVOList);
            examVO.setQuestionVOList(questionVOList);
        }
        return examVO;
    }

    @Override
    public ExamAnswerInfoVO saveExamRecord(ExamDTO examDTO) {
        ZkExamRecord examRecord = new ZkExamRecord();
        examRecord.setSubjectCode(examDTO.getSubjectCode());
        examRecord.setExamCategoryCode(examDTO.getExamCategoryCode());
        examRecord.setExamName(examDTO.getExamName());
        examRecord.setExamTime( examDTO.getExamTime());
        examRecord.setCreateTime(new Date());
        examRecordMapper.insert(examRecord);

        List<ExamAnswerDTO> answers = examDTO.getAnswers();
        if (!CollectionUtils.isEmpty(answers)) {
            Long examId = examRecord.getExamId();
            Map<String, List<ExamAnswerDTO>> questionStyleAnswerMap = answers.stream().collect(Collectors.groupingBy(ExamAnswerDTO::getQuestionStyle));
            for (QuestionCategoryEnum questionEnum : QuestionCategoryEnum.values()) {
                String cssStyle = questionEnum.getCssStyle();
                List<ExamAnswerDTO> examAnswerDTOList = questionStyleAnswerMap.get(cssStyle);
                if(CollectionUtils.isEmpty(examAnswerDTOList)){
                    continue;
                }
                if(Objects.equals(questionEnum.getCode(),QuestionCategoryEnum.DX.getCode())){
                    List<Long> questionIdList = examAnswerDTOList.stream().map(ExamAnswerDTO::getQuestionId).collect(Collectors.toList());
                    QueryWrapper<ZkQuestion> questionQueryWrapper = new QueryWrapper<>();
                    questionQueryWrapper.lambda()
                            .select(ZkQuestion::getQuestionId,ZkQuestion::getRightAnswer)
                            .in(ZkQuestion::getQuestionId,questionIdList);
                    List<ZkQuestion> questionList = questionMapper.selectList(questionQueryWrapper);
                    Map<Long,String> questionAnswerMap = CollectionUtils.isEmpty(questionList)?Collections.emptyMap():questionList.stream()
                            .collect(Collectors.toMap(ZkQuestion::getQuestionId,ZkQuestion::getRightAnswer));
                    List<ZkExamOptionAnswer> examOptionAnswerList = examAnswerDTOList.stream().map(examAnswerDTO -> {
                        Long questionId = examAnswerDTO.getQuestionId();

                        ZkExamOptionAnswer examOptionAnswer = new ZkExamOptionAnswer();
                        examOptionAnswer.setExamId(examId);
                        examOptionAnswer.setQuestionId(questionId);

                        String answer = examAnswerDTO.getAnswer();
                        String rightAnswer = questionAnswerMap.get(questionId);
                        examOptionAnswer.setExamAnswer(answer);
                        examOptionAnswer.setRightAnswer(rightAnswer);
                        examOptionAnswer.setAnswerRight(Objects.nonNull(answer) && Objects.equals(answer.trim(), rightAnswer.trim()));
                        return examOptionAnswer;
                    }).collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(examOptionAnswerList)) {
                        examOptionAnswerService.saveBatch(examOptionAnswerList);
                    }
                }else{
                    List<ZkExamPointAnswer> examPointAnswerList = examAnswerDTOList.stream().map(examAnswerDTO -> {
                        Long questionId = examAnswerDTO.getQuestionId();

                        ZkExamPointAnswer examPointAnswer = new ZkExamPointAnswer();
                        examPointAnswer.setExamId(examId);
                        examPointAnswer.setQuestionId(questionId);
                        examPointAnswer.setExamAnswer(examAnswerDTO.getAnswer());
                        return examPointAnswer;
                    }).collect(Collectors.toList());
                    if (!CollectionUtils.isEmpty(examPointAnswerList)) {
                        examPointAnswerService.saveBatch(examPointAnswerList);
                    }
                }
                questionStyleAnswerMap.remove(cssStyle);
            }
        }


        ExamAnswerInfoVO examAnswerInfoVO = new ExamAnswerInfoVO();
        BeanUtils.copyProperties(examDTO,examAnswerInfoVO);
        return examAnswerInfoVO;
    }

    @Override
    public ExamAnswerInfoVO getExamDetailList(ExamAnswerQueryDTO examAnswerQueryDTO) {

        Long examId = examAnswerQueryDTO.getExamId();
        String subjectCode = examAnswerQueryDTO.getSubjectCode();
        Integer examCategoryCode = examAnswerQueryDTO.getExamCategoryCode();


        QueryWrapper<ZkExamRecord> examRecordQueryWrapper = new QueryWrapper<>();
        if(Objects.nonNull(examId)){
            examRecordQueryWrapper.lambda().eq(ZkExamRecord::getExamId,examId);
        }else{
            //找测试记录集合
            if(StringUtils.hasText(subjectCode)){
                examRecordQueryWrapper.lambda().eq(ZkExamRecord::getSubjectCode,subjectCode);
            }
            if(Objects.nonNull(examCategoryCode)){
                examRecordQueryWrapper.lambda().eq(ZkExamRecord::getExamCategoryCode,examCategoryCode);
            }
        }
        examRecordQueryWrapper.lambda().orderByDesc(ZkExamRecord::getExamId);
        List<ZkExamRecord> examRecordList = Collections.emptyList();
        List<ZkExamRecord> databaseExamRecordList = examRecordMapper.selectList(examRecordQueryWrapper);
        if(!CollectionUtils.isEmpty(databaseExamRecordList)){
            examRecordList = databaseExamRecordList;
        }

        List<Long> examIdList = databaseExamRecordList.stream().map(ZkExamRecord::getExamId).collect(Collectors.toList());
        List<ZkExamOptionAnswer> examOptionAnswerList = Collections.emptyList();
        List<ZkExamPointAnswer> examPointAnswerList = Collections.emptyList();
        if (!CollectionUtils.isEmpty(examIdList)) {
            QueryWrapper<ZkExamOptionAnswer>  examOptionAnswerQueryWrapper = new QueryWrapper<>();
            examOptionAnswerQueryWrapper.lambda().in(ZkExamOptionAnswer::getExamId,examIdList)
                    .orderByDesc(ZkExamOptionAnswer::getOptionAnswerId);
            List<ZkExamOptionAnswer> databaseExamOptionAnswerList = examOptionAnswerMapper.selectList(examOptionAnswerQueryWrapper);
            if (!CollectionUtils.isEmpty(databaseExamOptionAnswerList)) {
                examOptionAnswerList = databaseExamOptionAnswerList;
            }

            QueryWrapper<ZkExamPointAnswer> examPointAnswerQueryWrapper = new QueryWrapper<>();
            examPointAnswerQueryWrapper.lambda().in(ZkExamPointAnswer::getExamId,examIdList)
                    .orderByDesc(ZkExamPointAnswer::getPointAnswerId);
            List<ZkExamPointAnswer> databaseExamPointAnswerList = examPointAnswerMapper.selectList(examPointAnswerQueryWrapper);
            if (!CollectionUtils.isEmpty(databaseExamPointAnswerList)) {
                examPointAnswerList = databaseExamPointAnswerList;
            }
        }
        List<Long> distinctQuestionIdList = Arrays.asList(
                examOptionAnswerList.stream().map(ZkExamOptionAnswer::getQuestionId).collect(Collectors.toList())
                ,examPointAnswerList.stream().map(ZkExamPointAnswer::getQuestionId).collect(Collectors.toList())
        ).stream().filter(Objects::nonNull).flatMap(Collection::stream).collect(Collectors.toList());


        ExamAnswerInfoVO examAnswerInfoVO = new ExamAnswerInfoVO();
        examAnswerInfoVO.setSubjectCode(subjectCode);
        examAnswerInfoVO.setExamCategoryCode(examCategoryCode);
        if(!CollectionUtils.isEmpty(distinctQuestionIdList)){
            QueryWrapper<ZkQuestion> questionQueryWrapper = new QueryWrapper();
            questionQueryWrapper.lambda().select()
                    .in(ZkQuestion::getQuestionId,distinctQuestionIdList);
            List<ZkQuestion> questionList = questionMapper.selectList(questionQueryWrapper);
            if(!CollectionUtils.isEmpty(questionList)){
                Map<Long, ZkExamRecord> examMap = examRecordList.stream().collect(Collectors.toMap(ZkExamRecord::getExamId, Function.identity()));
                Map<Long, List<ZkExamOptionAnswer>> questionExamOptionAnswerListMap = examOptionAnswerList.stream().collect(Collectors.groupingBy(ZkExamOptionAnswer::getQuestionId));
                Map<Long, List<ZkExamPointAnswer>> questionExamPointAnswerListMap = examPointAnswerList.stream().collect(Collectors.groupingBy(ZkExamPointAnswer::getQuestionId));
                List<ZkQuestionVO> questionVOList = questionList.stream().map(question -> {
                    ZkQuestionVO questionVO = new ZkQuestionVO();
                    BeanUtils.copyProperties(question, questionVO);
                    List<ExamAnswerHistoryVO> examAnswerHistoryVOList = Collections.emptyList();
                    if (Objects.equals(questionVO.getQuestionCode(), QuestionCategoryEnum.DX.getCode())) {
                        List<ZkExamOptionAnswer> questionOptionAnswerList = questionExamOptionAnswerListMap.get(questionVO.getQuestionId());
                        if (!CollectionUtils.isEmpty(questionOptionAnswerList)) {
                            examAnswerHistoryVOList = questionOptionAnswerList.stream().map(questionAnswer->{
                                ExamAnswerHistoryVO examAnswerHistoryVO = new ExamAnswerHistoryVO();

                                Long currentExamId = questionAnswer.getExamId();
                                examAnswerHistoryVO.setExamId(currentExamId);
                                if (examMap.containsKey(currentExamId)) {
                                    ZkExamRecord examRecord = examMap.get(currentExamId);
                                    examAnswerHistoryVO.setSubjectCode(examRecord.getSubjectCode());
                                    examAnswerHistoryVO.setExamCategoryCode(examRecord.getExamCategoryCode());
                                    examAnswerHistoryVO.setExamName(examRecord.getExamName());
                                    examAnswerHistoryVO.setExamTime(examRecord.getExamTime());
                                    examAnswerHistoryVO.setCreateTime(examRecord.getCreateTime());
                                }
                                examAnswerHistoryVO.setMyAnswer(questionAnswer.getExamAnswer());
                                examAnswerHistoryVO.setRight(questionAnswer.getAnswerRight());

                                return examAnswerHistoryVO;
                            }).collect(Collectors.toList());
                        }
                    }else{
                        List<ZkExamPointAnswer> questionPointAnswerList = questionExamPointAnswerListMap.get(questionVO.getQuestionId());
                        if (!CollectionUtils.isEmpty(questionPointAnswerList)) {
                            examAnswerHistoryVOList= questionPointAnswerList.stream().map(questionAnswer->{
                                ExamAnswerHistoryVO examAnswerHistoryVO = new ExamAnswerHistoryVO();
                                Long currentExamId = questionAnswer.getExamId();
                                examAnswerHistoryVO.setExamId(currentExamId);
                                if (examMap.containsKey(currentExamId)) {
                                    ZkExamRecord examRecord = examMap.get(currentExamId);
                                    examAnswerHistoryVO.setSubjectCode(examRecord.getSubjectCode());
                                    examAnswerHistoryVO.setExamCategoryCode(examRecord.getExamCategoryCode());
                                    examAnswerHistoryVO.setExamName(examRecord.getExamName());
                                    examAnswerHistoryVO.setExamTime(examRecord.getExamTime());
                                    examAnswerHistoryVO.setCreateTime(examRecord.getCreateTime());
                                }
                                examAnswerHistoryVO.setMyAnswer(questionAnswer.getExamAnswer());
                                return examAnswerHistoryVO;
                            }).collect(Collectors.toList());
                        }
                    }
                    questionVO.setExamAnswerHistoryVOList(examAnswerHistoryVOList);
                    return questionVO;
                }).collect(Collectors.toList());

                dxHandler(questionVOList);
                examAnswerInfoVO.setQuestionVOList(questionVOList);
            }
        }
        return examAnswerInfoVO;
    }

    public void dxHandler(List<ZkQuestionVO> questionVOList){
        List<ZkQuestionVO> dxQuestionVOList = questionVOList.stream().filter(questionVO -> Objects.equals(questionVO.getQuestionCode(), QuestionCategoryEnum.DX.getCode()))
                .collect(Collectors.toList());

        if(!CollectionUtils.isEmpty(dxQuestionVOList)){
            List<Long> questionIdList = dxQuestionVOList.stream().map(ZkQuestionVO::getQuestionId).collect(Collectors.toList());

            QueryWrapper<ZkQuestionOption> questionOptionQueryWrapper = new QueryWrapper();
            questionOptionQueryWrapper.lambda().select().in(ZkQuestionOption::getQuestionId,questionIdList);
            List<ZkQuestionOption> dxQuestionOptionList = questionOptionMapper.selectList(questionOptionQueryWrapper);
            if (!CollectionUtils.isEmpty(dxQuestionOptionList)) {
                Map<Long,List<ZkQuestionOption>> questionOptionListMap = dxQuestionOptionList.stream().collect(Collectors.groupingBy(ZkQuestionOption::getQuestionId));
                dxQuestionVOList.stream().forEach(questionVO->{
                    List<ZkQuestionOption> currentQuestionOptionList = questionOptionListMap.get(questionVO.getQuestionId());
                    if(!CollectionUtils.isEmpty(currentQuestionOptionList)){
                        questionVO.setQuestionOptionVOList(currentQuestionOptionList.stream().map(currentQuestionOption->{
                            ZkQuestionOptionVO questionOptionVO = new ZkQuestionOptionVO();
                            BeanUtils.copyProperties(currentQuestionOption, questionOptionVO);
                            return questionOptionVO;
                        }).collect(Collectors.toList()));
                    }
                });
            }
        }
    }
}
