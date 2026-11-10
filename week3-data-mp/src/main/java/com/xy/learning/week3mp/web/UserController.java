package com.xy.learning.week3mp.web;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xy.learning.week3mp.entity.User;
import com.xy.learning.week3mp.mapper.UserMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserMapper userMapper;

    public UserController(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /** GET /api/users —— 查全部（selectList(null) 表示无条件） */
    @GetMapping
    public List<User> list(){
        return userMapper.selectList(null);
    }

    /** GET /api/users/1 —— 按 id 查；不存在返回 404 */
    @GetMapping("/{id}")
    public ResponseEntity<User> get(@PathVariable Long id){
        return ResponseEntity.ofNullable(userMapper.selectById(id));
    }

    /** GET /api/users/search?keyword=张 —— LambdaQueryWrapper 拼 LIKE 条件 */
    @GetMapping("/search")
    public List<User> search(@RequestParam String keyword){
        return userMapper.selectList(new LambdaQueryWrapper<User>()
                .like(User::getName,keyword));
    }

    /** GET /api/users/page?page=1&size=2 —— 分页（依赖 MybatisPlusConfig 里的分页插件） */
    @GetMapping("/page")
    public Page<User> page(@RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "2") int size) {
        return userMapper.selectPage(new Page<>(page,size),
                new LambdaQueryWrapper<User>().orderByAsc(User::getId));
    }

    /** POST /api/users —— 新增；insert 后 id 已回填到实体 */
    @PostMapping
    public User create(@RequestBody User user){
        user.setId(null);
        user.setCreatedAt(LocalDateTime.now());
        userMapper.insert(user);
        return user;
    }

    /** DELETE /api/users/1 —— 按 id 删 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        userMapper.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
