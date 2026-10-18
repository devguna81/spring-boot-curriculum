package com.jqdom.curriculum.ch15;

/** attributes 컬럼은 JSON 문자열이 아니라 ProductAttributes 객체로 다룬다. */
public class Product {

    private Long id;
    private String name;
    private ProductAttributes attributes;

    public Product() {
    }

    public Product(String name, ProductAttributes attributes) {
        this.name = name;
        this.attributes = attributes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProductAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(ProductAttributes attributes) {
        this.attributes = attributes;
    }
}
