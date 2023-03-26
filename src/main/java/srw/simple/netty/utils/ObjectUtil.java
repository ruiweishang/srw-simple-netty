package srw.simple.netty.utils;

/**
 * @author shangruiwei
 * @date 2023/3/26 14:14
 */
public class ObjectUtil {

    public static <T> T checkNotNull(T arg, String text) {
        if (arg == null) {
            throw new NullPointerException(text);
        }
        return arg;
    }
}
