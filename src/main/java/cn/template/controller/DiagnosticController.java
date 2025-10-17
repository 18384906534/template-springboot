package cn.template.controller;

/**
 * @author licq
 * @version 1.0
 * @date 2025/10/15 18:50
 */
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.File;
import java.util.Arrays;

@Controller
public class DiagnosticController {

    @Autowired
    private ServletContext servletContext;

    @GetMapping("/diag")
    @ResponseBody
    public String diagnostic() {
        StringBuilder result = new StringBuilder();
        result.append("=== Spring Boot JSP 诊断信息 ===<br/>");

        // 1. 检查ServletContext路径
        String realPath = servletContext.getRealPath("/");
        result.append("1. ServletContext真实路径: ").append(realPath).append("<br/>");

        // 2. 检查webapp目录
        File webappDir = new File("src/main/webapp");
        result.append("2. 源码webapp目录存在: ").append(webappDir.exists()).append("<br/>");
        if (webappDir.exists()) {
            result.append("3. Webapp目录内容: ").append(Arrays.toString(webappDir.list())).append("<br/>");
        }

        // 3. 检查target目录
        File targetDir = new File("target");
        result.append("4. Target目录存在: ").append(targetDir.exists()).append("<br/>");
        if (targetDir.exists()) {
            result.append("5. Target目录内容: ").append(Arrays.toString(targetDir.list())).append("<br/>");

            // 检查war解压目录
            File warDir = new File("target/zk-exam");
            result.append("6. WAR解压目录存在: ").append(warDir.exists()).append("<br/>");
            if (warDir.exists()) {
                result.append("7. WAR目录内容: ").append(Arrays.toString(warDir.list())).append("<br/>");

                // 检查views目录
                File viewsDir = new File("target/zk-exam/views");
                result.append("8. Views目录存在: ").append(viewsDir.exists()).append("<br/>");
                if (viewsDir.exists()) {
                    result.append("9. Views目录内容: ").append(Arrays.toString(viewsDir.list())).append("<br/>");
                }
            }
        }

        // 4. 检查classes目录
        File classesDir = new File("target/classes");
        result.append("10. Classes目录存在: ").append(classesDir.exists()).append("<br/>");
        if (classesDir.exists()) {
            result.append("11. Classes目录内容: ").append(Arrays.toString(classesDir.list())).append("<br/>");
        }

        return result.toString();
    }
}
