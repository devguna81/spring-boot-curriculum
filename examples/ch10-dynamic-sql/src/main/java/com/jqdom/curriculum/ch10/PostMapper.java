package com.jqdom.curriculum.ch10;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PostMapper {

    List<PostSummary> search(@Param("cond") PostSearch cond, @Param("offset") int offset, @Param("limit") int limit);

    /** search와 같은 조건으로 전체 건수를 센다. 조건 조각은 sql 태그로 공유한다. */
    long count(@Param("cond") PostSearch cond);

    UserWithPosts findUserWithPosts(@Param("userId") long userId);
}
