package com.jqdom.curriculum.ch19;

/** 화면에 보여 줄 사용자. active=false 이면 목록에서 "비활성"으로 표시한다. */
public record User(long id, String name, String email, int age, boolean active) { }
