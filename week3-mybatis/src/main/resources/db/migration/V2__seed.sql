INSERT INTO app_user(name, email)
VALUES ('Alice', 'alice@example.com'),
       ('Bob', 'bob@example.com'),
       ('Charlie', 'charlie@example.com');

INSERT INTO app_todo(user_id, title, description, done, created_at)
VALUES (1, 'First Todo', 'This is the first todo', FALSE, CURRENT_TIMESTAMP),
       (2, 'Second Todo', 'This is the second todo', FALSE, CURRENT_TIMESTAMP),
       (3, 'Third Todo', 'This is the third todo', TRUE, CURRENT_TIMESTAMP);

