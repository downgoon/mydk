package xyz.downgoon.mydk.testing;

import xyz.downgoon.mydk.concurrent.ConcurrentResourceContainer;
import xyz.downgoon.mydk.concurrent.ResourceLifecycle;

/**
 * White-Box Unified Concurrent Testing Framework
 *
 * @author downgoon@qq.com
 * @date 2019/05/08
 */

public class XrayCTFactory {

    private static final ConcurrentResourceContainer<XrayCT> container = new ConcurrentResourceContainer<>(new ResourceLifecycle<XrayCT>() {
        @Override
        public XrayCT buildResource(String name) throws Exception {
            return new XrayCT(name);
        }

        @Override
        public void destoryResource(String name, XrayCT resource) throws Exception {

        }
    });

    public static XrayCT get(String name) {
        try {
            return container.getResource(name);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
