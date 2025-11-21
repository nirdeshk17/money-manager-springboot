package com.alienCoders.moneymanger.controller;


import com.alienCoders.moneymanger.dto.ExpenseDTO;
import com.alienCoders.moneymanger.dto.FilterDTO;
import com.alienCoders.moneymanger.dto.IncomeDTO;
import com.alienCoders.moneymanger.entity.IncomeEntity;
import com.alienCoders.moneymanger.service.ExpenseService;
import com.alienCoders.moneymanger.service.IncomeService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/filter")
public class FilterController {

    private final ExpenseService expenseService;
    private final IncomeService incomeService;


    @PostMapping
    public ResponseEntity<?> filterTransaction(@RequestBody FilterDTO filterDTO){
        System.out.println("hellooo");
        LocalDate startDate=filterDTO.getStartDate()!=null?filterDTO.getStartDate():LocalDate.MIN;
        LocalDate endDate=filterDTO.getEndDate()!=null?filterDTO.getEndDate():LocalDate.now();
        String keyword=filterDTO.getKeyword()!=null?filterDTO.getKeyword():"";
        String sortField=filterDTO.getSortField()!=null?filterDTO.getSortField():"date";
        Sort.Direction direction="desc".equalsIgnoreCase(filterDTO.getSortOrder())?Sort.Direction.DESC:Sort.Direction.ASC;
        Sort sort=Sort.by(direction,sortField);
        if(filterDTO.getType().equals("income")){
      List<IncomeDTO> incomeDTOList= incomeService.filterIncome(startDate,endDate,keyword,sort);
            return ResponseEntity.ok(incomeDTOList);
        }
        else if(filterDTO.getType().equals("expense")){
            List<ExpenseDTO> expenseDTOList= expenseService.filterExpense(startDate,endDate,keyword,sort);
            return ResponseEntity.ok(expenseDTOList);
        }
        else {
           return ResponseEntity.badRequest().body("Invalid type. Must be 'income' or 'expense'");
        }
    }
}
