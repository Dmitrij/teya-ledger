package com.teya.ledger.app.db.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "tl_balance")
public final class Balance {

    public static final String GLOBAL_ACCOUNT_ID = "GLOBAL_ACCOUNT";

    @Id
    private String accountId = GLOBAL_ACCOUNT_ID;

    @Column(nullable = false)
    private long balance = 0L;

    public void credit(long amount) {
        this.balance += amount;
    }

    public void debit(long amount) {
        this.balance -= amount;
    }
}
