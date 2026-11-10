package com.xy.learning.week1.miniioc;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Demo 1-3：手写迷你 IoC 容器（约 60 行核心逻辑）。
 *
 * 目的：不看 Spring 源码也能搞懂"@Inject 一下对象就来了"背后到底发生了什么。
 * 你在 Android 上用 Hilt，本质也是"扫描注解 → 创建实例 → 按类型注入依赖"，
 * 只不过 Hilt 在编译期用 APT 生成代码，Spring 在运行时用反射，这里的迷你版用最直白的运行时反射。
 *
 * 容器做的三件事：
 *   1. register(Class)：登记需要被管理的类（相当于组件扫描扫到 @Component）。
 *   2. getBean(Class)：按类型取实例，没有就递归创建（依赖的依赖也会被创建）。
 *   3. 单例缓存：同一个类型只创建一个实例（对应 Spring 默认 singleton 作用域）。
 *
 * 运行：直接 run 本类的 main 方法（不依赖 Spring，纯 Java）。
 */
public class MiniContainer {
    /** 已登记的类型 → Class 对象 */
    private final Map<Class<?>,Class<?>> registry = new HashMap<>();
    /** 单例缓存：类型 → 已创建实例 */
    private final Map<Class<?>, Object> singletons = new HashMap<>();

    /** 登记一个类，交给容器管理 */
    public void register(Class<?> clazz){
        registry.put(clazz,clazz);
    }

    /** 按类型获取 Bean，容器负责递归创建并注入依赖 */
    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> type){
        // 1. 命中单例缓存直接返回（这就是"单例"的含义）
        Object cached = singletons.get(type);
        if (cached != null){
            System.out.println("hit cache for: " + type);
            return (T) cached;
        }

        Class<?> clazz = registry.get(type);
        if (clazz == null){
            throw new IllegalStateException("类型未注册到容器: " + type.getName());
        }
        System.out.println("create bean: " + type);
        try{
            // 2. 选出用于注入的构造器：优先带 @MiniInject的，否则用无参构造器
            Constructor<?> ctor = resolveConstructor(clazz);
            Class<?>[] paramTypes = ctor.getParameterTypes();
            System.out.println("paramTypes: " + Arrays.toString(paramTypes));
            // 3. 递归解析每个构造器参数（依赖的依赖也会在这里被创建）
            Object[] args = new Object[paramTypes.length];
            for (int i = 0; i < paramTypes.length; i++) {
                args[i]= getBean(paramTypes[i]);
            }

            // 4. 反射创建实例并放入单例缓存
            ctor.setAccessible(true);
            T instance = (T) ctor.newInstance(args);
            singletons.put(type,instance);
            return instance;
        }catch (ReflectiveOperationException e){
            throw new IllegalStateException("创建 Bean 失败: " + type.getName(), e);
        }
    }

    private Constructor<?> resolveConstructor(Class<?> clazz) throws NoSuchMethodException{
        for(Constructor<?> ctor : clazz.getDeclaredConstructors()){
            System.out.println("try constructor: " + ctor);
            if (ctor.isAnnotationPresent(MiniInject.class)){
                return ctor;
            }
        }
        return clazz.getDeclaredConstructor(); // 回退到无参构造器
    }

    // ===== 下面是演示用的三个 Bean 与 main =====

    /** 最底层依赖：无参构造 */
    @MiniComponent
    public static class UserRepository{
        public String findUser(){
            return "user-alice";
        }
    }

    /** 中层依赖：带参构造 */
    @MiniComponent
    public static class UserService{
        private final UserRepository userRepository;

        @MiniInject
        public UserService(UserRepository userRepository){
            this.userRepository = userRepository;
        }

        public String currentUserName(){
            return userRepository.findUser();
        }
    }

    /** 顶层：构造器依赖 UserService，形成 repo → service → controller 的依赖链 */
    @MiniComponent
    public static class UserController{
        private final UserService userService;

        @MiniInject
        public UserController(UserService userService){
            this.userService = userService;
        }

        public String handle(){
            return "current user = " + userService.currentUserName();
        }
    }

    public static void main(String[] args) {
        MiniContainer container = new MiniContainer();
        // 登记（真实Spring里这一步是组件扫描自动完成的）
        container.register(UserRepository.class);
        container.register(UserService.class);
        container.register(UserController.class);

        // 只向容器要最顶层的 controller，底层依赖会被自动递归创建并注入
        UserController controller = container.getBean(UserController.class);
        System.out.println(controller.handle());

        // 验证单例：两次取到的是同一个实例
        boolean sameInstance = container.getBean(UserController.class) == controller;
        System.out.println("单例验证（两次 getBean 是否同一对象）: " + sameInstance);
    }
}
