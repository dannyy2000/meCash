package org.example.services.interfaces;

import org.example.data.model.Transaction;
import org.example.data.model.Wallet;
import org.example.dto.request.DepositRequest;
import org.example.dto.request.SignUpRequest;
import org.example.dto.request.TransferRequest;
import org.example.dto.request.WithdrawalRequest;
import org.example.dto.response.TransactionHistoryResponse;
import org.example.general.ApiResponse;

import java.math.BigDecimal;
import java.util.List;

public interface WalletService {

    ApiResponse<?> deposit (Long userId,DepositRequest depositRequest);
    ApiResponse<?> withdraw (Long userId, WithdrawalRequest withdrawalRequest);
    BigDecimal getBalance(Long userId);
    List<TransactionHistoryResponse> getTransactionHistory(Long userId);
    ApiResponse<?> transfer(Long senderId, TransferRequest transferRequest);

}
