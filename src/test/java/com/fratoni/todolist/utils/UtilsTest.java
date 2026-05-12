package com.fratoni.todolist.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UtilsTest {

    static class Sample {
        private String name;
        private String email;

        Sample(String name, String email) {
            this.name = name;
            this.email = email;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    @Test
    void shouldCopyNonNullProperties() {
        var source = new Sample("Alice", "alice@email.com");
        var target = new Sample("Bob", "bob@email.com");

        Utils.copyNonNullProperties(source, target);

        assertThat(target.getName()).isEqualTo("Alice");
        assertThat(target.getEmail()).isEqualTo("alice@email.com");
    }

    @Test
    void shouldNotOverwriteWithNull() {
        var source = new Sample("Alice", null);
        var target = new Sample("Bob", "bob@email.com");

        Utils.copyNonNullProperties(source, target);

        assertThat(target.getName()).isEqualTo("Alice");
        assertThat(target.getEmail()).isEqualTo("bob@email.com");
    }

    @Test
    void shouldReturnNullPropertyNames() {
        var source = new Sample(null, "test@email.com");

        var nullNames = Utils.getNullPropertyNames(source);

        assertThat(nullNames).contains("name");
        assertThat(nullNames).doesNotContain("email");
    }
}
