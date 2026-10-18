package com.jqdom.curriculum.ch36.member.internal;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
interface MemberMapper {

    List<MemberRow> findByIds(@Param("ids") List<Long> ids);

    List<Long> idsAfter(@Param("lastId") long lastId, @Param("limit") int limit);
}
