package com.teya.ledger.app.service;

import com.teya.ledger.app.db.model.Transaction;

import java.util.List;

public interface LedgerService {

    /**
     * QUERY SIDE: View transaction history
     */
    List<Transaction> getTransactionHistory();

    /**
     * QUERY SIDE: View current balance
     */
    long getBalance();

}
