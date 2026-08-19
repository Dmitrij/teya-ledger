package com.teya.ledger.lib.api.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public final class ApiErrorDto {

    @SerializedName("status")
    public int status;

    @SerializedName("message")
    public String message;

    @SerializedName("timestamp")
    public LocalDateTime timestamp;

    @SerializedName("errors")
    public Map<String, String> errors;

    public ApiErrorDto() {
    }

    public ApiErrorDto(int status, String message, LocalDateTime timestamp, Map<String, String> errors) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
        this.errors = errors;
    }

}
