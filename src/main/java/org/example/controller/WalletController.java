package org.example.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.example.data.model.User;
import org.example.dto.request.DepositRequest;
import org.example.dto.request.TransferRequest;
import org.example.dto.request.WithdrawalRequest;
import org.example.dto.response.TransactionHistoryResponse;
import org.example.general.ApiResponse;
import org.example.security.config.JwtProvider;
import org.example.services.interfaces.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/wallet")
public class WalletController extends Controller {
    private final WalletService walletService;
    @Autowired
    private  JwtProvider jwtProvider;
    public WalletController(HttpServletResponse response, WalletService walletService) {
        super(response);
        this.walletService = walletService;
    }

    @PostMapping("/deposit")
    public ApiResponse<?> deposit(@RequestHeader("Authorization") String token, @Valid @RequestBody DepositRequest depositRequest){
        String jwt = token.substring(7);

        Long userId = jwtProvider.extractUserId(jwt);
        if (userId == null) {
            throw new RuntimeException("User ID not found in token");
        }

        return responseWithUpdatedHttpStatus(walletService.deposit(userId,depositRequest));
    }

    @PostMapping("/withdrawal")
    public ApiResponse<?> withdrawal(@RequestHeader("Authorization") String token, @Valid @RequestBody
    WithdrawalRequest withdrawalRequest){

        String jwt = token.substring(7);

        Long userId = jwtProvider.extractUserId(jwt);
        if (userId == null) {
            throw new RuntimeException("User ID not found in token");
        }

        return responseWithUpdatedHttpStatus(walletService.withdraw(userId, withdrawalRequest));
    }

    @PostMapping("/transfer")
    public ApiResponse<?> transfer(@RequestHeader("Authorization") String token, @Valid @RequestBody
                                   TransferRequest transferRequest ){
        String jwt = token.substring(7);
        Long userId = jwtProvider.extractUserId(jwt);
        if (userId == null) {
            throw new RuntimeException("User ID not found in token");
        }

        return responseWithUpdatedHttpStatus(walletService.transfer(userId,transferRequest));
    }

    @GetMapping("/balance")
    public BigDecimal getBalance(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);
        Long userId = jwtProvider.extractUserId(jwt);
        if (userId == null) {
            throw new RuntimeException("User ID not found in token");
        }


        return walletService.getBalance(userId);
    }

    @GetMapping("/transactionHistory")
    public List<TransactionHistoryResponse> getTransactionHistory(@RequestHeader("Authorization") String token){
        String jwt = token.substring(7);
        Long userId = jwtProvider.extractUserId(jwt);
        if (userId == null) {
            throw new RuntimeException("User ID not found in token");
        }
        return (walletService.getTransactionHistory(userId));
    }

}
