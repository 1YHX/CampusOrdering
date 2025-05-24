package com.campus.ordering.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单DTO
 */
@Data
public class OrderDTO {
    
    @NotNull(message = "总金额不能为空")
    private BigDecimal amount;
    
    private BigDecimal subsidyAmount;
    
    @NotNull(message = "实付金额不能为空")
    private BigDecimal actualAmount;
    
    private String paymentMethod;
    
    @NotBlank(message = "订单类型不能为空")
    private String orderType;
    
    @NotNull(message = "就餐时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eatTime;
    
    private String remark;
    
    @NotEmpty(message = "订单明细不能为空")
    private List<OrderDetailDTO> details;
} 