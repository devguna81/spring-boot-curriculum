package com.jqdom.curriculum.ch10;

import java.util.ArrayList;
import java.util.List;

/**
 * 사용자 한 명과 그가 쓴 글 전체. 1:N 관계를 collection으로 매핑한다.
 * MyBatis가 JOIN 결과의 여러 행을 읽으며 posts 목록에 하나씩 채워 넣으므로 가변 클래스로 둔다.
 */
public class UserWithPosts {

    private long id;
    private String name;
    private List<PostTitle> posts = new ArrayList<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PostTitle> getPosts() {
        return posts;
    }

    public void setPosts(List<PostTitle> posts) {
        this.posts = posts;
    }

    public record PostTitle(long id, String title) {
    }
}
