package com.alienCoders.moneymanger.service;


import com.alienCoders.moneymanger.dto.ExpenseDTO;
import com.alienCoders.moneymanger.entity.ProfileEntity;
import com.alienCoders.moneymanger.repository.ProfileRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final ExpenseService expenseService;


    @Value("${money.manager.frontend.url}")
    private String frontendUrl;

//    @Scheduled(cron = "0 * * * * *",zone = "IST")
    @Scheduled(cron = "0 0 22 * * *",zone = "IST")
    public void sendDailyIncomeExpenseRemainder(){
        log.info("Job started: sendDailyIncomeExpenseRemainder()");
        List<ProfileEntity> profileEntityList=profileRepository.findAll();
        for(ProfileEntity profile:profileEntityList){
        String body="Hi " + profile.getFullName() + ",<br><br>"
                + "This is a friendly reminder to add your income and expenses for today in Money Manager.<br><br>"
                + "<a href='" + frontendUrl + "' "
                + "style='display:inline-block;padding:10px 20px;background-color:#4CAF50;color:white;"
                + "text-decoration:none;border-radius:5px;'>"
                + "Add Now</a>"
                + "<br><br>Best regards,<br>Money Manager Team";
           emailService.sendEmail(profile.getEmail(),"Daily Remainder: Add your income and expenses",body);
    }
    log.info("Job Completed: sendDailyIncomeExpenseRemainder()");
    }
//    @Scheduled(cron = "0 * * * * *",zone = "IST")
    @Scheduled(cron = "0 0 23 * * *",zone = "IST")
    public void sendDailyExpenseSummary(){
        log.info("Job Started: sendDailyExpenseSummary()");
        List<ProfileEntity> profileEntityList=profileRepository.findAll();
        for(ProfileEntity profile:profileEntityList){
        List<ExpenseDTO> expenseDTOList = expenseService.getExpensesForUserOnDate(profile.getId(), LocalDate.now());
            if(!expenseDTOList.isEmpty()) {
                StringBuilder table = new StringBuilder();
                table.append("<table style='border-collapse:collapse;width:100%;font-family:Arial,sans-serif;'>")
                        .append("<tr style='background-color:#f2f2f2;'>")
                        .append("<th style='border:1px solid #ddd;padding:8px;'>S.No</th>")
                        .append("<th style='border:1px solid #ddd;padding:8px;'>Name</th>")
                        .append("<th style='border:1px solid #ddd;padding:8px;'>Amount</th>")
                        .append("<th style='border:1px solid #ddd;padding:8px;'>Category</th>")
                        .append("</tr>");

                int i = 1;
                for (ExpenseDTO expense : expenseDTOList) {
                    table.append("<tr>")
                            .append("<td style='border:1px solid #ddd;padding:8px;'>").append(i++).append("</td>")
                            .append("<td style='border:1px solid #ddd;padding:8px;'>").append(expense.getName()).append("</td>")
                            .append("<td style='border:1px solid #ddd;padding:8px;'>₹").append(expense.getAmount()).append("</td>")
                            .append("<td style='border:1px solid #ddd;padding:8px;'>").append(expense.getCategoryName() != null ? expense.getCategoryName() : "N/A").append("</td>")
                            .append("</tr>");
                }
                table.append("</table>");
                String body="Hi "+profile.getFullName()+"<br/><br/> Here is summary of your expense for today:<br/><br/>"+table+"<br/><br/>Best regards,<br/>Money Manager Team";
              emailService.sendEmail(profile.getEmail(),"Your Daily Expenses Summary",body);
            }
        }
    }
}
