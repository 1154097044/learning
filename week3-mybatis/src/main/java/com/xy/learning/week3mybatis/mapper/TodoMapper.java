package com.xy.learning.week3mybatis.mapper;

import com.xy.learning.week3mybatis.model.Todo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/** SQL 都在 TodoMapper.xml。写操作返回影响行数，用于判断更新/删除是否命中。 */
public interface TodoMapper {

    int insert(Todo todo);

    Todo findById(@Param("id") Long id);

    int update(Todo todo);

    int deleteById(@Param("id") Long id);

    long count(@Param("userId") Long userId,@Param("done") Boolean done,@Param("keyword") String keyword);

    List<Todo> findPage(@Param("userId") Long userId,@Param("done") Boolean done,@Param("keyword") String keyword,@Param("offset") long offset,
                        @Param("size") int size);

    List<Todo> findByUserId(@Param("userId") Long userId);

    int updateDoneBatch(@Param("ids") List<Long> ids,@Param("done") boolean done);
}
