package com.farmdiary.api.exception.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ErrorDetailsTest {

    private final Result<String> message = new Result<String>("message1");
    private final String details = "details1";

    @Test
    @DisplayName("getErrorDetails를 호출하면 ErrorDetails가 생성된다.")
    void call_getErrorDetails_then_return_ErrorDetails() {
        // given
        String description = "message1";
        Result<String> message = new Result<String>(description);
        String details = "details1";

        // when
        ErrorDetails errorDetails = ErrorDetails.getErrorDetails(message, details);

        // then
        assertThat(errorDetails.getMessage().getDescription()).isEqualTo(description);
        assertThat(errorDetails.getDetails()).isEqualTo(details);
    }
}