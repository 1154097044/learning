package com.xy.learning.week3rel.service;

import com.xy.learning.week3rel.dto.UserTodoCountDto;
import com.xy.learning.week3rel.dto.UserWithTodosDto;
import com.xy.learning.week3rel.entity.Todo;
import com.xy.learning.week3rel.entity.User;
import com.xy.learning.week3rel.repository.UserRepository;
import org.hibernate.LazyInitializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

/**
 * 一对多关系 Service：演示联表计数、懒加载陷阱、N+1 与修复。
 *
 * 【事务边界是懒加载的生命线】
 * 懒加载集合只在"session 还开着"（即事务内）才能初始化。open-in-view=false 时，
 * 事务一结束 session 就关，之后再碰 getTodos() 立刻抛 LazyInitializationException。
 * 所以：要在事务内把数据组装成 DTO 再返回（见 getUserWithTodos）。
 */
@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepo;

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Transactional
    public User createUser(String name) {
        return userRepo.save(new User(name));
    }

    /**
     * 给某用户加一条待办。靠 User.addTodo 维护双向关系 + cascade=ALL 级联保存，
     * 只需 save(user)，新 todo 会跟着落库（外键 user_id 由 JPA 填）。
     */
    @Transactional
    public Todo addTodo(Long userId, String title) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在: " + userId));
        Todo todo = new Todo(title);
        user.addTodo(todo);          // 设双向关系
        userRepo.save(user);         // cascade=ALL → 级联 insert todo
        return todo;
    }

    /**
     * 【正确姿势】事务内把懒加载集合读出来、组装成 DTO 返回。
     * 事务内访问 u.getTodos() 会触发一条 select 初始化集合（懒加载），合法且安全。
     */
    @Transactional(readOnly = true)
    public UserWithTodosDto getUserWithTodos(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在: " + userId));
        List<String> titles = user.getTodos().stream().map(Todo::getTitle).toList();  // 事务内初始化，OK
        return new UserWithTodosDto(user.getId(), user.getName(), titles);
    }

    /**
     * 【N+1 复现】findAll() 只发 1 条 select user；循环里每次 u.getTodos() 又各发 1 条
     * select todo → 共 1 + N 条 SQL。用户越多越灾难。跑完去看日志数 select 条数。
     */
    @Transactional(readOnly = true)
    public List<String> nPlusOne() {
        List<User> users = userRepo.findAll();                 // 1 条 select
        List<String> lines = new ArrayList<>();
        for (User u : users) {
            lines.add(u.getName() + " -> " + u.getTodos().size() + " todos");  // 每人 +1 条 select
        }
        log.info(">>> N+1 复现：{} 个用户，去看日志应为 1+{} 条 select", users.size(), users.size());
        return lines;
    }

    /**
     * 【N+1 修复】findAllWithTodos() 用 @EntityGraph 一条 LEFT JOIN 把 user+todos 全捞回，
     * 循环里 getTodos() 直接用已初始化的集合，不再发新 SQL。日志里只有 1 条 join。
     */
    @Transactional(readOnly = true)
    public List<String> nPlusOneFixed() {
        List<User> users = userRepo.findAllWithTodos();        // 1 条 join，todos 已初始化
        List<String> lines = new ArrayList<>();
        for (User u : users) {
            lines.add(u.getName() + " -> " + u.getTodos().size() + " todos");  // 不再发 SQL
        }
        log.info(">>> N+1 修复：{} 个用户，去看日志应只有 1 条 join select", users.size());
        return lines;
    }

    /**
     * 【懒加载陷阱复现】本方法【没有】@Transactional：findById 自带的事务一返回就结束、
     * session 关闭；随后在事务外碰 u.getTodos()（懒加载代理）→ LazyInitializationException。
     * 这就是 open-in-view=false 时"Controller 里直接返回实体"会炸的原因。
     */
    public String lazyBug(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在: " + userId));
        try {
            int n = user.getTodos().size();   // 事务外初始化懒加载集合 → 炸
            return "不应走到这里: " + n;
        } catch (LazyInitializationException e) {
            log.warn(">>> 懒加载陷阱复现: {}", e.getMessage());
            return "复现 LazyInitializationException（事务外访问懒加载集合）: " + e.getMessage();
        }
    }

    /** 联表计数：一条 JPQL left join + group by */
    @Transactional(readOnly = true)
    public List<UserTodoCountDto> countTodosPerUser() {
        return userRepo.countTodosPerUser();
    }
}
