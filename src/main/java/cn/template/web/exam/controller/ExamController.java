package cn.template.web.exam.controller;

import cn.template.web.exam.dto.ExamAnswerQueryDTO;
import cn.template.web.exam.dto.ExamCategoryDTO;
import cn.template.web.exam.dto.ExamDTO;
import cn.template.web.exam.dto.ExamRecordQueryDTO;
import cn.template.web.exam.service.IExamService;
import cn.template.web.exam.vo.ExamAnswerInfoVO;
import cn.template.web.exam.vo.ExamInfoVO;
import cn.template.web.exam.vo.ExamRecordVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author licq
 * @version 1.0
 * @date 2025/10/15 11:06
 */

@Controller
@RequestMapping("/exam")
public class ExamController {

    @Autowired
    private IExamService examService;

    @ResponseBody
    @PostMapping("/getExamList")
    public List<ExamRecordVO> getExamList(@RequestBody ExamRecordQueryDTO examRecordQueryDTO) {
        return examService.getExamList(examRecordQueryDTO);
    }

    @PostMapping("/generate")
    public String generate(ExamCategoryDTO examCategoryDTO, Model model) throws Exception {
        ExamInfoVO examInfoVO = examService.getExamInfo(examCategoryDTO);
        model.addAttribute("exam", examInfoVO);
        return "exam";
    }

    @PostMapping("/examSubmit")
    public String examSubmit(@RequestBody ExamDTO examDTO, Model model){
        ExamAnswerInfoVO currentAnswerInfo = examService.saveExamRecord(examDTO);
        model.addAttribute("currentAnswer", currentAnswerInfo);
        return "index";
    }

    @GetMapping("/detail")
    public String examDetail() {
        return "detail";
    }

    @ResponseBody
    @PostMapping("/getExamDetailList")
    public ExamAnswerInfoVO getExamDetailList(ExamAnswerQueryDTO examAnswerQueryDTO) {
        return examService.getExamDetailList(examAnswerQueryDTO);
    }
}
