package com.xy.learning.week3.web;

import com.xy.learning.week3.entity.User;
import com.xy.learning.week3.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户 REST 接口。
 *
 * 【为什么这么简单？】入门 Demo 刻意省略了第 2 周教的 Result<T> 统一响应体、
 * 参数校验 DTO、全局异常处理——那些是 Web 层关注点，本模块聚焦"数据怎么进出
 * MySQL"。等第 3 周主线 Demo（把 Todo 迁到数据库）时再把两层拼起来。
 *
 * 【Android 对照】Controller ≈ 你 App 里的"路由 + 用例"合并体：每个方法对应
 * 一个接口，浏览器/curl/http 测试文件就是调用方。
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    /** 构造器注入（教程工程原则：不用字段 @Autowired） */
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /** GET /api/users —— 查全部 */
    @GetMapping
    public List<User> list(){
        return userRepository.findAll();
    }

    /** GET /api/users/1 —— 按 id 查；不存在返回 404 */
    @GetMapping("/{id}")
    public ResponseEntity<User> get(@PathVariable Long id){
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** GET /api/users/search?keyword=张 —— 方法名派生出来的模糊查询 */
    @GetMapping("/search")
    public List<User> search(@RequestParam String keyword){
        return userRepository.findByNameContaining(keyword);
    }

    /** POST /api/users —— 新增（body: {"name":"赵六","email":"zhaoliu@example.com"}） */
    @PostMapping
    public User create(@RequestBody User user){
        user.setId(null);                           // id 交给数据库自增，忽略客户端传值
        user.setCreatedAt(LocalDateTime.now());     // 创建时间由服务端生成
        return userRepository.save(user);           // save 后返回的实体里 id 已被数据库回填
    }

    /** DELETE /api/users/1 —— 按 id 删 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
