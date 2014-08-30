package iminto.util.common;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 反射辅助工具
 * 
 */
public class ReflectHelper {

    /**
     * 类注解表
     */
    private Map<Class<? extends Annotation>, Annotation> annotationMap;
    /**
     * 方法表
     */
    private Map<String, Method> methodMap;
    /**
     * 方法注解表
     */
    private Map<Method, Map<Class<? extends Annotation>, Annotation>> methodAnnotationMap;
    /**
     * 字段表
     */
    private Map<String, Field> fieldMap;
    /**
     * 字段注解表
     */
    private Map<Field, Map<Class<? extends Annotation>, Annotation>> fieldAnnotationMap;
    /**
     * 缓存域名称
     */
    private static final String CACHE_DOMAIN = "org.zoeey.core.util.ReflectCacheHelper";

    /**
     * 锁定创建
     */
    private ReflectHelper(Class<?> clazz) {
        // 类注解表
        annotationMap = new HashMap<Class<? extends Annotation>, Annotation>();
        // 方法表
        methodMap = new HashMap<String, Method>();
        //  方法注解表
        methodAnnotationMap = new HashMap<Method, Map<Class<? extends Annotation>, Annotation>>();
        // 字段表
        fieldMap = new HashMap<String, Field>();
        // 字段注解表
        fieldAnnotationMap = new HashMap<Field, Map<Class<? extends Annotation>, Annotation>>();
        /**
         * getMethods 与 getDeclaredMethods 的选择:
         * 前者会给出该方法所有共有方法,而后者只给出当前类所定义的公有方法。
         * 为保证数据完整性，遂采用前者
         */
        /**
         * 方法表
         * 方法注解表
         */
        Map<Class<? extends Annotation>, Annotation> methodAnnotation;
        for (Method method : clazz.getMethods()) {
            methodMap.put(method.getName(), method);
            //
            methodAnnotation = new HashMap<Class<? extends Annotation>, Annotation>();
            for (Annotation annot : method.getAnnotations()) {
                methodAnnotation.put(annot.annotationType(), annot);
            }
            methodAnnotationMap.put(method, methodAnnotation);
        }
        /**
         * 类注解表
         */
        for (Annotation annot : clazz.getAnnotations()) {
            annotationMap.put(annot.annotationType(), annot);
        }
        /**
         * 字段表
         */
        Map<Class<? extends Annotation>, Annotation> fieldAnnotation;
        for (Field field : clazz.getFields()) {
            fieldAnnotation = new HashMap<Class<? extends Annotation>, Annotation>();
            for (Annotation annot : field.getAnnotations()) {
                fieldAnnotation.put(annot.annotationType(), annot);
            }
            fieldAnnotationMap.put(field, fieldAnnotation);
            fieldMap.put(field.getName(), field);
        }
    }

    /**
     * 获取 ReflectCacheHelper 对象。
     * @param clazz
     * @return
     */
    public static ReflectHelper get(Class<?> clazz) {
        ReflectHelper reflectCache = ObjectCacheHelper.<ReflectHelper>get(CACHE_DOMAIN, clazz);
        if (reflectCache == null) {
            synchronized (ReflectHelper.class) {
                // 避免double check提示
                ReflectHelper _cache = ObjectCacheHelper.<ReflectHelper>get(CACHE_DOMAIN, clazz);
                if (_cache == null) {
                    _cache = new ReflectHelper(clazz);
                    ObjectCacheHelper.put(CACHE_DOMAIN, clazz, _cache); // cache
                }
                reflectCache = _cache;
            }
        }
        return reflectCache;
    }

    /**
     * 获取方法表，方法名为键，方法为值
     * @return 方法表
     */
    public Map<String, Method> getMethodMap() {
        return methodMap;
    }

    /**
     * 获取类注解表
     * @return 类注解表
     */
    public Map<Class<? extends Annotation>, Annotation> getAnnotationMap() {
        return annotationMap;
    }

    /**
     * 获取方法注解表
     * @return 方法注解表
     */
    public Map<Method, Map<Class<? extends Annotation>, Annotation>> getMethodAnnotationMap() {
        return methodAnnotationMap;
    }

    /**
     * 获取类字段列表
     * @return  类字段列表
     */
    public Map<String, Field> getFieldMap() {
        return fieldMap;
    }

    /**
     * 获取字段注解表
     * @return
     */
    public Map<Field, Map<Class<? extends Annotation>, Annotation>> getFieldAnnotationMap() {
        return fieldAnnotationMap;
    }

    /**
     * 获取声明指定注解的方法
     * @param <T>   注解类型
     * @param annotationType    注解类
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends Annotation> Map<Method, T> getAnnotationPresentedMethodMap(Class<T> annotationType) {
        Map<Method, T> annotMap = new HashMap<Method, T>();
        Map<Method, Map<Class<? extends Annotation>, Annotation>> _annotMap = getMethodAnnotationMap();
        for (Entry<Method, Map<Class<? extends Annotation>, Annotation>> entry : _annotMap.entrySet()) {
            if (entry.getValue().containsKey(annotationType)) {
                annotMap.put(entry.getKey(), (T) entry.getValue().get(annotationType));
            }
        }
        return annotMap;
    }

    /**
     * 获取方法的注解
     * @param method    方法实体
     * @return  方法未定义时返回 null
     */
    public Annotation[] getMethodAnnotations(Method method) {
        Map<Class<? extends Annotation>, Annotation> annotMap = methodAnnotationMap.get(method);
        return annotMap == null ? null : annotMap.values().toArray(new Annotation[annotMap.size()]);
    }

    /**
     * 获取方法的注解
     * @param methodName    方法名
     * @return  方法未定义时返回 null
     */
    public Annotation[] getMethodAnnotations(String methodName) {
        Method method = methodMap.get(methodName);
        if (method == null) {
            return null;
        }
        return getMethodAnnotations(method);
    }

    /**
     * 获取指定方法的注解
     * @param method    方法实体
     * @return
     */
    public Map<Class<? extends Annotation>, Annotation> getMethodAnnotationMap(Method method) {
        return methodAnnotationMap.get(method);
    }

    /**
     * 获取指定方法的注解
     * @param methodName    方法名
     * @return
     */
    public Map<Class<? extends Annotation>, Annotation> getMethodAnnotationMap(String methodName) {
        return methodAnnotationMap.get(methodMap.get(methodName));
    }

    /**
     * 获取指定方法的注解类
     * @param method    方法
     * @return
     */
    @SuppressWarnings("unchecked")
    public Class<? extends Annotation>[] getMethodAnnotationTypes(Method method) {
        Collection<Annotation> anntColl = methodAnnotationMap.get(method).values();
        Iterator<Annotation> annotIterator = anntColl.iterator();
        Class<? extends Annotation>[] anntClasses = (Class<? extends Annotation>[]) new Class<?>[anntColl.size()];
        int i = 0;
        while (annotIterator.hasNext()) {
            anntClasses[i++] = annotIterator.next().annotationType();
        }
        return anntClasses;
    }

    /**
     * 获取指定方法的注解类
     * @param methodName    方法名
     * @return  方法未定义时返回 null
     */
    public Class<? extends Annotation>[] getMethodAnnotationTypes(String methodName) {
        Method method = methodMap.get(methodName);
        return method == null ? null : getMethodAnnotationTypes(method);
    }

    /**
     * 使用方法名前缀来获取方法列表
     * @param prefix    前缀，大小写敏感
     * @return 方法列表，前缀为空时返回空列表
     */
    public List<Method> getMethodByPrefix(String... prefix) {
        List<Method> methodList = new ArrayList<Method>();
        if (prefix == null) {
            return methodList;
        }
        for (Entry<String, Method> entry : methodMap.entrySet()) {
            for (int i = 0; i < prefix.length; i++) {
                if (entry.getKey().startsWith(prefix[i])) {
                    if (!methodList.contains(entry.getValue())) {
                        methodList.add(entry.getValue());
                    }
                }
            }
        }
        return methodList;
    }

    /**
     * 获取类成员取值方法<br />
     * 例如：getName() 方法对应 name 字段，isIsActive() 方法对应 isActive 字段
     *      
     * @return
     */
    public Map<String, Method> getGetMap() {
        List<Method> methodList = getMethodByPrefix("get", "is");
        Map<String, Method> map = new HashMap<String, Method>();
        if (methodList == null) {
            return map;
        }
        String fieldName = null;
        int len = 0;
        for (Method method : methodList) {
            if (method.getParameterTypes().length > 0) {
                continue;
            }
            fieldName = method.getName();
            if (fieldName.startsWith("is")) {
                /**
                 * isIsActive() -> isActive
                 */
                len = 2;
            } else {
                /**
                 * getName() -> name
                 */
                len = 3;
            }
            fieldName = StringHelper.subString(fieldName, len, -1);
            fieldName = StringHelper.uncapitalize(fieldName);
            map.put(fieldName, method);
        }
        return map;
    }

    /**
     * 获取类成员设置方法<br />
     * 例如：setName() 方法对应 name 字段,setIsActive() 方法对应 isActive 字段。
     * @return  
     */
    public Map<String, Method> getSetMap() {
        List<Method> methodList = getMethodByPrefix("set");
        Map<String, Method> map = new HashMap<String, Method>();
        String fieldName = null;
        for (Method method : methodList) {
            /**
             * getName() -> name
             */
            fieldName = StringHelper.subString(method.getName(), 3, -1);
            fieldName = StringHelper.uncapitalize(fieldName);
            map.put(fieldName, method);
        }
        return map;
    }

    /**
     * 获取声明指定注解的字段
     * @param <T>   注解类型
     * @param annotationType    注解类
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends Annotation> Map<Field, T> getAnnotationPresentedFieldMap(Class<T> annotationType) {
        Map<Field, T> annotMap = new HashMap<Field, T>();
        Map<Field, Map<Class<? extends Annotation>, Annotation>> _annotMap = getFieldAnnotationMap();
        for (Entry<Field, Map<Class<? extends Annotation>, Annotation>> entry : _annotMap.entrySet()) {
            if (entry.getValue().containsKey(annotationType)) {
                annotMap.put(entry.getKey(), (T) entry.getValue().get(annotationType));
            }
        }
        return annotMap;
    }

    /**
     * 获取字段的注解
     * @param field    字段实体
     * @return  字段未定义时返回 null
     */
    public Annotation[] getFieldAnnotations(Field field) {
        Map<Class<? extends Annotation>, Annotation> annotMap = fieldAnnotationMap.get(field);
        return annotMap == null ? null : annotMap.values().toArray(new Annotation[annotMap.size()]);
    }

    /**
     * 获取字段的注解
     * @param fieldName    字段名
     * @return  字段未定义时返回 null
     */
    public Annotation[] getFieldAnnotations(String fieldName) {
        Field field = fieldMap.get(fieldName);
        if (field == null) {
            return null;
        }
        return getFieldAnnotations(field);
    }

    /**
     * 获取指定字段的注解
     * @param field    字段实体
     * @return
     */
    public Map<Class<? extends Annotation>, Annotation> getFieldAnnotationMap(Field field) {
        return fieldAnnotationMap.get(field);
    }

    /**
     * 获取指定字段的注解
     * @param fieldName    字段名
     * @return
     */
    public Map<Class<? extends Annotation>, Annotation> getFieldAnnotationMap(String fieldName) {
        return fieldAnnotationMap.get(fieldMap.get(fieldName));
    }

    /**
     * 获取指定字段的注解类
     * @param field    字段
     * @return
     */
    @SuppressWarnings("unchecked")
    public Class<? extends Annotation>[] getFieldAnnotationTypes(Field field) {
        Collection<Annotation> anntColl = fieldAnnotationMap.get(field).values();
        Iterator<Annotation> annotIterator = anntColl.iterator();
        Class<? extends Annotation>[] anntClasses = (Class<? extends Annotation>[]) new Class<?>[anntColl.size()];
        int i = 0;
        while (annotIterator.hasNext()) {
            anntClasses[i++] = annotIterator.next().annotationType();
        }
        return anntClasses;
    }

    /**
     * 获取指定字段的注解类
     * @param fieldName    字段名
     * @return  字段未定义时返回 null
     */
    public Class<? extends Annotation>[] getFieldAnnotationTypes(String fieldName) {
        Field field = fieldMap.get(fieldName);
        return field == null ? null : getFieldAnnotationTypes(field);
    }

    /**
     * 使用字段名前缀来获取字段列表
     * @param prefix    前缀，大小写敏感
     * @return 字段列表，前缀为空时返回空列表
     */
    public List<Field> getFieldByPrefix(String... prefix) {
        List<Field> fieldList = new ArrayList<Field>();
        if (prefix == null) {
            return fieldList;
        }
        for (Entry<String, Field> entry : fieldMap.entrySet()) {
            for (int i = 0; i < prefix.length; i++) {
                if (entry.getKey().startsWith(prefix[i])) {
                    if (!fieldList.contains(entry.getValue())) {
                        fieldList.add(entry.getValue());
                    }
                }
            }
        }
        return fieldList;
    }
    
    /**
     * 根据传入的类名返回对应的Class对象
     */
    public static <T> Class<T> getType(String type) {
        try {
            return (Class<T>) Class.forName(type);
        } catch (Exception e) {
            throw new RuntimeException("Exception at li.util.Reflect.getType(String)", e);
        }
    }
    
    /**
     * 构造一个新的实例,根据类型,参数类型列表和参数列表
     */
    public static <T> T born(Class<T> type, Class[] argTypes, Object[] args) {
        try {
            return type.getConstructor(argTypes).newInstance(args);
        } catch (Exception e) {
            throw new RuntimeException("Exception at li.util.Reflect.born(Class<T>, Class<?>[], Object[])", e);
        }
    }
    
    /**
     * 构造一个新的实例,根据类型和参数列表
     */
    public static <T> T born(Class<T> type, Object... args) {
        return born(type, typesOf(args), args);
    }

    /**
     * 构造一个新的实例,根据类型名,参数类型列表和参数列表
     */
    public static <T> T born(String type, Class<?>[] argTypes, Object[] args) {
        return (T) born(getType(type), argTypes, args);
    }

    /**
     * 构造一个新的实例,根据类型名和参数列表
     */
    public static <T> T born(String type, Object... args) {
        return (T) born(getType(type), typesOf(args), args);
    }
    /**
     * 得到一个方法,根据对象类型,方法名和参数类型列表
     */
    public static Method getMethod(Class targetType, String methodName, Class... argTypes) {
        try {
            Method method = targetType.getDeclaredMethod(methodName, argTypes);// 在当前类型中查找方法
            method.setAccessible(true);// 设置可见性为true
            return method;
        } catch (Exception e) {// 在超类型中查找方法
            return Object.class == targetType.getSuperclass() ? null : getMethod(targetType.getSuperclass(), methodName, argTypes);
        }
    }

    /**
     * 执行target的method方法,以args为参数
     */
    public static Object invoke(Object target, Method method, Object... args) {
        try {
            return method.invoke(target, args);
        } catch (Exception e) {
            throw new RuntimeException("method invoking Exception", e);
        }
    }
    
    /**
     * 执行target的methodName方法,args类型需要顺序匹配于argTypes
     */
    public static Object invoke(Object target, String methodName, Class<?>[] argTypes, Object[] args) {
        return invoke(target, getMethod(target.getClass(), methodName, argTypes), args);
    }

    /**
     * 执行target的methodName方法,args为参数列表,不可以为null
     * 
     * @param args 可以没有,表示方法无参数,不可以为null
     */
    public static Object invoke(Object target, String methodName, Object... args) {
        return invoke(target, getMethod(target.getClass(), methodName, typesOf(args)), args);
    }

    /**
     * 执行targetType类型的methodName静态方法,args类型需要顺序匹配于argTypes
     */
    public static Object call(String targetType, String methodName, Class[] argTypes, Object[] args) {
        return invoke(null, getMethod(getType(targetType), methodName, argTypes), args);
    }

    /**
     * 执行targetType类型的methodName静态方法,args为参数列表,不可以为null
     */
    public static Object call(String targetType, String methodName, Object... args) {
        return invoke(null, getMethod(getType(targetType), methodName, typesOf(args)), args);
    }

    /**
     * 得到一个targetType的名为fieldName的属性,或者它的超类的
     */
    public static Field getField(Class targetType, String fieldName) {
        try {
            Field field = targetType.getDeclaredField(fieldName);// 在当前类型中得到属性
            field.setAccessible(true);// 设置可操作性为true
            return field;
        } catch (Exception e) {// 如果当前类型中无这个属性,则从其超类中查找
            return Object.class == targetType.getSuperclass() ? null : getField(targetType.getSuperclass(), fieldName);
        }
    }

    /**
     * 探测一个属性的类型,从Field或者Getter
     */
    public static Class fieldType(Class targetType, String fieldName) {
        Field field = getField(targetType, fieldName);
        if (null != field) { // 从field探测
            return field.getType();
        }
        String method = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        Method getter = getMethod(targetType, method);
        if (null != getter) {// 从getter方法探测
            return getter.getReturnType();
        }
        return null;
    }

    /**
     * 返回一些对象的类型数组
     */
    public static Class[] typesOf(Object... objects) {
        Class[] types = new Class[objects.length];
        for (int i = 0; i < objects.length; i++) {
            types[i] = objects[i].getClass();
        }
        return types;
    }

    /**
     * 超类的第index个泛型参数在子类中的实际类型
     * 
     * @param subType 子类类型
     * @param index 泛型参数索引
     */
    public static Type actualType(Class subType, Integer index) {
        try {
            return ((ParameterizedType) subType.getGenericSuperclass()).getActualTypeArguments()[index];
        } catch (Exception e) {// 不能转换为ParameterizedType或者数组越界的异常,探测他的超类
            return Object.class == subType.getSuperclass() ? null : actualType(subType.getSuperclass(), index);
        }
    }

    /**
     * 得到一个方法的参数注解列表
     */
    public static <T extends Annotation> T[] argAnnotations(Method method, Class<T> annotation) {
        T[] array = (T[]) Array.newInstance(annotation, method.getParameterTypes().length);// 生成注解数组
        Annotation[][] ats = method.getParameterAnnotations();// 所有参数注解的二维数组
        for (int i = 0; i < ats.length; i++) {// 每一个参数的注解数组
            for (Annotation at : ats[i]) {// 一个参数上的每一个注解
                if (annotation.isAssignableFrom(at.annotationType())) {// 如果注解类型是指定类型
                    array[i] = (T) at;
                    break;// 跳出当前层循环,处理下一个参数
                }
            }
        }
        return array;
    }

    /**
     * 获得传入的方法的形参名列表
     */
    public static String[] argNames(Method method) {
        return ClassTools.getParameterNames(method);
    }
         
}
