package com.xy.learning.week3rel.repository;

import com.xy.learning.week3rel.dto.UserTodoCountDto;
import com.xy.learning.week3rel.entity.User;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByName(String name);

    @EntityGraph(attributePaths = "todos")
    @Query("select u from User u")
    List<User> findAllWithTodos();

    @Query("select u.name as name,count(t.id) as todoCount "
    + "from User u left join u.todos t group by u.name")
    List<UserTodoCountDto> countTodosPerUser();
}
