package com.xy.learning.week1.notify;


/**
 * 通知服务接口。
 * 两个实现按配置项 app.notify-channel 二选一生效（见实现类上的 @ConditionalOnProperty）。
 *
 * 对照 Android：相当于 Hilt 里用 @Binds + 自定义 Qualifier 切换实现，
 * 区别是 Spring 在运行时按条件装配，Hilt 在编译期生成代码。
 */
public interface NotificationService {

    /** 发送一条通知，返回描述实际走了哪个渠道 */
    String send(String message);
}
