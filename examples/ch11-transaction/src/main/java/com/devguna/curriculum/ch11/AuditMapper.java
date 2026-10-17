package com.devguna.curriculum.ch11;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AuditMapper {

    @Insert("INSERT INTO audit_log (message) VALUES (#{message})")
    int insert(@Param("message") String message);

    @Select("SELECT message FROM audit_log ORDER BY id")
    List<String> findAll();
}
