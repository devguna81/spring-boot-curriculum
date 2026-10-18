package com.jqdom.curriculum.ch01;

/**
 * /info 응답 본문. Java 16부터 정식인 record는 불변 데이터 객체를 한 줄로 선언한다.
 * Jackson이 필드명(name, javaVersion, springBootVersion)을 그대로 JSON 키로 사용한다.
 */
public record AppInfo(String name, String javaVersion, String springBootVersion) {
}
