package com.teya.ledger.app.service;

import com.teya.ledger.app.db.model.Balance;
import com.teya.ledger.app.db.model.Transaction;
import com.teya.ledger.app.db.repository.BalanceRepository;
import com.teya.ledger.app.db.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LedgerServiceImpl implements LedgerService {

    private static final String GLOBAL_ACCOUNT_ID = Balance.GLOBAL_ACCOUNT_ID;

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
    
}
