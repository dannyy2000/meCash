package org.example.services.interfaces;

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
import org.example.general.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Slf4j
public class WalletServiceTest {

    @MockBean
    private WalletRepository walletRepository;

    @MockBean
    private TransactionRepository transactionRepository;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private WalletService walletService;

    private DepositRequest depositRequest;

    private User user;

    private Wallet wallet;

    private Transaction transaction;



    @BeforeEach
    void setUp() {
        depositRequest = new DepositRequest();
        depositRequest.setAmount(BigDecimal.valueOf(200));

        user = new User();
        user.setId(1L);

        wallet = new Wallet();
        wallet.setId(1L);
        wallet.setUser(user);

        transaction = new Transaction();
        transaction.setId(1L);
        transaction.setWallet(wallet);


    }

    @Test
    void testDeposit_Success() {
        wallet.setBalance(BigDecimal.valueOf(500));

        when(walletRepository.findByUserId(user.getId()))
                .thenReturn(Optional.of(wallet));

        when(walletRepository.save(wallet)).thenReturn(wallet);
        when(transactionRepository.save(transaction)).thenReturn(transaction);


        ApiResponse<?> response = walletService.deposit(user.getId(), depositRequest);

        assertNotNull(response);
        assertEquals(Status.SUCCESS, response.getStatus());
    }

    @Test
    void testDeposit_WalletNotFound_ShouldThrowException() {
        User user = new User();
        user.setId(1L);
        user.setAccountNumber("1234567890");


        when(walletRepository.findByUserId(user.getId()))
                .thenReturn(Optional.empty());

        assertThrows(WalletNotFoundException.class, () -> walletService.deposit(user.getId(),depositRequest));

        verify(walletRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void testDeposit_InvalidAmount_ShouldThrowException() {
        depositRequest.setAmount(BigDecimal.valueOf(50));

        User user = new User();
        user.setId(1L);
        user.setAccountNumber("1234567890");

        assertThrows(IllegalArgumentException.class, () -> walletService.deposit(user.getId(),depositRequest));

        verify(walletRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void testWithdraw_Success() {
        wallet.setBalance(BigDecimal.valueOf(500));
        WithdrawalRequest withdrawalRequest = new WithdrawalRequest();
        withdrawalRequest.setAmount(BigDecimal.valueOf(200));

        when(walletRepository.findByUserId(user.getId()))
                .thenReturn(Optional.of(wallet));

        when(walletRepository.save(wallet)).thenReturn(wallet);
        when(transactionRepository.save(transaction)).thenReturn(transaction);
        ApiResponse<?> response = walletService.withdraw(user.getId(), withdrawalRequest);

        assertNotNull(response);
        assertEquals(Status.SUCCESS, response.getStatus());
        assertEquals(BigDecimal.valueOf(300),wallet.getBalance());
    }

    @Test
    void testWithdraw_InvalidAmount_ShouldThrowException() {
        wallet.setBalance(BigDecimal.valueOf(500));
        WithdrawalRequest withdrawalRequest = new WithdrawalRequest();
        withdrawalRequest.setAmount(BigDecimal.valueOf(-1));

        assertThrows(IllegalArgumentException.class, () -> walletService.withdraw(user.getId(),withdrawalRequest));

        verify(walletRepository, never()).save(any());
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void testGetBalance_Success() {
        wallet.setBalance(BigDecimal.valueOf(500));

        when(walletRepository.findByUserId(user.getId()))
                .thenReturn(Optional.of(wallet));

        BigDecimal balance = walletService.getBalance(user.getId());

        assertNotNull(balance);
        assertEquals(BigDecimal.valueOf(500), balance);
    }

    @Test
    void testGetBalance_WalletNotFound_ShouldThrowException() {
        when(walletRepository.findByUserId(user.getId()))
                .thenReturn(Optional.empty());

        assertThrows(WalletNotFoundException.class, () -> walletService.getBalance(user.getId()));
    }

    @Test
    void testGetBalance_ZeroBalance() {
        wallet.setBalance(BigDecimal.ZERO);

        when(walletRepository.findByUserId(user.getId()))
                .thenReturn(Optional.of(wallet));

        BigDecimal balance = walletService.getBalance(user.getId());

        assertNotNull(balance);
        assertEquals(BigDecimal.ZERO, balance);
    }

    @Test
    void testGetTransactionHistory_Success() {
        wallet.setId(1L);

        Transaction transaction1 = new Transaction();
        transaction1.setAmount(BigDecimal.valueOf(200));
        transaction1.setTransactiontype(TransactionType.DEPOSIT);
        transaction1.setTimestamp(LocalDateTime.now());
        transaction1.setWallet(wallet);

        Transaction transaction2 = new Transaction();
        transaction2.setAmount(BigDecimal.valueOf(100));
        transaction2.setTransactiontype(TransactionType.WITHDRAWAL);
        transaction2.setTimestamp(LocalDateTime.now());
        transaction2.setWallet(wallet);

        when(walletRepository.findByUserId(user.getId()))
                .thenReturn(Optional.of(wallet));

        when(transactionRepository.findByWalletId(wallet.getId()))
                .thenReturn(List.of(transaction1, transaction2));

        List<TransactionHistoryResponse> transactionHistory = walletService.getTransactionHistory(user.getId());

        assertNotNull(transactionHistory);
        assertEquals(2, transactionHistory.size());
        assertEquals(BigDecimal.valueOf(200), transactionHistory.get(0).getAmount());
        assertEquals(TransactionType.DEPOSIT, transactionHistory.get(0).getTransactionType());
        assertEquals(BigDecimal.valueOf(100), transactionHistory.get(1).getAmount());
        assertEquals(TransactionType.WITHDRAWAL, transactionHistory.get(1).getTransactionType());
    }

    @Test
    void testGetTransactionHistory_WalletNotFound_ShouldThrowException() {
        when(walletRepository.findByUserId(user.getId()))
                .thenReturn(Optional.empty());

        assertThrows(WalletNotFoundException.class, () -> walletService.getTransactionHistory(user.getId()));
    }

    @Test
    void testGetTransactionHistory_EmptyTransactionHistory() {
        wallet.setId(1L);

        when(walletRepository.findByUserId(user.getId()))
                .thenReturn(Optional.of(wallet));

        when(transactionRepository.findByWalletId(wallet.getId()))
                .thenReturn(List.of());

        List<TransactionHistoryResponse> transactionHistory = walletService.getTransactionHistory(user.getId());

        assertNotNull(transactionHistory);
        assertTrue(transactionHistory.isEmpty());
    }

    @Test
    void testTransfer_Success() {
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setAmount(BigDecimal.valueOf(100));
        transferRequest.setRecipientAccountNumber("recipient123");

        User sender = new User();
        sender.setId(1L);

        User recipient = new User();
        recipient.setId(2L);
        recipient.setAccountNumber("recipient123");

        Wallet senderWallet = new Wallet();
        senderWallet.setId(1L);
        senderWallet.setBalance(BigDecimal.valueOf(500));
        senderWallet.setUser(sender);

        Wallet receiverWallet = new Wallet();
        receiverWallet.setId(2L);
        receiverWallet.setBalance(BigDecimal.valueOf(200));
        receiverWallet.setUser(recipient);

        when(userRepository.findUserByAccountNumber("recipient123"))
                .thenReturn(Optional.of(recipient));

        when(walletRepository.findByUserId(sender.getId()))
                .thenReturn(Optional.of(senderWallet));

        when(walletRepository.findByUserId(recipient.getId()))
                .thenReturn(Optional.of(receiverWallet));

        when(walletRepository.save(senderWallet)).thenReturn(senderWallet);
        when(walletRepository.save(receiverWallet)).thenReturn(receiverWallet);

        when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());

        ApiResponse<?> response = walletService.transfer(sender.getId(), transferRequest);

        assertNotNull(response);
        assertEquals(Status.SUCCESS, response.getStatus());
        assertEquals(Message.WITHDRAWAL_SUCCESSFUL, response.getMessage());
        assertEquals(senderWallet.getBalance(), BigDecimal.valueOf(400));
        assertEquals(receiverWallet.getBalance(), BigDecimal.valueOf(300));
    }


    @Test
    void testTransfer_InvalidAmount_ShouldThrowException() {
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setAmount(BigDecimal.valueOf(0));
        transferRequest.setRecipientAccountNumber("recipient123");

        User sender = new User();
        sender.setId(1L);

        assertThrows(IllegalArgumentException.class, () -> walletService.transfer(sender.getId(), transferRequest));
    }

    @Test
    void testTransfer_UserNotFound_ShouldThrowException() {
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setAmount(BigDecimal.valueOf(100));
        transferRequest.setRecipientAccountNumber("recipient123");

        User sender = new User();
        sender.setId(1L);

        when(userRepository.findUserByAccountNumber("recipient123"))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> walletService.transfer(sender.getId(), transferRequest));
    }

    @Test
    void testTransfer_SenderWalletNotFound_ShouldThrowException() {
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setAmount(BigDecimal.valueOf(100));
        transferRequest.setRecipientAccountNumber("recipient123");

        User sender = new User();
        sender.setId(1L);
        User recipient = new User();
        recipient.setId(2L);
        recipient.setAccountNumber("recipient123");

        when(userRepository.findUserByAccountNumber("recipient123"))
                .thenReturn(Optional.of(recipient));

        when(walletRepository.findByUserId(sender.getId()))
                .thenReturn(Optional.empty());

        assertThrows(WalletNotFoundException.class, () -> walletService.transfer(sender.getId(), transferRequest));
    }

    @Test
    void testTransfer_ReceiverWalletNotFound_ShouldThrowException() {
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setAmount(BigDecimal.valueOf(100));
        transferRequest.setRecipientAccountNumber("recipient123");

        User sender = new User();
        sender.setId(1L);
        User recipient = new User();
        recipient.setId(2L);
        recipient.setAccountNumber("recipient123");

        Wallet senderWallet = new Wallet();
        senderWallet.setId(1L);
        senderWallet.setBalance(BigDecimal.valueOf(500));
        senderWallet.setUser(sender);

        when(userRepository.findUserByAccountNumber("recipient123"))
                .thenReturn(Optional.of(recipient));

        when(walletRepository.findByUserId(sender.getId()))
                .thenReturn(Optional.of(senderWallet));

        when(walletRepository.findByUserId(recipient.getId()))
                .thenReturn(Optional.empty());

        assertThrows(WalletNotFoundException.class, () -> walletService.transfer(sender.getId(), transferRequest));
    }

    @Test
    void testTransfer_InsufficientBalance_ShouldThrowException() {
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setAmount(BigDecimal.valueOf(600));
        transferRequest.setRecipientAccountNumber("recipient123");

        User sender = new User();
        sender.setId(1L);

        User recipient = new User();
        recipient.setId(2L);
        recipient.setAccountNumber("recipient123");

        Wallet senderWallet = new Wallet();
        senderWallet.setId(1L);
        senderWallet.setBalance(BigDecimal.valueOf(500));
        senderWallet.setUser(sender);

        Wallet receiverWallet = new Wallet();
        receiverWallet.setId(2L);
        receiverWallet.setBalance(BigDecimal.valueOf(200));
        receiverWallet.setUser(recipient);

        when(userRepository.findUserByAccountNumber("recipient123"))
                .thenReturn(Optional.of(recipient));

        when(walletRepository.findByUserId(sender.getId()))
                .thenReturn(Optional.of(senderWallet));

        when(walletRepository.findByUserId(recipient.getId()))
                .thenReturn(Optional.of(receiverWallet));

        assertThrows(WalletException.class, () -> walletService.transfer(sender.getId(), transferRequest));
    }








}
