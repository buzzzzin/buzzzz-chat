package in.buzzzz.utils;

import java.util.List;
import java.util.Map;

/**
 * @author jitendra on 26/9/15.
 */
public class ObjectUtils {

    public static boolean isEmptyMap(Map map) {
        return (isEmptyObject(map) || map.isEmpty());
    }

    public static boolean isNotEmptyMap(Map map) {
        return (!isEmptyMap(map));
    }

    public static boolean isEmptyObject(Object object){
        return (object == null);
    }

    public static boolean isNotEmptyObject(Object object){
        return (!isEmptyObject(object));
    }

    public static boolean isEmptyList(List list) {
        return (isEmptyObject(list) || list.isEmpty());
    }

    public static boolean isNotEmptyList(List list) {
        return !isEmptyList(list);
    }
}
