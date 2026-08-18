package com.teya.ledger.app.db.repository;

import com.teya.ledger.app.db.model.Balance;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface BalanceRepository extends JpaRepository<Balance, String> {

    // Optimistic Locking via @Version || Pessimistic Locking via SELECT FOR UPDATE
    @Lock(LockModeType.PESSIMISTIC_WRITE) // Генерирует SELECT ... FOR UPDATE
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "5000")})
    Optional<Balance> findWithLockByAccountId(String id);

}
