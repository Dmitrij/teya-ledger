package com.teya.ledger.lib.api.dto;

import com.google.gson.annotations.SerializedName;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public final class BalanceDto {

    @NotNull
    @SerializedName("accountId")
    public String accountId;

    @NotNull
    @SerializedName("balance")
    public Long balance;

    public BalanceDto() {
    }

    public BalanceDto(String accountId, Long balance) {
        this.accountId = accountId;
        this.balance = balance;
    }


}
