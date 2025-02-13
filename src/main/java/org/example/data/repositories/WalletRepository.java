package org.example.data.repositories;

import org.example.data.model.Transaction;
import org.example.data.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet,Long> {
}
