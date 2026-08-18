package com.teya.ledger.lib.api.dto;

import com.google.gson.annotations.SerializedName;
import com.teya.ledger.lib.api.type.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public final class TransactionDto {

    @NotNull
    @SerializedName("type")
    private TransactionType type;

    @NotNull
    @Positive
    @SerializedName("amount")
    private Long amount;

    public TransactionDto() {
    }

    public TransactionDto(TransactionType type, Long amount) {
        this.type = type;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return String.format("TransactionDto{type=%s, amount=%s}", type, amount);
    }

}
