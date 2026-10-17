package com.devguna.curriculum.ch11;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 감사 기록은 바깥 작업이 실패해 롤백되어도 남아야 한다.
 * REQUIRES_NEW는 진행 중인 트랜잭션을 잠시 멈추고 새 트랜잭션에서 실행해 따로 커밋한다.
 */
@Service
public class AuditService {

    private final AuditMapper mapper;

    public AuditService(AuditMapper mapper) {
        this.mapper = mapper;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordInNewTransaction(String message) {
        mapper.insert(message);
    }

    /** 기본 전파(REQUIRED): 바깥 트랜잭션이 있으면 거기에 참여한다. */
    @Transactional
    public void recordJoiningCaller(String message) {
        mapper.insert(message);
    }
}
