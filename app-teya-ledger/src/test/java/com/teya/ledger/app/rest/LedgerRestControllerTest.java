package com.teya.ledger.app.rest;

import com.google.gson.Gson;
import com.teya.ledger.app.config.GsonConfig;
import com.teya.ledger.app.config.GsonWebMvcConfig;
import com.teya.ledger.app.db.model.Transaction;
import com.teya.ledger.app.exception.AccountNotFoundException;
import com.teya.ledger.app.rest.advisor.LedgerControllerAdvisor;
import com.teya.ledger.app.service.LedgerService;
import com.teya.ledger.lib.api.dto.BalanceDto;
import com.teya.ledger.lib.api.dto.TransactionDto;
import com.teya.ledger.lib.api.type.TransactionType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.teya.ledger.lib.api.TeyaLedgerApi.API_PREFIX;
import static com.teya.ledger.lib.api.TeyaLedgerApi.BALANCE_PATH;
import static com.teya.ledger.lib.api.TeyaLedgerApi.HISTORY_PATH;
import static com.teya.ledger.lib.api.TeyaLedgerApi.MOVEMENT_PATH;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = {LedgerRestController.class, LedgerControllerAdvisor.class},
        excludeAutoConfiguration = {
                HibernateJpaAutoConfiguration.class
        }
)
@Import({GsonConfig.class, GsonWebMvcConfig.class})
public class LedgerRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Gson gson;

    @MockitoBean
    private LedgerService ledgerService; // Изолируем слой бизнес-логики

    @Test
    void testGetBalance_ShouldReturnBalance_WithPathVariable() throws Exception {
        // Arrange
        String accountId = "12345";
        BalanceDto expectedDto = new BalanceDto(accountId, 5000L);
        Mockito.when(ledgerService.getBalance(accountId)).thenReturn(expectedDto);

        // Act & Assert
        mockMvc.perform(get(API_PREFIX + BALANCE_PATH + "/" + accountId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(expectedDto.getBalance()));
    }

    @Test
    void testGetBalance_ShouldReturn404AndApiErrorDto_WhenAccountNotFound() throws Exception {
        // Arrange
        String nonExistentAccountId = "missing-account-123";
        String expectedExceptionMessage = String.format("Account with ID '%s' not found", nonExistentAccountId);

        // Настраиваем Mock-сервис на выброс исключения
        Mockito.when(ledgerService.getBalance(nonExistentAccountId))
                .thenThrow(new AccountNotFoundException(expectedExceptionMessage));

        // Act & Assert
        mockMvc.perform(get(API_PREFIX + BALANCE_PATH + "/" + nonExistentAccountId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()) // Ожидаем HTTP 404

                // Проверяем структуру вашего ApiErrorDto
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(expectedExceptionMessage))
                .andExpect(jsonPath("$.timestamp").exists())
                //.andExpect(jsonPath("$.errors").value(org.hamcrest.Matchers.nullValue())); // Поля errors быть не должно (null)
                .andExpect(jsonPath("$.errors").doesNotExist()); // Поля errors быть не должно (null)

        // Проверяем, что метод сервиса действительно вызывался с нужным ID
        Mockito.verify(ledgerService, Mockito.times(1)).getBalance(nonExistentAccountId);
    }

    @Test
    void testGetHistory_ShouldReturnTransactionList() throws Exception {
        // Arrange
        Transaction tx1 = new Transaction(TransactionType.DEPOSIT, 1000L);
        Transaction tx2 = new Transaction(TransactionType.WITHDRAWAL, 300L);
        Mockito.when(ledgerService.getTransactionHistory()).thenReturn(List.of(tx1, tx2));

        // Act & Assert
        mockMvc.perform(get(API_PREFIX + HISTORY_PATH)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].type").value("DEPOSIT"))
                .andExpect(jsonPath("$[1].type").value("WITHDRAWAL"));
    }

    @Test
    void testCreateMovement_ShouldReturnBadRequestWithDetailedErrors_WhenAmountIsNegative() throws Exception {
        // Arrange: Создаем некорректный DTO, нарушающий аннотацию @Positive
        TransactionDto invalidDto = new TransactionDto(TransactionType.DEPOSIT, -500L);

        // Act & Assert
        mockMvc.perform(post(API_PREFIX + MOVEMENT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(invalidDto))) // Сериализуем через Gson
                .andExpect(status().isBadRequest()) // Проверяем HTTP 400

                // Проверяем структуру вашего ApiErrorDto
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.timestamp").exists())

                // Проверяем, что в карте errors вернулась детализация по невалидному полю
                .andExpect(jsonPath("$.errors.amount").exists());

        // Гарантируем, что из-за ошибки валидации запрос даже не пытался вызвать метод сервиса
        Mockito.verifyNoInteractions(ledgerService);
    }

    @Test
    void testCreateMovement_ShouldReturnBadRequestWithDetailedErrors_WhenAmountIsNull() throws Exception {
        // Arrange: Создаем DTO, у которого сумма (amount) равна null, что нарушает аннотацию @NotNull
        TransactionDto invalidDto = new TransactionDto(TransactionType.DEPOSIT, null);

        // Act & Assert
        mockMvc.perform(post(API_PREFIX + MOVEMENT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(invalidDto))) // Сериализуем через Gson
                .andExpect(status().isBadRequest()) // Ожидаем статус 400 Bad Request

                // Проверяем структуру вашего ApiErrorDto
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.timestamp").exists())

                // Проверяем, что в мапе errors зафиксирована ошибка конкретно по полю "amount"
                .andExpect(jsonPath("$.errors.amount").exists());

        // Гарантируем, что из-за провала валидации Spring MVC, запрос не дошел до бизнес-логики сервиса
        Mockito.verifyNoInteractions(ledgerService);
    }
    
}
