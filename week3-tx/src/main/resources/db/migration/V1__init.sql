CREATE TABLE account (
                         id      BIGINT        NOT NULL AUTO_INCREMENT,
                         name    VARCHAR(50)   NOT NULL,
                         balance DECIMAL(10,2) NOT NULL DEFAULT 0.00,
                         PRIMARY KEY (id),
                         UNIQUE KEY uk_account_name (name)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;