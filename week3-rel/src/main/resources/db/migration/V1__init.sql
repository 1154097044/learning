-- 用户表（一方）
CREATE TABLE app_user (
                          id   BIGINT      NOT NULL AUTO_INCREMENT,
                          name VARCHAR(50) NOT NULL,
                          PRIMARY KEY (id),
                          UNIQUE KEY uk_user_name (name)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- 待办表（多方）：user_id 外键指向 app_user，关系由"多方"持有外键列维护
CREATE TABLE app_todo (
                          id      BIGINT       NOT NULL AUTO_INCREMENT,
                          title   VARCHAR(100) NOT NULL,
                          done    BOOLEAN      NOT NULL DEFAULT FALSE,
                          user_id BIGINT       NOT NULL,
                          PRIMARY KEY (id),
                          KEY idx_todo_user (user_id),
                          CONSTRAINT fk_todo_user FOREIGN KEY (user_id) REFERENCES app_user (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;
