package com.devguna.curriculum.finalproject.user.api;

import com.devguna.curriculum.finalproject.contract.Role;

public record UserAccount(long id, String username, String passwordHash, String displayName, Role role) {
}
