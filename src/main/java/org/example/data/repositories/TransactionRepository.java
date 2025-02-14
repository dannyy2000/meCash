package org.example.data.repositories;

import org.example.data.model.Transaction;
import org.example.data.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction,Long> {

    List<Transaction> findByWalletId(Long walletId);
}
