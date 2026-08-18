package com.teya.ledger.app.service;

import com.teya.ledger.app.db.model.Transaction;
import com.teya.ledger.lib.api.dto.TransactionDto;
import org.jspecify.annotations.NonNull;

import java.util.List;

public interface LedgerService {

    /**
     * QUERY SIDE: View transaction history
     */
    List<Transaction> getTransactionHistory();

    /**
     * QUERY SIDE: View the current balance
     */
    long getBalance();

    /**
     * COMMAND SIDE: processing balance change - record
     */
    Transaction recordMovement(@NonNull TransactionDto transactionDto);

}