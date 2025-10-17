package cn.template.web.exam.service.impl;

import cn.template.web.exam.entity.ZkPaper;
import cn.template.web.exam.mapper.ZkPaperMapper;
import cn.template.web.exam.service.IPaperService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author licq
 * @version 1.0
 * @date 2025/10/16 23:02
 */
@Service
public class IPaperServiceImpl  extends ServiceImpl<ZkPaperMapper, ZkPaper> implements IPaperService {
}
