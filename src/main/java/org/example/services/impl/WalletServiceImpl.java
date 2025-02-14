package org.example.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.data.model.Transaction;
import org.example.data.model.User;
import org.example.data.model.Wallet;
import org.example.data.model.enums.Status;
import org.example.data.model.enums.TransactionType;
import org.example.data.repositories.TransactionRepository;
import org.example.data.repositories.UserRepository;
import org.example.data.repositories.WalletRepository;
import org.example.dto.request.DepositRequest;
import org.example.dto.request.TransferRequest;
import org.example.dto.request.WithdrawalRequest;
import org.example.dto.response.TransactionHistoryResponse;
import org.example.excepions.UserNotFoundException;
import org.example.excepions.WalletException;
import org.example.excepions.WalletNotFoundException;
import org.example.general.ApiResponse;
import org.example.general.ErrorMessages;
import org.example.general.Message;
import org.example.services.interfaces.WalletService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.example.general.ErrorMessages.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletServiceImpl implements WalletService {
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public ApiResponse<?> deposit(Long userId,DepositRequest depositRequest) {
        if (depositRequest.getAmount().compareTo(BigDecimal.valueOf(100)) <= 0) throw new
                IllegalArgumentException(ErrorMessages.INVALID_DEPOSIT_AMOUNT);


        Wallet wallet = walletRepository.findByUserId(userId).orElseThrow(()->
                new WalletNotFoundException(ErrorMessages.WALLET_NOT_FOUND));
        wallet.setBalance(wallet.getBalance().add(depositRequest.getAmount()));
        walletRepository.save(wallet);

        Transaction transaction = Transaction.builder()
                .amount(depositRequest.getAmount())
                .transactiontype(TransactionType.DEPOSIT)
                .timestamp(LocalDateTime.now())
                .wallet(wallet)
                .build();
        transactionRepository.save(transaction);
        return ApiResponse.builder()
                .message(Message.DEPOSIT_SUCCESSFUL)
                .status(Status.SUCCESS)
                .data(transaction)
                .build();
    }

    @Override
    public ApiResponse<?> withdraw(Long userId, WithdrawalRequest withdrawalRequest) {
        if (withdrawalRequest.getAmount().compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException(ErrorMessages.INVALID_WITHDRAWAL_AMOUNT);

        Wallet wallet = walletRepository.findByUserId(userId).orElseThrow(()->
                new WalletNotFoundException(ErrorMessages.WALLET_NOT_FOUND));
        if (wallet.getBalance().compareTo(withdrawalRequest.getAmount()) < 0) throw new WalletException(INSUFFICIENT_BALANCE);

        wallet.setBalance(wallet.getBalance().subtract(withdrawalRequest.getAmount()));
        walletRepository.save(wallet);

        Transaction transaction = Transaction.builder()
                .amount(withdrawalRequest.getAmount())
                .transactiontype(TransactionType.WITHDRAWAL)
                .timestamp(LocalDateTime.now())
                .wallet(wallet)
                .build();
        transactionRepository.save(transaction);
        return ApiResponse.builder()
                .message(Message.WITHDRAWAL_SUCCESSFUL)
                .status(Status.SUCCESS)
                .data(transaction)
                .build();
    }

    @Override
    public BigDecimal getBalance(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new WalletNotFoundException(WALLET_NOT_FOUND + userId))
                .getBalance();
    }

    @Override
    public List<TransactionHistoryResponse> getTransactionHistory(Long userId) {
        Wallet wallet = walletRepository.findByUserId(userId).orElseThrow(()->
                new WalletNotFoundException(ErrorMessages.WALLET_NOT_FOUND));
        List<Transaction> transactions = transactionRepository.findByWalletId(wallet.getId());
        return transactions.stream()
                .map(transaction -> new TransactionHistoryResponse(
                        transaction.getAmount(),
                        transaction.getTransactiontype(),
                        transaction.getTimestamp()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public ApiResponse<?> transfer(Long senderId, TransferRequest transferRequest) {
        if (transferRequest.getAmount().compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException(INVALID_TRANSFER_AMOUNT);

        User recipient = userRepository.findUserByAccountNumber
                (transferRequest.getRecipientAccountNumber()).orElseThrow(()->
                new UserNotFoundException(ErrorMessages.USER_NOT_FOUND));

        Wallet senderWallet = walletRepository.findByUserId(senderId).orElseThrow(()->
                new WalletNotFoundException(ErrorMessages.WALLET_NOT_FOUND + senderId));
        Wallet receiverWallet = walletRepository.findByUserId(recipient.getId()).orElseThrow(()->
                new WalletNotFoundException(ErrorMessages.WALLET_NOT_FOUND + recipient.getId()));


        if (senderWallet.getBalance().compareTo(transferRequest.getAmount()) < 0) throw new WalletException(INSUFFICIENT_BALANCE);

        senderWallet.setBalance(senderWallet.getBalance().subtract(transferRequest.getAmount()));
        receiverWallet.setBalance(receiverWallet.getBalance().add(transferRequest.getAmount()));

        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);

        Transaction senderTransaction = Transaction.builder()
                .amount(transferRequest.getAmount())
                .transactiontype(TransactionType.TRANSFER)
                .timestamp(LocalDateTime.now())
                .wallet(senderWallet)
                .build();
        Transaction recipientTransaction = Transaction.builder()
                .amount(transferRequest.getAmount())
                .transactiontype(TransactionType.TRANSFER)
                .timestamp(LocalDateTime.now())
                .wallet(receiverWallet)
                .build();
        transactionRepository.save(senderTransaction);
        transactionRepository.save(recipientTransaction);
        return ApiResponse.builder()
                .message(Message.WITHDRAWAL_SUCCESSFUL)
                .status(Status.SUCCESS)
                .data(senderTransaction)
                .build();
    }
}
