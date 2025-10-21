package com.alienCoders.moneymanger.service;

import com.alienCoders.moneymanger.dto.ExpenseDTO;
import com.alienCoders.moneymanger.dto.IncomeDTO;
import com.alienCoders.moneymanger.dto.RecentTransactionDTO;
import com.alienCoders.moneymanger.entity.ProfileEntity;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import java.util.stream.Collectors;
import java.util.stream.Stream;


@AllArgsConstructor
@Service
public class DashboardService {

    private final IncomeService incomeService;
    private final ExpenseService expenseService;
    private final ProfileService profileService;

    public Map<String,Object> getDashBoardData() {
        ProfileEntity profileEntity = profileService.getCurrentProfile();
        Map<String, Object> returnValue = new LinkedHashMap<>();
        List<ExpenseDTO> expenseEntityList = expenseService.getLatestFiveExpenseList();
        List<IncomeDTO> incomeEntityList = incomeService.getLatestFiveIncomeList();
        List<RecentTransactionDTO> recentTransactionDTOS = Stream.concat(incomeEntityList.stream().map(incomeDTO -> RecentTransactionDTO.builder()
                                .id(incomeDTO.getId())
                                .categoryId(incomeDTO.getCategoryId())
                                .icon(incomeDTO.getIcon())
                                .profileId(profileEntity.getId())
                                .amount(incomeDTO.getAmount())
                                .date(incomeDTO.getDate())
                                .name(incomeDTO.getName())
                                .createdAt(incomeDTO.getCreatedAt())
                                .updatedAt(incomeDTO.getUpdatedAt())
                                .type("income")
                                .build()),
                        expenseEntityList.stream().map(expenseDTO -> RecentTransactionDTO.builder()
                                .id(expenseDTO.getId())
                                .categoryId(expenseDTO.getCategoryId())
                                .icon(expenseDTO.getIcon())
                                .profileId(profileEntity.getId())
                                .amount(expenseDTO.getAmount())
                                .date(expenseDTO.getDate())
                                .name(expenseDTO.getName())
                                .createdAt(expenseDTO.getCreatedAt())
                                .updatedAt(expenseDTO.getUpdatedAt())
                                .type("expense")
                                .build()))
                .sorted((a, b) -> {
                    int cmp = b.getDate().compareTo(a.getDate());
                    if (cmp == 0 && a.getCreatedAt() != null && b.getCreatedAt() != null) {
                        return b.getCreatedAt().compareTo(a.getCreatedAt());
                    }
                    return cmp;
                }).collect(Collectors.toList());
 returnValue.put("totalBalance",incomeService.getTotalIncome().subtract(expenseService.getTotalExpense()));
 returnValue.put("totalExpense",expenseService.getTotalExpense());
 returnValue.put("recent5Expenses",expenseEntityList);
  returnValue.put("recent5Incomes",incomeEntityList);
  returnValue.put("recentTransactions",recentTransactionDTOS);
  return  returnValue;
    }

}
