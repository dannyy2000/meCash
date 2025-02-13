package org.example.services.interfaces;

import org.example.data.model.Wallet;
import org.example.dto.request.SignUpRequest;
import org.example.general.ApiResponse;

public interface WalletService {

    ApiResponse<Wallet> deposit (Long userId, DepositRequest depositRequest);
}
