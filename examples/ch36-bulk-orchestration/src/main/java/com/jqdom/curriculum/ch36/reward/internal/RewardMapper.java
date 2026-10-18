package com.jqdom.curriculum.ch36.reward.internal;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
interface RewardMapper {

    List<LedgerRow> ledgerByMemberIds(@Param("ids") List<Long> ids);
}
