package com.devguna.curriculum.ch29;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * SQL은 resources/mapper/UserMapper.xml에 있고, 이 인터페이스의 메서드 이름이 XML의 id와 이어진다.
 * 구현 클래스는 만들지 않는다. MyBatis가 실행 시점에 프록시를 만든다.
 */
@Mapper
public interface UserMapper {

    /** 생성된 id가 user.id에 채워진다. 반환값은 영향받은 행 수다. */
    int insert(User user);

    User findById(@Param("id") long id);

    List<User> findAll();

    int update(User user);

    int deleteById(@Param("id") long id);

    /** 짧은 SQL은 애노테이션으로도 쓸 수 있다. 긴 SQL은 XML이 읽기 쉽다. */
    @Select("SELECT COUNT(*) FROM users")
    int count();
}
