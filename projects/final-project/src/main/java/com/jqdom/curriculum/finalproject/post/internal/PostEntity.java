package com.jqdom.curriculum.finalproject.post.internal;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/** fp_posts의 한 행. autoResultMap을 켜야 meta 조회에도 TypeHandler가 적용된다. */
@TableName(value = "fp_posts", autoResultMap = true)
class PostEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long authorId;
    private String title;
    private String content;
    private Boolean pinned;
    @TableField(typeHandler = PostMetaTypeHandler.class)
    private PostMeta meta;
    private LocalDateTime createdAt;

    PostEntity() {
    }

    PostEntity(Long authorId, String title, String content, boolean pinned, PostMeta meta) {
        this.authorId = authorId;
        this.title = title;
        this.content = content;
        this.pinned = pinned;
        this.meta = meta;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getPinned() {
        return pinned;
    }

    public void setPinned(Boolean pinned) {
        this.pinned = pinned;
    }

    public PostMeta getMeta() {
        return meta;
    }

    public void setMeta(PostMeta meta) {
        this.meta = meta;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
