package com.alienCoders.moneymanger.controller;

import com.alienCoders.moneymanger.dto.ExpenseDTO;
import com.alienCoders.moneymanger.dto.IncomeDTO;
import com.alienCoders.moneymanger.service.IncomeService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/incomes")
public class IncomeController {
final private IncomeService incomeService;


    @PostMapping
    public ResponseEntity<IncomeDTO> addIncome(IncomeDTO incomeDTO){
        IncomeDTO saved=incomeService.addIncome(incomeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<IncomeDTO>> getExpenses(){
        List<IncomeDTO> incomeDTO=incomeService.getCurrentMonthIncomeForCurrentUser();
        return ResponseEntity.ok(incomeDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String,Object>> deleteIncome(@PathVariable Long id){
        incomeService.deleteIncome(id);
        return ResponseEntity.ok(Map.of("message","Income deleted successfully"));
    }

}
