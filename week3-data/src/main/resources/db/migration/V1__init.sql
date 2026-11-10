-- Flyway 版本化建表脚本（文件名约定：V<版本号>__<描述>.sql，注意是两个下划线）
-- 应用启动时 Flyway 自动按版本号顺序执行；已执行过的脚本一个字都不能改，
-- 后续改表结构要新建 V2__xxx.sql、V3__xxx.sql……

-- 用户表：user 是 MySQL 保留字，表名用 app_user
CREATE TABLE app_user (
                          id         BIGINT       NOT NULL AUTO_INCREMENT,
                          name       VARCHAR(50)  NOT NULL,
                          email      VARCHAR(100) NOT NULL,
                          created_at DATETIME     NOT NULL,
                          PRIMARY KEY (id),
                          UNIQUE KEY uk_user_email (email)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;
