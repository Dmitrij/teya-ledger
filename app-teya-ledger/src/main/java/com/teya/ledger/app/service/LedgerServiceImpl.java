package com.teya.ledger.app.service;

import com.teya.ledger.app.db.model.Balance;
import com.teya.ledger.app.db.model.Transaction;
import com.teya.ledger.app.db.repository.BalanceRepository;
import com.teya.ledger.app.db.repository.TransactionRepository;
import com.teya.ledger.lib.api.dto.TransactionDto;
import com.teya.ledger.lib.api.type.TransactionType;
import com.teya.ledger.lib.model.utils.MathUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class LedgerServiceImpl implements LedgerService {

    private static final String GLOBAL_ACCOUNT_ID = Balance.GLOBAL_ACCOUNT_ID;

    // A: let's stick to +- 1000.00 min/max per transaction
    private static final long MAX_AMOUNT = 1000 * 100;
    private static final long MIN_AMOUNT = 1;

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
        log.info("Starting recordMovement for type: {}", t.getType());

        // 1. Бизнес-валидация: max min
        if (!MathUtils.isBetween(t.getAmount(), MIN_AMOUNT, MAX_AMOUNT)) {
            log.warn("Business validation FAILED: amount {} out of bounds [{}...{}]",
                    t.getAmount(), MIN_AMOUNT, MAX_AMOUNT);
            throw new IllegalStateException("Max/Min validation failed");
        }

        // 2. Получение баланса с пессимистичной блокировкой строки
        Balance balance = balanceRepository.findWithLockByAccountId(GLOBAL_ACCOUNT_ID)
                .orElseGet(() -> {
                    Balance newBalance = new Balance();
                    return balanceRepository.save(newBalance);
                });


        // 3. Бизнес-валидация: проверка овердрафта
        if (TransactionType.WITHDRAWAL == t.getType() && balance.getBalance() < t.getAmount()) {
            log.warn("Business validation FAILED: insufficient funds. Balance: {}, Requested: {}",
                    balance.getBalance(), t.getAmount());
            throw new IllegalStateException("Insufficient funds for withdrawal");
        }

        // 4. Сохранение события транзакции
        Transaction event = new Transaction(t.getType(), t.getAmount());
        Transaction savedEvent = transactionRepository.save(event);

        // 5. Обновление проекции баланса
        if (TransactionType.DEPOSIT == t.getType()) {
            balance.credit(t.getAmount());
        } else if (TransactionType.WITHDRAWAL == t.getType()) {
            balance.debit(t.getAmount());
        } else {
            throw new IllegalStateException("Invalid transaction type: " + t.getType());
        }

        // Имитация задержки (раскомментировать при необходимости тестирования таймаутов блокировки)
        try {
            int rand = MathUtils.randomInt(1, 3);
            log.info("Sleeping for {} seconds inside transaction", rand);
            Thread.sleep(rand * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 6. Сохранение обновленного баланса (коммит транзакции запишет данные и снимет блокировку)
        balanceRepository.save(balance);

        log.info("recordMovement SUCCESS for transaction ID: {}", savedEvent.getId());
        return savedEvent;
    }
}
