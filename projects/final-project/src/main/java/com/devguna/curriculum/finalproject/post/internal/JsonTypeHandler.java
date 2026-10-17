package com.devguna.curriculum.finalproject.post.internal;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

/** Java 객체와 JSON 문자열 컬럼을 오가는 공통 TypeHandler(ch15). */
abstract class JsonTypeHandler<T> extends BaseTypeHandler<T> {

    private final JsonMapper mapper;
    private final Class<T> type;

    protected JsonTypeHandler(JsonMapper mapper, Class<T> type) {
        this.mapper = mapper;
        this.type = type;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        try {
            ps.setString(i, mapper.writeValueAsString(parameter));
        } catch (JacksonException e) {
            throw new IllegalArgumentException("JSON으로 바꿀 수 없습니다: " + type.getSimpleName(), e);
        }
    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parse(rs.getString(columnName));
    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parse(rs.getString(columnIndex));
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parse(cs.getString(columnIndex));
    }

    private T parse(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return mapper.readValue(json, type);
        } catch (JacksonException e) {
            throw new IllegalStateException("컬럼 값을 " + type.getSimpleName() + "로 읽을 수 없습니다: " + json, e);
        }
    }
}
