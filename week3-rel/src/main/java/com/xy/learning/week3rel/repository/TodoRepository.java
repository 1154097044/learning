package com.xy.learning.week3rel.repository;

import com.xy.learning.week3rel.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo,Long> {
}
