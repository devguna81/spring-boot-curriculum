package com.jqdom.curriculum.ch24.adapter.address;

/**
 * 외부 주소 API가 내려주는 모양 그대로다. 필드 이름이 우리 도메인과 다르다(zipcode, sido, sigungu, road).
 * 이 타입은 어댑터 안에서만 쓰고 바깥으로 내보내지 않는다.
 */
record ExternalAddressResponse(String zipcode, String sido, String sigungu, String road) {
}
