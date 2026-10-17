package com.devguna.curriculum.finalproject.post;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.devguna.curriculum.finalproject.common.Actor;
import com.devguna.curriculum.finalproject.common.InvalidRequestException;
import com.devguna.curriculum.finalproject.contract.AuthorStat;
import com.devguna.curriculum.finalproject.contract.PageResponse;
import com.devguna.curriculum.finalproject.contract.PostRequest;
import com.devguna.curriculum.finalproject.contract.PostSummary;
import com.devguna.curriculum.finalproject.contract.Role;
import com.devguna.curriculum.finalproject.contract.UserView;
import com.devguna.curriculum.finalproject.post.api.PostApi;
import com.devguna.curriculum.finalproject.post.api.PostQuery;
import com.devguna.curriculum.finalproject.user.api.UserApi;

/**
 * 검색 시나리오. H2와 Oracle이 같은 기대값을 만족하는지 보려고 하위 클래스가 DB만 바꿔 같은 테스트를 돌린다.
 * 각 테스트는 롤백되므로 데이터가 남지 않는다.
 */
@SpringBootTest
@Transactional
abstract class PostSearchScenarios {

    @Autowired
    UserApi userApi;

    @Autowired
    PostApi postApi;

    UserView alice;
    UserView bob;
    UserView carol;

    /** Oracle 하위 클래스가 테이블을 새로 만드는 자리. */
    void prepareSchema() throws Exception {
    }

    @BeforeEach
    void seed() throws Exception {
        prepareSchema();
        alice = userApi.createAccount("alice", "password-1234", "앨리스", Role.USER);
        bob = userApi.createAccount("bob", "password-1234", "밥", Role.USER);
        carol = userApi.createAccount("carol", "password-1234", "캐롤", Role.USER);
        post(alice, "Spring Boot 입문", "의존성 주입을 정리했다", false, "spring", "입문");
        post(alice, "MyBatis 메모", "동적 SQL로 조건을 조립한다", false, "mybatis", "sql");
        post(bob, "Oracle JSON", "JSON_EXISTS 예제 100% 확인", true, "oracle", "sql");
    }

    private void post(UserView author, String title, String content, boolean pinned, String... tags) {
        postApi.create(new Actor(author.id(), author.role()), new PostRequest(title, content, List.of(tags), pinned));
    }

    private PageResponse<PostSummary> search(String keyword, Long authorId, String tag) {
        return postApi.search(new PostQuery(keyword, authorId, tag, 0, 10));
    }

    private List<String> titles(PageResponse<PostSummary> page) {
        return page.items().stream().map(PostSummary::title).toList();
    }

    @Test
    void withoutConditionsReturnsEverythingPinnedFirst() {
        PageResponse<PostSummary> page = search(null, null, null);

        assertThat(page.total()).isEqualTo(3);
        assertThat(titles(page)).containsExactly("Oracle JSON", "MyBatis 메모", "Spring Boot 입문");
        assertThat(page.items().get(0).pinned()).isTrue();
        assertThat(page.items().get(0).authorName()).isEqualTo("밥");
    }

    @Test
    void keywordMatchesTitleOrContentIgnoringCase() {
        assertThat(titles(search("spring", null, null))).containsExactly("Spring Boot 입문");
        assertThat(titles(search("조건을", null, null))).containsExactly("MyBatis 메모");
        assertThat(titles(search("ORACLE", null, null))).containsExactly("Oracle JSON");
    }

    @Test
    void percentInKeywordIsTreatedAsPlainCharacter() {
        assertThat(titles(search("100%", null, null))).containsExactly("Oracle JSON");
        assertThat(search("%", null, null).total()).isEqualTo(1);
        assertThat(search("_", null, null).total()).isEqualTo(1);
    }

    @Test
    void filtersByAuthor() {
        assertThat(search(null, alice.id(), null).total()).isEqualTo(2);
        assertThat(search(null, carol.id(), null).total()).isZero();
    }

    @Test
    void tagMustMatchExactly() {
        assertThat(search(null, null, "sql").total()).isEqualTo(2);
        assertThat(search(null, null, "sq").total()).isZero();
        assertThat(search(null, null, "입문").total()).isEqualTo(1);
    }

    @Test
    void tagWithQuoteOrPercentDoesNotBreakQuery() {
        assertThat(search(null, null, "a\"b").total()).isZero();
        assertThat(search(null, null, "%").total()).isZero();
    }

    @Test
    void conditionsAreCombinedWithAnd() {
        assertThat(titles(search("메모", alice.id(), "sql"))).containsExactly("MyBatis 메모");
        assertThat(search("메모", bob.id(), "sql").total()).isZero();
    }

    @Test
    void pagingReturnsRequestedSliceAndFullTotal() {
        PageResponse<PostSummary> first = postApi.search(new PostQuery(null, null, null, 0, 2));
        PageResponse<PostSummary> second = postApi.search(new PostQuery(null, null, null, 1, 2));

        assertThat(first.items()).hasSize(2);
        assertThat(second.items()).hasSize(1);
        assertThat(first.total()).isEqualTo(3);
        assertThat(second.total()).isEqualTo(3);
        assertThat(titles(second)).containsExactly("Spring Boot 입문");
    }

    @Test
    void summaryCarriesTagsFromJsonColumn() {
        PostSummary summary = search("mybatis", null, null).items().get(0);

        assertThat(summary.tags()).containsExactly("mybatis", "sql");
    }

    @Test
    void invalidPagingIsRejected() {
        assertThatThrownBy(() -> postApi.search(new PostQuery(null, null, null, 0, 0)))
                .isInstanceOf(InvalidRequestException.class);
        assertThatThrownBy(() -> postApi.search(new PostQuery(null, null, null, 0, 51)))
                .isInstanceOf(InvalidRequestException.class);
        assertThatThrownBy(() -> postApi.search(new PostQuery(null, null, null, -1, 10)))
                .isInstanceOf(InvalidRequestException.class);
    }

    @Test
    void authorStatsIncludeAuthorsWithoutPosts() {
        List<AuthorStat> stats = postApi.authorStats();

        assertThat(stats).extracting(AuthorStat::authorName).containsExactly("앨리스", "밥", "캐롤");
        assertThat(stats).extracting(AuthorStat::postCount).containsExactly(2L, 1L, 0L);
    }
}
