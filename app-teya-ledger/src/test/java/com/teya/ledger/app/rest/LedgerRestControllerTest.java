package com.teya.ledger.app.rest;

import com.teya.ledger.app.db.model.Transaction;
import com.teya.ledger.app.service.LedgerService;
import com.teya.ledger.lib.api.type.TransactionType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.teya.ledger.lib.api.TeyaLedgerApi.API_PREFIX;
import static com.teya.ledger.lib.api.TeyaLedgerApi.BALANCE_PATH;
import static com.teya.ledger.lib.api.TeyaLedgerApi.HISTORY_PATH;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LedgerRestController.class) // Запускает контекст только для этого контроллер
public class LedgerRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LedgerService ledgerService; // Изолируем слой бизнес-логики

    @Test
    void testGetBalance_ShouldReturnBalanceMap() throws Exception {
        // Arrange
        Mockito.when(ledgerService.getBalance()).thenReturn(5000L);

        // Act & Assert
        mockMvc.perform(get(API_PREFIX + BALANCE_PATH)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(5000L));
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

}
