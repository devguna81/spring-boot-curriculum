package com.jqdom.curriculum.ch11;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AccountMapper {

    @Select("SELECT id, owner, balance FROM accounts WHERE id = #{id}")
    Account findById(@Param("id") long id);

    /** 잔액이 충분할 때만 차감한다. 반환값 0은 계좌가 없거나 잔액이 부족하다는 뜻이다. */
    @Update("UPDATE accounts SET balance = balance - #{amount} WHERE id = #{id} AND balance >= #{amount}")
    int withdraw(@Param("id") long id, @Param("amount") long amount);

    /** 반환값 0은 해당 계좌가 없다는 뜻이다. */
    @Update("UPDATE accounts SET balance = balance + #{amount} WHERE id = #{id}")
    int deposit(@Param("id") long id, @Param("amount") long amount);
}
