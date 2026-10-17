package com.jqdom.curriculum.ch12;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * BaseMapper가 insert, selectById, selectList, updateById, deleteById 등을 제공한다.
 * 직접 SQL이 필요한 질의만 XML에 두고 같은 인터페이스에 선언한다.
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /** XML로 작성한 질의. BaseMapper와 한 인터페이스에서 함께 쓴다. */
    List<User> findOlderThan(@Param("age") int age);
}
