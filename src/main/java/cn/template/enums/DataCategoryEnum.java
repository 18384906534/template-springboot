package cn.template.enums;

/**
 * @author licq
 * @version 1.0
 * @date 2025/10/15 11:20
 */
public enum DataCategoryEnum {
    MODULE("【1.1】章节练习")
    ,COURSE("【2.1】每课一练")
    ,SIMULATION("【3.1】模拟考试")
    ,REAL("【4.1】历年真题")

    ;


    private String info;

    DataCategoryEnum(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }

    public static DataCategoryEnum match(String content){
        if (content!=null && !content.isEmpty()) {
            for (DataCategoryEnum dataCategoryEnum : DataCategoryEnum.values()) {
                if (content.contains(dataCategoryEnum.info)) {
                    return dataCategoryEnum;
                }
            }
        }
        return null;
    }
}
