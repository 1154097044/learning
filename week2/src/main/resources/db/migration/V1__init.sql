CREATE TABLE app_todo (
                          id         BIGINT       NOT NULL AUTO_INCREMENT,
                          title       VARCHAR(100)  NOT NULL,
                          description VARCHAR(500) NOT NULL,
                          done        BOOLEAN      NOT NULL DEFAULT FALSE,
                          created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          PRIMARY KEY (id),
                          KEY idx_todo_created_at(created_at)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;