-- Flyway 版本化建表脚本（MP 版独立库 learn_mp，内容与 JPA 版 week3-data 完全一致）
-- 应用启动时 Flyway 自动执行；已执行过的脚本一个字都不能改，改动要新建 V2__xxx.sql

-- 用户表：user 是 MySQL 保留字，表名用 app_user
CREATE TABLE app_user (
                          id         BIGINT       NOT NULL AUTO_INCREMENT,
                          name       VARCHAR(50)  NOT NULL,
                          email      VARCHAR(100) NOT NULL,
                          created_at DATETIME     NOT NULL,
                          PRIMARY KEY (id),
                          UNIQUE KEY uk_user_email (email)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;
