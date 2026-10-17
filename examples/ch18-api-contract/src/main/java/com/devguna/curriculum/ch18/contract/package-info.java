/**
 * 외부에 약속한 요청/응답 형식. 클라이언트가 보는 유일한 모양이다.
 * 이 패키지는 internal, web 패키지에 의존하지 않는다 (ArchitectureTests가 검사한다).
 * 필드를 지우거나 타입을 바꾸면 클라이언트가 깨지므로, 바꿀 때는 ContractSnapshotTests가 실패해 알려 준다.
 */
package com.devguna.curriculum.ch18.contract;
