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

    @Id
    private String accountId = "GLOBAL_ACCOUNT";

    @Column(nullable = false)
    private long balance = 0L;

}
