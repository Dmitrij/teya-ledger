package com.teya.ledger.app.service;

import com.teya.ledger.app.db.model.Balance;
import com.teya.ledger.app.db.model.Transaction;
import com.teya.ledger.app.db.repository.BalanceRepository;
import com.teya.ledger.app.db.repository.TransactionRepository;
import com.teya.ledger.lib.api.dto.TransactionDto;
import com.teya.ledger.lib.api.type.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LedgerServiceImpl implements LedgerService {

    private static final String GLOBAL_ACCOUNT_ID = Balance.GLOBAL_ACCOUNT_ID;

    // A: let's stick to +- 1000.00 min/max per transaction
    private static final long MAX_AMOUNT = 1000 * 100;

    private final BalanceRepository balanceRepository;
    private final TransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    @Override
    public List<Transaction> getTransactionHistory() {
        return transactionRepository.findAll();
    }

    @Override
    public long getBalance() {
        return balanceRepository.findById(GLOBAL_ACCOUNT_ID)
                .map(Balance::getBalance)
                .orElse(0L);
    }

    // Transactional - to ensure Immutability
    @Transactional
    @Override
    public Transaction recordMovement(@NonNull TransactionDto t) {

        // Current balance
        Balance balance = balanceRepository.findById(GLOBAL_ACCOUNT_ID)
                .orElseGet(() -> {
                    Balance newBalance = new Balance();
                    return balanceRepository.save(newBalance);
                });

        // Business validation: max min
        if (t.getAmount() > MAX_AMOUNT) {
            throw new IllegalStateException("Max/Min validation");
        }

        // Business validation: check overdrafts
        if (TransactionType.WITHDRAWAL == t.getType() && balance.getBalance() < t.getAmount()) {
            throw new IllegalStateException("Insufficient funds for withdrawal");
        }

        // 1. create event
        Transaction event = new Transaction(t.getType(), t.getAmount());
        Transaction savedEvent = transactionRepository.save(event);

        // 2. update projection
        if (TransactionType.DEPOSIT == t.getType()) {
            balance.credit(t.amount);
        } else if (TransactionType.WITHDRAWAL == t.getType()) {
            balance.debit(t.amount);
        } else {
            throw new IllegalStateException("Invalid transaction type");
        }

        balanceRepository.save(balance);

        return savedEvent;
    }
}
