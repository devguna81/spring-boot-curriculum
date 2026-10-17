package com.devguna.curriculum.ch26.fixture.cycle.a;

import com.devguna.curriculum.ch26.fixture.cycle.b.ClassB;

/** a가 b를 쓰고 b가 다시 a를 쓰는 순환. 순환 검사 규칙이 이를 잡는지 확인하는 용도다. */
public class ClassA {
    ClassB b;
}
