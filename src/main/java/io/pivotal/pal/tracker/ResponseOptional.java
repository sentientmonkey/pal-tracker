package io.pivotal.pal.tracker;

import org.springframework.http.ResponseEntity;

import java.util.function.Consumer;

public class ResponseOptional<T> {
    T value;

    private ResponseOptional(T value) {
        this.value = value;
    }

    public static <T> ResponseOptional<T> ofNullable(T value) {
        return new ResponseOptional<>(value);
    }

    public ResponseOptional<T> let(Consumer<T> consumer) {
        if (this.value != null) {
            consumer.accept(this.value);
        }

        return this;
    }

    public ResponseEntity<T> into() {
        if (value == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(value);
        }
    }
}
