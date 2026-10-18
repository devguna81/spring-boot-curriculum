package com.jqdom.curriculum.ch10;

import java.util.List;

/**
 * 검색 조건. 값이 없는(null 또는 빈) 조건은 SQL에서 통째로 빠진다.
 *
 * @param keyword  제목에 포함된 문자열
 * @param authorId 작성자 id
 * @param statuses 허용할 상태 목록 (하나라도 일치하면 포함)
 * @param sort     정렬 키. latest(기본), title 중 하나이며 그 밖의 값은 기본 정렬로 처리한다.
 */
public record PostSearch(String keyword, Long authorId, List<String> statuses, String sort) {
}
