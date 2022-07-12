package cn.template.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author licq
 * @date 2022/7/12
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @Value("${server.port}")
    private String port;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @GetMapping("/port")
    public String port() {
        return port;
    }

    @GetMapping("/contextPath")
    public String contextPath() {
        return contextPath;
    }
}

