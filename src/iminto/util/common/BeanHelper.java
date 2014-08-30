package iminto.util.common;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Bean帮助类
 */
public class BeanHelper {

    /**
     * <pre>
     * 将Bean转换为Map
     * 获取所有以get和is起始的可读属性
     * 例如：getName() 方法对应 name 字段
     *      isIsActive() 方法对应 isActive 字段
     * 注意：此方法内<b>包含</b>getClass()转换
     * </pre>
     * @see ReflectHelper#getBeanGetMap()
     * @param obj
     * @return 
     */
    public static Map<String, Object> toMap(Object obj) {
        return toMap(obj, true);
    }

    /**
     * <pre>
     * 将Bean转换为Map
     * 获取所有以get和is起始的可读属性
     * 例如：getName() 方法对应 name 字段
     *      isIsActive() 方法对应 isActive 字段
     * 注意：此方法内<b>不包含</b>getClass()转换
     * </pre>
     * @see ReflectHelper#getBeanGetMap()
     * @param obj
     * @return
     */
    public static Map<String, Object> toMapIgnoreClass(Object obj) {
        return toMap(obj, false);
    }

    private static Map<String, Object> toMap(Object obj, boolean isNeedClassName) {
        Map<String, Object> map = new HashMap<String, Object>();
        ReflectHelper refHelper = ReflectHelper.get(obj.getClass());
        Map<String, Method> beanMap = refHelper.getGetMap();
        try {
            String key = null;
            for (Entry<String, Method> entry : beanMap.entrySet()) {
                key = entry.getKey();
                if (!isNeedClassName && "class".equals(key)) {
                    continue;
                }
                map.put(key, entry.getValue().invoke(obj, new Object[0]));
            }
        } catch (IllegalAccessException ex) {
            Logger.getLogger(BeanHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(BeanHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(BeanHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return map;
    }

    /**
     * <pre>
     * 将Map转换为Beans，异常时返回null。
     * 类需要有无参数构造函数，可写属性不可重载，防止在参数为null时的异常。
     * </pre>
     * @see ReflectHelper#getBeanSetMap()
     * @param map  
     * @param clazz 
     * @return
     */
    public static Object fromMap(Map<String, Object> map, Class<?> clazz) {
        Object obj = null;
        ReflectHelper refHelper = ReflectHelper.get(clazz);
        Map<String, Method> beanMap = refHelper.getSetMap();
        try {
            obj = clazz.newInstance();
            String key;
            for (Entry<String, Method> entry : beanMap.entrySet()) {
                key = entry.getKey();
                if (map.containsKey(key)) {
                    entry.getValue().invoke(obj, new Object[]{map.get(key)});
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(BeanHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return obj;
    }

    /**
     * 将Map转换为Beans,异常时返回null<br />
     * 类需要有无参数构造函数，可写属性不可重载，防止在参数为null时的异常。
     * @see ReflectHelper#getBeanSetMap()
     * @param map
     * @return
     */
    public static Object fromMap(Map<String, Object> map) {
        Object obj = null;

        do {
            String className = (String) map.get("class");
            if (StringHelper.isEmpty(className)) {
                break;
            }
            Class<?> clazz;
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException ex) {
                break;
            }
            obj = fromMap(map, clazz);
        } while (false);

        return obj;
    }
}
