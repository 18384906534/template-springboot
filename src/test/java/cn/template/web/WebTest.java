package cn.template.web;

import cn.template.CommonTest;
import cn.template.StartApplication;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(classes = StartApplication.class)
@RunWith(SpringRunner.class)
@Transactional
public class WebTest extends CommonTest {
}