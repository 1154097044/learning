package com.xy.learning.week3mybatis.mapper;

import com.xy.learning.week3mybatis.dto.UserTodoCount;
import com.xy.learning.week3mybatis.model.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 原生 Mapper 不继承 BaseMapper，每个方法都要有对应 SQL。
 * XML namespace = 本接口全限定名，statement id = 方法名；@Param 名与 #{...} 一致。
 */
public interface UserMapper {

    int insert(User user);

    User findById(@Param("id") Long id);

    User findByEmail(@Param("email") String email);

    List<User> findAll();

    List<User> findAllWithTodos();

    List<UserTodoCount> countTodosPerUser();
}
