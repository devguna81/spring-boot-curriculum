package com.jqdom.curriculum.finalproject.user.internal;

import org.mapstruct.Mapper;

import com.jqdom.curriculum.finalproject.contract.UserView;
import com.jqdom.curriculum.finalproject.user.api.UserAccount;

/** 저장용 객체를 계약 타입으로 바꾼다. 대상에 채우지 못하는 필드가 있으면 컴파일이 실패한다. */
@Mapper(componentModel = "spring")
interface UserConverter {

    UserView toView(UserEntity entity);

    UserAccount toAccount(UserEntity entity);
}
