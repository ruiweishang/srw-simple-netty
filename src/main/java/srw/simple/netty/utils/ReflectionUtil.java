package srw.simple.netty.utils;

import java.lang.reflect.AccessibleObject;

/**
 * @author shangruiwei
 * @date 2023/4/11 23:43
 */
public final class ReflectionUtil {

    private ReflectionUtil() { }

    /**
     * Try to call {@link AccessibleObject#setAccessible(boolean)} but will catch any {@link SecurityException} and
     * {@link java.lang.reflect.InaccessibleObjectException} and return it.
     * The caller must check if it returns {@code null} and if not handle the returned exception.
     */
    public static Throwable trySetAccessible(AccessibleObject object, boolean checkAccessible) {
        if (checkAccessible) {
            return new UnsupportedOperationException("Reflective setAccessible(true) disabled");
        }
        try {
            object.setAccessible(true);
            return null;
        } catch (SecurityException e) {
            return e;
        } catch (RuntimeException e) {
            return handleInaccessibleObjectException(e);
        }
    }

    private static RuntimeException handleInaccessibleObjectException(RuntimeException e) {
        // JDK 9 can throw an inaccessible object exception here; since Netty compiles
        // against JDK 7 and this exception was only added in JDK 9, we have to weakly
        // check the type
        if ("java.lang.reflect.InaccessibleObjectException".equals(e.getClass().getName())) {
            return e;
        }
        throw e;
    }
}
