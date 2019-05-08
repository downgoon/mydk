package xyz.downgoon.mydk.testing;

/**
 * White-Box Unified Concurrent Testing Framework
 *
 * @author downgoon@qq.com
 * @date 2019/05/08
 */

public class XrayCTFactory {

    private static XrayCT _INSTANCE = new XrayCT("DEFAULT");

    public static XrayCT get(String name) {
        return _INSTANCE;
    }

}
