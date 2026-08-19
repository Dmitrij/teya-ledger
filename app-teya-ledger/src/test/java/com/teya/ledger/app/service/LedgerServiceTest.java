package com.teya.ledger.app.service;

import com.teya.ledger.app.TeyaLedgerApplication;
import com.teya.ledger.app.db.model.Transaction;
import com.teya.ledger.app.db.repository.BalanceRepository;
import com.teya.ledger.app.db.repository.TransactionRepository;
import com.teya.ledger.app.exception.AccountNotFoundException;
import com.teya.ledger.lib.api.dto.BalanceDto;
import com.teya.ledger.lib.api.dto.TransactionDto;
import com.teya.ledger.lib.api.type.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.teya.ledger.app.db.model.Balance.GLOBAL_ACCOUNT_ID;
import static com.teya.ledger.lib.api.DtoUtils.asBalance;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = TeyaLedgerApplication.class)
@ActiveProfiles("test")
public class LedgerServiceTest {

    @Autowired
    private LedgerService ledgerService;

    // Внедрите ваши репозитории, чтобы очищать БД между тестами
    @Autowired
    private BalanceRepository balanceRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private TransactionDto depositDto;
    private TransactionDto withdrawDto;

    @BeforeEach
    void setUp() {
        // Очищаем базу данных перед каждым тестом для полной изоляции
        transactionRepository.deleteAll();
        balanceRepository.deleteAll();

        depositDto = new TransactionDto(TransactionType.DEPOSIT, 1000L);
        withdrawDto = new TransactionDto(TransactionType.WITHDRAWAL, 300L);
    }

    // Обычные тесты изолируем стандартной транзакцией Spring
    @Test
    @Transactional
    void testGetBalance_ShouldReturnInitialBalance() {
        // Act
        ledgerService.registerAccount(GLOBAL_ACCOUNT_ID);
        BalanceDto dto = ledgerService.getBalance(GLOBAL_ACCOUNT_ID);
        long currentBalance = asBalance(dto);

        // Assert
        assertEquals(0L, currentBalance, "Initial balance should be equals 0");
    }

    /**
     * ТЕСТ: После регистрации аккаунт должен успешно находиться в базе данных
     */
    @Test
    void testRegisterAccountAndGetBalance_ShouldWorkPerfectly() {
        // Arrange
        String newAccountId = "client-777";

        // Act: Регистрируем новый аккаунт
        BalanceDto registeredAccount = ledgerService.registerAccount(newAccountId);

        // Assert: Проверяем возвращаемый DTO после регистрации
        assertNotNull(registeredAccount);
        assertEquals(newAccountId, registeredAccount.getAccountId());
        assertEquals(0L, registeredAccount.getBalance(), "Начальный баланс нового аккаунта должен быть равен 0");

        // Act: Проверяем, что теперь getBalance() находит этот аккаунт без ошибок
        BalanceDto fetchedAccount = ledgerService.getBalance(newAccountId);

        // Assert
        assertNotNull(fetchedAccount);
        assertEquals(newAccountId, fetchedAccount.getAccountId());
        assertEquals(0L, fetchedAccount.getBalance());
    }

    @Test
    void testGetBalance_ShouldThrowAccountNotFoundException_WhenAccountDoesNotExist() {
        // Arrange
        String nonExistentAccountId = "unknown-account-id";

        // Act & Assert
        AccountNotFoundException exception = assertThrows(AccountNotFoundException.class, () -> {
            ledgerService.getBalance(nonExistentAccountId);
        }, "Метод getBalance должен выбросить AccountNotFoundException для несуществующего ID");

        // Проверяем, что текст сообщения внутри исключения сформирован корректно
        assertTrue(exception.getMessage().contains(nonExistentAccountId),
                "Сообщение об ошибке должно содержать ID отсутствующего аккаунта");
    }

    @Test
    @Transactional
    void testRecordMovement_ShouldUpdateBalanceAndSaveTransaction() {
        // Act: Начисление средств
        Transaction firstTx = ledgerService.recordMovement(depositDto);

        // Assert
        assertNotNull(firstTx);
        assertEquals(1000L, asBalance(ledgerService.getBalance(GLOBAL_ACCOUNT_ID)), "Баланс должен увеличиться на 1000");

        // Act: Списание средств
        Transaction secondTx = ledgerService.recordMovement(withdrawDto);

        // Assert
        assertNotNull(secondTx);
        assertEquals(700L, asBalance(ledgerService.getBalance(GLOBAL_ACCOUNT_ID)), "Баланс должен уменьшиться до 700");
    }

    @Test
    @Transactional
    void testGetTransactionHistory_ShouldReturnAllRecordedTransactions() {
        ledgerService.registerAccount(GLOBAL_ACCOUNT_ID);

        long currentBalance = asBalance(ledgerService.getBalance(GLOBAL_ACCOUNT_ID));
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
    @Transactional
    void testRecordMovement_ShouldThrowException_WhenDtoIsNull() {
        // Assert
        assertThrows(NullPointerException.class, () -> {
            ledgerService.recordMovement(null);
        }, "Метод должен выбросить NullPointerException, так как аргумент помечен @NonNull");
    }

    // Здесь @Transactional НЕЛЬЗЯ ставить, так как это многопоточный тест!
    @Test
    void testConcurrentMovement() throws InterruptedException {

        ledgerService.registerAccount(GLOBAL_ACCOUNT_ID);

        long currentBalance = asBalance(ledgerService.getBalance(GLOBAL_ACCOUNT_ID));
        assertEquals(0L, currentBalance, "Initial balance should be equals 0");

        ledgerService.recordMovement(depositDto);

        List<TransactionDto> transactions = List.of(
                this.depositDto,  // +1000
                this.withdrawDto, // -300
                this.withdrawDto, // -300
                this.withdrawDto, // -300
                this.withdrawDto, // -300
                this.withdrawDto, // -300
                this.withdrawDto, // -300
                this.withdrawDto  // -300 (Этой транзакции не хватит денег, она упадет с ошибкой)
        );

        int numberOfThreads = transactions.size();
        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for (TransactionDto transactionDto : transactions) {
            service.submit(() -> {
                try {
                    ledgerService.recordMovement(transactionDto);
                } catch (Exception e) {
                    // Логируем исключения, если потоки упадут из-за race condition или блокировок
                    System.err.println("Thread execution failed: " + e.getMessage());
                } finally {
                    latch.countDown(); // Поток завершил работу
                }
            });
            Thread.sleep(1);
        }

        // Ждем завершения всех потоков максимум 15 секунд
        boolean finishedSuccessfully = latch.await(numberOfThreads * 3 + 2, TimeUnit.SECONDS);
        service.shutdown();

        // Assert
        assertTrue(finishedSuccessfully, "Тест прерван по таймауту. Потоки заблокировали друг друга.");

        currentBalance = asBalance(ledgerService.getBalance(GLOBAL_ACCOUNT_ID));
        assertEquals(200L, currentBalance, "Финальный баланс рассчитан неверно из-за Race Condition");

        // Проверяем количество успешных транзакций в истории:
        // 1 (стартовый депозит) + 1 (параллельный депозит) + 6 (успешных параллельных списаний) = 8
        // 7-е списание откатилось и запись в историю НЕ попала.
        int expectedHistorySize = 8;
        assertEquals(expectedHistorySize, ledgerService.getTransactionHistory().size(), "Часть транзакций была потеряна");

    }

}
