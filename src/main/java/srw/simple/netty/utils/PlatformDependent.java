package srw.simple.netty.utils;

import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * @author shangruiwei
 * @date 2023/4/11 23:37
 */
public class PlatformDependent {

    public static ClassLoader getSystemClassLoader() {
        if (System.getSecurityManager() == null) {
            return ClassLoader.getSystemClassLoader();
        } else {
            return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
                @Override
                public ClassLoader run() {
                    return ClassLoader.getSystemClassLoader();
                }
            });
        }
    }
}
