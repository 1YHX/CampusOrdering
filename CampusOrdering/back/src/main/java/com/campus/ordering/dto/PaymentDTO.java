package com.campus.ordering.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * 支付DTO
 */
@Data
public class PaymentDTO {
    
    /**
     * 订单总金额
     */
    @NotNull(message = "订单总金额不能为空")
    private BigDecimal totalAmount;
    
    /**
     * 可用补贴金额
     */
    private BigDecimal availableSubsidy;
    
    /**
     * 实际使用的补贴金额
     */
    private BigDecimal usedSubsidy;
    
    /**
     * 实际支付金额
     */
    @NotNull(message = "实际支付金额不能为空")
    private BigDecimal actualAmount;
    
    /**
     * 支付方式
     */
    private String paymentMethod;
    
    /**
     * 现金支付比例要求
     */
    private BigDecimal requiredCashRatio;
    
    /**
     * 最低现金支付金额
     */
    private BigDecimal minCashAmount;
    
    /**
     * 补贴账户信息
     */
    private List<SubsidyAccountInfo> subsidyAccounts;
    
    /**
     * 补贴账户信息内部类
     */
    @Data
    public static class SubsidyAccountInfo {
        private Long accountId;
        private String subsidyName;
        private BigDecimal balance;
        private BigDecimal usedAmount;
        private BigDecimal cashRatio;
    }
} 