package cn.template;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * @author licq
 * @version 1.0
 * @date 2025/10/15 12:46
 */
public class CommonTest {

    public String fileJsonStringReader(String path){
        try {

            BufferedReader reader = new BufferedReader(new FileReader(path));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            reader.close();
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }
}
