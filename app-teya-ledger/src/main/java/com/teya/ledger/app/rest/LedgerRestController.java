package com.teya.ledger.app.rest;

import com.teya.ledger.app.db.model.Transaction;
import com.teya.ledger.app.service.LedgerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static com.teya.ledger.lib.api.TeyaLedgerApi.API_PREFIX;
import static com.teya.ledger.lib.api.TeyaLedgerApi.BALANCE_PATH;
import static com.teya.ledger.lib.api.TeyaLedgerApi.HISTORY_PATH;
import static com.teya.ledger.lib.api.TeyaLedgerApi.MEDIA_TYPE_APPLICATION_JSON;

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

    @GetMapping(BALANCE_PATH)
    public ResponseEntity<Map<String, Long>> getBalance() {
        long currentBalance = ledgerService.getBalance();
        return ResponseEntity.ok(Map.of("balance", currentBalance));
    }

}
