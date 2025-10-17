package cn.template.web.exam.service.impl;

import cn.template.web.exam.entity.ZkQuestion;
import cn.template.web.exam.mapper.ZkQuestionMapper;
import cn.template.web.exam.service.IZkQuestionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author licq
 * @version 1.0
 * @date 2025/10/15 12:35
 */
@Service
public class IZkQuestionServiceImpl  extends ServiceImpl<ZkQuestionMapper, ZkQuestion> implements IZkQuestionService {
}
