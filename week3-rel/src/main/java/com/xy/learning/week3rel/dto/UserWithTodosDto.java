package com.xy.learning.week3rel.dto;

import java.util.List;

public record UserWithTodosDto(Long id, String name, List<String> todos) {
}
