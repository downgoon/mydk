package xyz.downgoon.mydk.process;


/**
 * @title StringLine
 * @description 行字符串包装器 
 * @author liwei39
 * @date 2014-7-2
 * @version 1.0
 */
class StringLine {

    private String line;

    public static final StringLine NULL = new StringLine(null);

    public boolean isNull() {
        return line == null;
    }

    public boolean isNotNull() {
        return line != null;
    }

    public StringLine(String line) {
        super();
        this.line = line;
    }

    public String getLine() {
        return line;
    }

}
