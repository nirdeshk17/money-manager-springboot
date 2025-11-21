package com.alienCoders.moneymanger.controller;


import com.alienCoders.moneymanger.dto.ExpenseDTO;
import com.alienCoders.moneymanger.dto.IncomeDTO;
import com.alienCoders.moneymanger.service.EmailService;
import com.alienCoders.moneymanger.service.ExcelService;
import com.alienCoders.moneymanger.service.ExpenseService;
import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final ExcelService excelService;

    @PostMapping
    public ResponseEntity<ExpenseDTO> addExpense(@RequestBody ExpenseDTO expenseDTO){
        ExpenseDTO saved=expenseService.addExpense(expenseDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<ExpenseDTO>> getExpenses(){
        List<ExpenseDTO> incomeDTO=expenseService.getCurrentMonthExpensesForCurrentUser();
        return ResponseEntity.ok(incomeDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String,Object>> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.ok(Map.of("message","Expense deleted successfully"));
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadExcel() {

        ByteArrayInputStream excelFile = excelService.generateExpenseDataExel();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(
                ContentDisposition.builder("attachment")
                        .filename("income_data.xlsx")
                        .build()
        );
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        try {
            byte[] bytes = excelFile.readAllBytes();
            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .body(bytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/sendEmail")
    ResponseEntity<?> sendEmailExpenseData(){
        try {
            excelService.sendEmailExpenseData();
            return ResponseEntity.ok().body("Email send successfully");
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not found");
        }
    }

}
