package com.teya.ledger.app.utils;

import com.teya.ledger.app.db.model.Balance;
import com.teya.ledger.lib.api.dto.BalanceDto;

public final class LedgerDtoUtils {

    private LedgerDtoUtils() {
        // Utility class
    }

    public static BalanceDto toBalanceDto(Balance src) {
        if (src == null) {
            return null;
        }
        BalanceDto retVal = new BalanceDto();
        retVal.setBalance(src.getBalance());
        retVal.setAccountId(src.getAccountId());
        return retVal;
    }

}
