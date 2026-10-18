package com.jqdom.curriculum.ch15;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProductMapper {

    int insert(Product product);

    Product findById(@Param("id") long id);

    List<Product> findAll();

    long count();

    int deleteAll();
}
