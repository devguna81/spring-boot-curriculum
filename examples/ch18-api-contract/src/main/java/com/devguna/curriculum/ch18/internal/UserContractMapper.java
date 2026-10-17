package com.devguna.curriculum.ch18.internal;

import java.util.List;

import com.devguna.curriculum.ch18.contract.CreateUserRequest;
import com.devguna.curriculum.ch18.contract.UserStatus;
import com.devguna.curriculum.ch18.contract.UserView;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** 내부 객체와 외부 계약 사이의 변환. 이 경계 밖으로는 UserEntity가 나가지 않는다. */
@Mapper(componentModel = "spring")
interface UserContractMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "internalMemo", ignore = true)
    @Mapping(target = "suspended", constant = "false")
    UserEntity toEntity(CreateUserRequest request);

    @Mapping(target = "status", source = "suspended")
    UserView toView(UserEntity entity);

    List<UserView> toViews(List<UserEntity> entities);

    default UserStatus status(boolean suspended) {
        return suspended ? UserStatus.SUSPENDED : UserStatus.ACTIVE;
    }
}
