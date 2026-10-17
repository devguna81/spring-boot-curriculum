package com.devguna.curriculum.ch13;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/** H2와 Oracle에서 같은 XML로 동작하도록 표준 SQL만 쓴다. */
@Mapper
public interface UserMapper {

    int insert(User user);

    User findById(@Param("id") long id);

    List<User> findPage(@Param("offset") int offset, @Param("limit") int limit);

    long count();

    int deleteAll();
}
