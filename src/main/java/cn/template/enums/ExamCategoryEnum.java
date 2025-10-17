package cn.template.enums;

import java.util.Objects;

/**
 * @author licq
 * @version 1.0
 * @date 2025/10/15 11:20
 */
public enum ExamCategoryEnum {
    MODULE(1,"章节练习",DataCategoryEnum.MODULE.getInfo())
    ,COURSE(2,"每课一练",DataCategoryEnum.COURSE.getInfo())
    ,SIMULATION(3,"模拟试卷",DataCategoryEnum.SIMULATION.getInfo())
    ,REAL(4,"历年真题",DataCategoryEnum.REAL.getInfo())
    ,SIMULATION2(99,"模拟生成","")
    ;


    private Integer code;
    private String info;
    private String matchName;

    ExamCategoryEnum(Integer code,String info,String matchName) {
        this.code = code;
        this.info = info;
        this.matchName = matchName;
    }

    public Integer getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }

    public String getMatchName() {
        return matchName;
    }

    public static ExamCategoryEnum getByCode(Integer code){
        for (ExamCategoryEnum value : ExamCategoryEnum.values()) {
            if (Objects.equals(code,value.code)) {
                return value;
            }
        }
        return null;
    }

    public static ExamCategoryEnum getByMatchName(String matchName){
        if (matchName!=null && !matchName.isEmpty()) {
            for (ExamCategoryEnum examCategoryEnum : ExamCategoryEnum.values()) {
                if(Objects.equals(matchName,examCategoryEnum.matchName)){
                    return examCategoryEnum;
                }
            }
        }
        return null;
    }

}
