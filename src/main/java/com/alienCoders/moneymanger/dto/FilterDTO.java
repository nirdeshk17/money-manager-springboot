package com.alienCoders.moneymanger.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class FilterDTO {
    private String keyword;
    private String sortField;
    private String sortOrder;
    private LocalDate startDate;
    private LocalDate endDate;
    private String type;
}
