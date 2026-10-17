package com.jqdom.curriculum.ch14;

/**
 * attributes는 JSON 문자열 그대로 담는다. Java 객체로 바꾸는 방법은 ch15에서 다룬다.
 */
public class Product {

    private Long id;
    private String name;
    private String attributes;

    public Product() {
    }

    public Product(String name, String attributes) {
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

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }
}
