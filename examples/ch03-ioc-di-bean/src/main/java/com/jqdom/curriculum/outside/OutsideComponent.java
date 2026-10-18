package com.jqdom.curriculum.outside;

import org.springframework.stereotype.Component;

/**
 * 컴포넌트 스캔 범위(com.jqdom.curriculum.ch03) 밖에 있다. {@code @Component}가 붙어 있어도
 * 기본 설정에서는 Bean으로 등록되지 않는다.
 */
@Component
public class OutsideComponent {
}
