package com.devguna.curriculum.ch27;

import java.time.LocalDate;

public record User(long id, String name, String email, LocalDate joinedOn) {
}
