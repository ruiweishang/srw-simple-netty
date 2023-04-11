package srw.simple.netty.utils;

/**
 * @author shangruiwei
 * @date 2023/4/11 16:43
 */
public class LogUtil {

    public static <T> void log(Class<T> c, String content) {
        System.out.println(String.format("class %s,content %s", c.getName(), content));
    }
}
