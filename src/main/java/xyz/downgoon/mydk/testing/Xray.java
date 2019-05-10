package xyz.downgoon.mydk.testing;

/**
 * White-Box Unified Concurrent Testing Framework
 *
 * @author downgoon@qq.com
 * @date 2019/05/08
 */

public interface Xray {

    /**
     * a breakpoint on concurrent testing environments
     */
    void dot(String dotName);

    static Xray xray(String name) {
        return XrayCTFactory.get(name);
    }

    static Xray xray(Class anyClass) {
        return XrayCTFactory.get(anyClass.getName());
    }

}
