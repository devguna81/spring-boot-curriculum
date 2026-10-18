package com.jqdom.curriculum.finalproject.post.internal;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jqdom.curriculum.finalproject.contract.AuthorStat;

/**
 * 단순 CRUD는 BaseMapper가, JOIN·동적 조건·JSON 필터가 필요한 질의는 PostMapper.xml이 맡는다.
 */
@Mapper
interface PostMapper extends BaseMapper<PostEntity> {

    List<PostRow> search(@Param("c") PostSearch condition);

    long count(@Param("c") PostSearch condition);

    List<AuthorStat> authorStats();
}
