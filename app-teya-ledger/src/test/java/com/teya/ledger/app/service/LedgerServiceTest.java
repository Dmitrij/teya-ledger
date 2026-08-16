package com.teya.ledger.app.service;

import com.teya.ledger.app.TeyaLedgerApplication;
import com.teya.ledger.app.db.model.Transaction;
import com.teya.ledger.lib.api.dto.TransactionDto;
import com.teya.ledger.lib.api.type.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = TeyaLedgerApplication.class)
@ActiveProfiles("test")
@Transactional
public class LedgerServiceTest {

    @Autowired
    private LedgerService ledgerService;

    private TransactionDto depositDto;
    private TransactionDto withdrawDto;

    @BeforeEach
    void setUp() {
        depositDto = new TransactionDto(TransactionType.DEPOSIT, 1000L);
        withdrawDto = new TransactionDto(TransactionType.WITHDRAWAL, 300L);
    }

    @Test
    void testGetBalance_ShouldReturnInitialBalance() {
        // Act
        long currentBalance = ledgerService.getBalance();

        // Assert
        assertEquals(0L, currentBalance, "Initial balance should be equals 0");
    }

    @Test
    void testRecordMovement_ShouldUpdateBalanceAndSaveTransaction() {
        // Act: Начисление средств
        Transaction firstTx = ledgerService.recordMovement(depositDto);

        // Assert
        assertNotNull(firstTx);
        assertEquals(1000L, ledgerService.getBalance(), "Баланс должен увеличиться на 1000");

        // Act: Списание средств
        Transaction secondTx = ledgerService.recordMovement(withdrawDto);

        // Assert
        assertNotNull(secondTx);
        assertEquals(700L, ledgerService.getBalance(), "Баланс должен уменьшиться до 700");
    }

    @Test
    void testGetTransactionHistory_ShouldReturnAllRecordedTransactions() {
        long currentBalance = ledgerService.getBalance();
        assertEquals(0L, currentBalance, "Initial balance should be equals 0");

        // Arrange
        ledgerService.recordMovement(depositDto);
        ledgerService.recordMovement(withdrawDto);

        // Act
        List<Transaction> history = ledgerService.getTransactionHistory();

        // Assert
        assertNotNull(history);
        assertEquals(2, history.size(), "История должна содержать ровно 2 транзакции");

        // Дополнительные проверки структуры данных (зависит от полей вашего класса Transaction)
        assertEquals(TransactionType.DEPOSIT, history.get(0).getType());
        assertEquals(TransactionType.WITHDRAWAL, history.get(1).getType());
    }

    @Test
    void testRecordMovement_ShouldThrowException_WhenDtoIsNull() {
        // Assert
        assertThrows(NullPointerException.class, () -> {
            ledgerService.recordMovement(null);
        }, "Метод должен выбросить NullPointerException, так как аргумент помечен @NonNull");
    }

}
