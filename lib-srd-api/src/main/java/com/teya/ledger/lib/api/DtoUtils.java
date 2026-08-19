package com.teya.ledger.lib.api;

import com.teya.ledger.lib.api.dto.BalanceDto;

public final class DtoUtils {

    private DtoUtils() {
        // Utility Class
    }

    public static long asBalance(BalanceDto balanceDto) {
        return balanceDto == null ? -1L : balanceDto.getBalance();
    }
    
}
