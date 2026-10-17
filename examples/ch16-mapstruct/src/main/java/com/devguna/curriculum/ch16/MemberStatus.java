package com.devguna.curriculum.ch16;

public enum MemberStatus {
    ACTIVE("활동"),
    SUSPENDED("정지");

    private final String label;

    MemberStatus(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
