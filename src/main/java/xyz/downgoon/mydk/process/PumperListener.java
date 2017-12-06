package xyz.downgoon.mydk.process;


import java.io.IOException;

/**
 * @title PumperListener
 * @description TODO 
 * @author liwei39
 * @date 2014-7-3
 * @version 1.0
 */
public interface PumperListener {

    /**
     * 文件开头
     * */
    public void onReadBegin(String pumperName);

    /**
     * 读取一行
     * */
    public void onReadLine(String pumperName, String lineText, int lineNumber);

    /**
     * 文件结束
     * */
    public void onReadEnd(String pumperName, int totalLines);

    /**
     * 读取遇到异常
     * */
    public void onReadException(String pumperName, IOException ioe);

}
