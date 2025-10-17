package cn.template.enums;

/**
 * @author licq
 * @version 1.0
 * @date 2025/10/15 11:20
 */
public enum QuestionCategoryEnum {
    DX( "1","单选","single-choice")
    ,JS("5","简述","short-answer")
    ,LS("9","论述","short-answer")

    ;

    private String code;
    private String info;
    private String cssStyle;

    QuestionCategoryEnum(String code, String info,String cssStyle) {
        this.code = code;
        this.info = info;
        this.cssStyle = cssStyle;
    }

    public String getCode() {
        return code;
    }
    public String getCssStyle() {
        return cssStyle;
    }
}
