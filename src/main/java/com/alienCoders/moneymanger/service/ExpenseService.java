package com.alienCoders.moneymanger.service;

import com.alienCoders.moneymanger.dto.ExpenseDTO;
import com.alienCoders.moneymanger.entity.CategoryEntity;
import com.alienCoders.moneymanger.entity.ExpenseEntity;
import com.alienCoders.moneymanger.entity.ProfileEntity;
import com.alienCoders.moneymanger.repository.CategoryRepository;
import com.alienCoders.moneymanger.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final CategoryService categoryService;
    private final ProfileService profileService;
    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;

   // Adds a new expense to the database
    public ExpenseDTO addExpense(ExpenseDTO dto){
        ProfileEntity profile=profileService.getCurrentProfile();
        CategoryEntity category=categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(()->new RuntimeException("Category not found"));
        ExpenseEntity newExpense=toEntity(dto,category,profile);
        newExpense=  expenseRepository.save(newExpense);
        return toDTO(newExpense);

    }

    //Retrieves all expenses for current month/based on the start and end date
    public List<ExpenseDTO> getCurrentMonthExpensesForCurrentUser(){
        ProfileEntity profileEntity=profileService.getCurrentProfile();
        LocalDate startDate=LocalDate.now().withDayOfMonth(1);
        LocalDate endDate=LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        List<ExpenseEntity> expenseEntity=expenseRepository.findByProfileIdAndDateBetween(profileEntity.getId(),startDate,endDate);
        return expenseEntity.stream().map(this::toDTO).toList();
    }

    // Delete expense by id
    public void deleteExpense(Long expenseId){
        ProfileEntity profile=profileService.getCurrentProfile();
        ExpenseEntity expenseEntity=expenseRepository.findById(expenseId)
                .orElseThrow(()->new RuntimeException("Expense not found"));
        if(profile.getId().equals(expenseEntity.getProfile().getId())) {
            expenseRepository.deleteById(expenseId);
        }
        else {
            throw new RuntimeException("Unauthorized to delete this expense");
        }
    }

    // Get latest 5 expenses for current user
    public List<ExpenseDTO> getLatestFiveExpenseList(){
        ProfileEntity profileEntity=profileService.getCurrentProfile();
        List<ExpenseEntity> expenseEntity=expenseRepository.findTop5ByProfileIdOrderByDateDesc(profileEntity.getId());
        return expenseEntity.stream().map(this::toDTO).toList();
    }

    // Get total sum amount of all the expenses for current user
    public BigDecimal getTotalExpense(){
       ProfileEntity profileEntity=profileService.getCurrentProfile();
     BigDecimal total= expenseRepository.findTotalExpenseByProfileId(profileEntity.getId());
    return total!=null?total:BigDecimal.ZERO;
    }

    // filter method for expense
    public List<ExpenseDTO> filterExpense(LocalDate startDate, LocalDate endDate, String keyword, Sort sort){
        ProfileEntity profileEntity=profileService.getCurrentProfile();
        List<ExpenseEntity> expenseEntityList=expenseRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profileEntity.getId(),startDate,endDate,keyword,sort);
        return expenseEntityList.stream().map(this::toDTO).toList();
    }

    public List<ExpenseDTO> getExpensesForUserOnDate(Long profileId,LocalDate date){
        List<ExpenseEntity> expenseEntityList=expenseRepository.findByProfileIdAndDate(profileId,date);
        return expenseEntityList.stream().map(this::toDTO).toList();
    }

    //helper methods
    private ExpenseEntity toEntity(ExpenseDTO expenseDTO, CategoryEntity category, ProfileEntity profile){
        return ExpenseEntity.builder()
                .name(expenseDTO.getName())
                .icon(expenseDTO.getIcon())
                .amount(expenseDTO.getAmount())
                .date(expenseDTO.getDate())
                .profile(profile)
                .category(category)
                .build();
    }

    private ExpenseDTO toDTO(ExpenseEntity entity){
        return ExpenseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .icon(entity.getIcon())
                .date(entity.getDate())
                .categoryId(entity.getCategory()!=null?entity.getCategory().getId():null)
                .categoryName(entity.getCategory()!=null?entity.getCategory().getName():null)
                .amount(entity.getAmount())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
