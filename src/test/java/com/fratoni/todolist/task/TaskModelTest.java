package com.fratoni.todolist.task;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TaskModelTest {

    @Test
    void shouldThrowExceptionWhenTitleExceeds50Chars() {
        var task = new TaskModel();

        assertThatThrownBy(() -> task.setTitle("A".repeat(51)))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("50 caracteres");
    }

    @Test
    void shouldAcceptTitleWithLessThan50Chars() {
        var task = new TaskModel();

        assertThatNoException().isThrownBy(() -> task.setTitle("Valid title"));
    }

    @Test
    void shouldAcceptTitleWithExactly50Chars() {
        var task = new TaskModel();

        assertThatNoException().isThrownBy(() -> task.setTitle("A".repeat(50)));
    }
}
