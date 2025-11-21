package com.alienCoders.moneymanger.service;

import com.alienCoders.moneymanger.dto.ExpenseDTO;
import com.alienCoders.moneymanger.dto.IncomeDTO;
import com.alienCoders.moneymanger.dto.ProfileDTO;
import com.alienCoders.moneymanger.dto.RecentTransactionDTO;
import com.alienCoders.moneymanger.entity.ProfileEntity;
import com.alienCoders.moneymanger.repository.ExpenseRepository;
import com.alienCoders.moneymanger.repository.IncomeRepository;
import com.alienCoders.moneymanger.util.ExcelGenerator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
@Service
public class ExcelService {


    private final ExpenseService expenseService;
    private final IncomeService incomeService;
    private final ExcelGenerator excelGenerator;
    private final EmailService emailService;
    private final ProfileService profileService;

    public ByteArrayInputStream generateIncomeDataExel(){
        List<IncomeDTO> incomeDTOList=incomeService.getCurrentMonthIncomeForCurrentUser();
        System.out.println("income details "+incomeDTOList);
        List<RecentTransactionDTO> recentTransactionDTOList=
              incomeDTOList.stream().map(incomeDto->RecentTransactionDTO.builder()
                .id(incomeDto.getId())
                .name(incomeDto.getName())
                .amount(incomeDto.getAmount())
                .categoryName(incomeDto.getCategoryName())
                .date(incomeDto.getDate()).build()).collect(Collectors.toList());
       return excelGenerator.transactionToExcel(recentTransactionDTOList,"List Of Income");
    }

    public ByteArrayInputStream generateExpenseDataExel(){
        List<ExpenseDTO> expenseDTOList=expenseService.getCurrentMonthExpensesForCurrentUser();
        List<RecentTransactionDTO> recentTransactionDTOList=
                expenseDTOList.stream().map(expenseDTO->RecentTransactionDTO.builder()
                        .id(expenseDTO.getId())
                        .name(expenseDTO.getName())
                        .amount(expenseDTO.getAmount())
                        .categoryName(expenseDTO.getCategoryName())
                        .date(expenseDTO.getDate()).build()).collect(Collectors.toList());
        return excelGenerator.transactionToExcel(recentTransactionDTOList,"List Of Expense");
    }

    public void sendEmailIncomeData(){

        ProfileEntity profileEntity = profileService.getCurrentProfile();
        String subject="Current month income report";
        String body="Your requested Excel report is attached.";
        ByteArrayInputStream excel=generateIncomeDataExel();
        byte[] excelBytes=excel.readAllBytes();
        String fileName= "income_details.xlsx";
        emailService.sendEmailWithAttachment(profileEntity.getEmail(),subject,body,excelBytes,fileName);
    }

    public void sendEmailExpenseData(){

        ProfileEntity profileEntity = profileService.getCurrentProfile();
        String subject="Current month income report";
        String body="Your requested Excel report is attached.";
        ByteArrayInputStream excel=generateExpenseDataExel();
        byte[] excelBytes=excel.readAllBytes();
        String fileName= "expense_details.xlsx";
        emailService.sendEmailWithAttachment(profileEntity.getEmail(),subject,body,excelBytes,fileName);
    }


}
