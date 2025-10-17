package cn.template.enums;

import java.util.Objects;

/**
 * @author licq
 * @version 1.0
 * @date 2025/10/15 11:20
 */
public enum SubjectEnum {
    XSX( "15040","习近平新时代中国特色社会主义思想概论","习思想概论")
    ,JXS("15043","中国近现代史纲要","中近现史纲要")

    ;

    private String code;
    private String info;
    private String abbreviation;

    SubjectEnum(String code,String info,String abbreviation) {
        this.code = code;
        this.info = info;
        this.abbreviation = abbreviation;
    }

    public String getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public static SubjectEnum getByCode(String code){
        for (SubjectEnum value : SubjectEnum.values()) {
            if (Objects.equals(code,value.code)) {
                return value;
            }
        }
        return null;
    }
}
