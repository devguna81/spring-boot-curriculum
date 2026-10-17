package com.jqdom.curriculum.ch36.order.internal;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
interface OrderMapper {

    List<OrderCountRow> countByMemberIds(@Param("ids") List<Long> ids);
}
