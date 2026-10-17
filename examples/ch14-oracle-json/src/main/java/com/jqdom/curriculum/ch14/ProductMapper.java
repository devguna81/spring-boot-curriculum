package com.jqdom.curriculum.ch14;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/** Oracle의 JSON 함수를 쓰는 질의 모음. H2에서는 실행할 수 없다. */
@Mapper
public interface ProductMapper {

    int insert(Product product);

    int insertClob(Product product);

    Product findById(@Param("id") long id);

    Product findClobById(@Param("id") long id);

    /** JSON_VALUE: 경로의 스칼라 값 하나를 꺼낸다. */
    String findBrand(@Param("id") long id);

    /** JSON_QUERY: 경로의 객체나 배열을 JSON 텍스트로 꺼낸다. */
    String findSpecs(@Param("id") long id);

    /** JSON_VALUE로 숫자를 비교한다. RETURNING NUMBER를 빼면 문자열로 비교한다. */
    List<Product> findPriceOver(@Param("min") int min);

    /** RETURNING NUMBER 없이 비교한 결과. JSON_VALUE는 기본이 문자열이라 사전순으로 비교된다. */
    List<Product> findPriceOverComparedAsText(@Param("min") String min);

    /** JSON_EXISTS: 조건에 맞는 값이 있는 행만 고른다. */
    List<Product> findByTag(@Param("tag") String tag);

    /** JSON_TABLE: 배열의 각 원소를 행으로 펼친다. */
    List<String> findTags(@Param("id") long id);

    long count();

    int deleteAll();
}
