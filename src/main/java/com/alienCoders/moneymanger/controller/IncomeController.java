package com.alienCoders.moneymanger.controller;

import com.alienCoders.moneymanger.dto.ExpenseDTO;
import com.alienCoders.moneymanger.dto.IncomeDTO;
import com.alienCoders.moneymanger.service.ExcelService;
import com.alienCoders.moneymanger.service.IncomeService;
import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/incomes")
public class IncomeController {
final private IncomeService incomeService;
final private ExcelService excelService;

    @PostMapping
    public ResponseEntity<IncomeDTO> addIncome(@RequestBody IncomeDTO incomeDTO){
        IncomeDTO saved=incomeService.addIncome(incomeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<IncomeDTO>> getIncome(){
        List<IncomeDTO> incomeDTO=incomeService.getCurrentMonthIncomeForCurrentUser();
        return ResponseEntity.ok(incomeDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String,Object>> deleteIncome(@PathVariable Long id){
        incomeService.deleteIncome(id);
        return ResponseEntity.ok(Map.of("message","Income deleted successfully"));
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadExcel() {

        ByteArrayInputStream excelFile = excelService.generateIncomeDataExel();

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
    ResponseEntity<?> sendEmailIncomeData(){
        try {
            excelService.sendEmailIncomeData();
           return ResponseEntity.ok().body("Email send successfully");
        }
        catch (Exception e){
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not found");
        }
    }

}
