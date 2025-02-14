package org.example.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.data.model.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class TransactionHistoryResponse {

    private BigDecimal amount;
    private TransactionType transactionType;
    private LocalDateTime timestamp;
}
