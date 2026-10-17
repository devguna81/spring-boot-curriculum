package com.devguna.curriculum.finalproject.user.internal;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/** 단순 CRUD만 필요해서 BaseMapper를 그대로 쓴다. */
@Mapper
interface UserMapper extends BaseMapper<UserEntity> {
}
