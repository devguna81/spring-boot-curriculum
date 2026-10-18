package com.jqdom.curriculum.ch10;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PostMapperTests {

    @Autowired
    PostMapper mapper;

    private PostSearch cond(String keyword, Long authorId, List<String> statuses, String sort) {
        return new PostSearch(keyword, authorId, statuses, sort);
    }

    private List<Long> ids(PostSearch cond) {
        return mapper.search(cond, 0, 100).stream().map(PostSummary::id).toList();
    }

    @Test
    void noConditionReturnsEverythingNewestFirst() {
        PostSearch all = cond(null, null, null, null);

        assertEquals(12, mapper.count(all));
        assertEquals(List.of(12L, 11L, 10L, 9L, 8L, 7L, 6L, 5L, 4L, 3L, 2L, 1L), ids(all));
    }

    @Test
    void blankKeywordAndEmptyListAreTreatedAsNoCondition() {
        assertEquals(12, mapper.count(cond("", null, List.of(), null)));
    }

    @Test
    void keywordFiltersByTitle() {
        assertEquals(List.of(12L, 8L, 4L, 1L), ids(cond("Spring", null, null, null)));
    }

    @Test
    void authorFilter() {
        assertEquals(List.of(5L, 4L, 3L, 2L, 1L), ids(cond(null, 1L, null, null)));
    }

    @Test
    void foreachBuildsInClause() {
        assertEquals(7, mapper.count(cond(null, null, List.of("PUBLISHED"), null)));
        assertEquals(10, mapper.count(cond(null, null, List.of("PUBLISHED", "DRAFT"), null)));
    }

    @Test
    void conditionsAreCombinedWithAnd() {
        assertEquals(List.of(8L, 4L, 1L), ids(cond("Spring", null, List.of("PUBLISHED"), null)));
        assertEquals(List.of(4L, 2L, 1L), ids(cond(null, 1L, List.of("PUBLISHED"), null)));
    }

    @Test
    void noMatchReturnsEmptyList() {
        PostSearch none = cond("없는제목", null, null, null);

        assertEquals(0, mapper.count(none));
        assertTrue(mapper.search(none, 0, 10).isEmpty());
    }

    @Test
    void sortByTitleUsesWhitelist() {
        List<String> titles = mapper.search(cond(null, null, List.of("DRAFT"), "title"), 0, 10)
                .stream().map(PostSummary::title).toList();

        assertEquals(List.of("CI 정리", "트랜잭션 메모", "페이징 메모"), titles);
    }

    @Test
    void unknownSortFallsBackToDefaultInsteadOfBeingInjected() {
        PostSearch hostile = cond(null, null, null, "id; DROP TABLE posts");

        assertEquals(12L, ids(hostile).get(0));
        assertEquals(12, mapper.count(cond(null, null, null, null)));
    }

    @Test
    void offsetAndLimitSlicePages() {
        PostSearch all = cond(null, null, null, null);

        assertEquals(List.of(12L, 11L, 10L, 9L, 8L), mapper.search(all, 0, 5).stream().map(PostSummary::id).toList());
        assertEquals(List.of(7L, 6L, 5L, 4L, 3L), mapper.search(all, 5, 5).stream().map(PostSummary::id).toList());
        assertEquals(List.of(2L, 1L), mapper.search(all, 10, 5).stream().map(PostSummary::id).toList());
    }

    @Test
    void joinFillsNestedAuthor() {
        PostSummary first = mapper.search(cond(null, null, null, null), 0, 1).get(0);

        assertEquals(new PostSummary(12, "Spring 배포", "ARCHIVED", new PostSummary.Author(3, "park")), first);
    }

    @Test
    void collectionGroupsPostsUnderOneUser() {
        UserWithPosts kim = mapper.findUserWithPosts(1);

        assertEquals("kim", kim.getName());
        assertEquals(5, kim.getPosts().size());
        assertEquals("Spring Boot 시작", kim.getPosts().get(0).title());
    }

    @Test
    void missingUserIsNull() {
        assertNull(mapper.findUserWithPosts(999));
    }
}
