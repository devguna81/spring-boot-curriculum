package com.jqdom.curriculum.ch15;

import org.apache.ibatis.type.MappedTypes;
import tools.jackson.databind.json.JsonMapper;

import org.springframework.stereotype.Component;

/**
 * Bean으로 등록하면 MyBatis 스타터가 TypeHandler로 등록한다.
 * Boot가 구성한 JsonMapper를 주입받으므로 웹 계층과 같은 Jackson 설정을 쓴다.
 */
@Component
@MappedTypes(ProductAttributes.class)
public class ProductAttributesTypeHandler extends JsonTypeHandler<ProductAttributes> {

    public ProductAttributesTypeHandler(JsonMapper mapper) {
        super(mapper, ProductAttributes.class);
    }
}
