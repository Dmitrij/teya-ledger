package com.teya.ledger.app.rest;

import com.teya.ledger.app.db.model.Transaction;
import com.teya.ledger.app.service.LedgerService;
import com.teya.ledger.lib.api.dto.BalanceDto;
import com.teya.ledger.lib.api.dto.TransactionDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static com.teya.ledger.lib.api.TeyaLedgerApi.API_PREFIX;
import static com.teya.ledger.lib.api.TeyaLedgerApi.BALANCE_PATH;
import static com.teya.ledger.lib.api.TeyaLedgerApi.HISTORY_PATH;
import static com.teya.ledger.lib.api.TeyaLedgerApi.MEDIA_TYPE_APPLICATION_JSON;
import static com.teya.ledger.lib.api.TeyaLedgerApi.MOVEMENT_PATH;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = {API_PREFIX}, produces = MEDIA_TYPE_APPLICATION_JSON)
public final class LedgerRestController {

    private final LedgerService ledgerService;

    @GetMapping(HISTORY_PATH)
    public ResponseEntity<List<Transaction>> getHistory() {
        return ResponseEntity.ok(ledgerService.getTransactionHistory());
    }

    @GetMapping(BALANCE_PATH + "/{accountId}")
    public BalanceDto getBalance(@PathVariable String accountId) {
        log.info("getBalance for {}", accountId);
        BalanceDto currentBalance = ledgerService.getBalance(accountId);
        log.info("Current balance: {}", currentBalance);
        return currentBalance;
    }

    @PostMapping(MOVEMENT_PATH)
    public ResponseEntity<?> createMovement(@Valid @RequestBody TransactionDto transactionDto) {
        log.info("Creating transaction {}", transactionDto);
        try {
            Transaction event = ledgerService.recordMovement(transactionDto);
            return new ResponseEntity<>(event, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid transaction type or layout"));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Malformed request metadata"));
        }
    }

}
