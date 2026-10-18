package com.jqdom.curriculum.finalproject.user.api;

import com.jqdom.curriculum.finalproject.contract.Role;

public record UserAccount(long id, String username, String passwordHash, String displayName, Role role) {
}
