package com.alienCoders.moneymanger.service;
import com.alienCoders.moneymanger.dto.ExpenseDTO;
import com.alienCoders.moneymanger.dto.IncomeDTO;
import com.alienCoders.moneymanger.entity.CategoryEntity;
import com.alienCoders.moneymanger.entity.ExpenseEntity;
import com.alienCoders.moneymanger.entity.IncomeEntity;
import com.alienCoders.moneymanger.entity.ProfileEntity;
import com.alienCoders.moneymanger.repository.CategoryRepository;
import com.alienCoders.moneymanger.repository.IncomeRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Service
public class IncomeService {
    private final CategoryRepository categoryRepository;
    private final IncomeRepository incomeRepository;
    private final ProfileService profileService;

    // Adds a new income to the database
    public IncomeDTO addIncome(IncomeDTO dto){
        ProfileEntity profile=profileService.getCurrentProfile();
        CategoryEntity category=categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(()->new RuntimeException("Category not found"));
        IncomeEntity newIncome=toEntity(dto,category,profile);
        newIncome=  incomeRepository.save(newIncome);
        return toDTO(newIncome);
    }

    // Delete income by id
    public void deleteIncome(Long incomeId){
        ProfileEntity profile=profileService.getCurrentProfile();
        IncomeEntity incomeEntity=incomeRepository.findById(incomeId)
                .orElseThrow(()->new RuntimeException("Income not found"));
        if(profile.getId().equals(incomeEntity.getProfile().getId())) {
            incomeRepository.deleteById(incomeId);
        }
        else {
            throw new RuntimeException("Unauthorized to delete this income");
        }
    }


    //Retrieves all incomes for current month/based on the start and end date
    public List<IncomeDTO> getCurrentMonthIncomeForCurrentUser(){
        ProfileEntity profileEntity=profileService.getCurrentProfile();
        LocalDate startDate=LocalDate.now().withDayOfMonth(1);
        LocalDate endDate=LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        List<IncomeEntity> incomeEntities=incomeRepository.findByProfileIdAndDateBetween(profileEntity.getId(),startDate,endDate);
        return incomeEntities.stream().map(this::toDTO).toList();
    }

    // Get latest 5 income for current user
    public List<IncomeDTO> getLatestFiveIncomeList(){
        ProfileEntity profileEntity=profileService.getCurrentProfile();
        List<IncomeEntity> incomeEntities=incomeRepository.findTop5ByProfileIdOrderByDateDesc(profileEntity.getId());
        return incomeEntities.stream().map(this::toDTO).toList();
    }

    // Get total sum amount of all the income for current user
    public BigDecimal getTotalIncome(){
        ProfileEntity profileEntity=profileService.getCurrentProfile();
        BigDecimal total= incomeRepository.findTotalIncomeByProfileId(profileEntity.getId());
        return total!=null?total:BigDecimal.ZERO;
    }

    // filter method for income
    public List<IncomeDTO> filterIncome(LocalDate startDate, LocalDate endDate, String keyword, Sort sort){
        ProfileEntity profileEntity=profileService.getCurrentProfile();
        List<IncomeEntity> incomeEntityList=incomeRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(profileEntity.getId(),startDate,endDate,keyword,sort);
        return incomeEntityList.stream().map(this::toDTO).toList();
    }

    //helper methods
    private IncomeEntity toEntity(IncomeDTO incomeDTO, CategoryEntity category, ProfileEntity profile){
        return IncomeEntity.builder()
                .name(incomeDTO.getName())
                .icon(incomeDTO.getIcon())
                .amount(incomeDTO.getAmount())
                .date(incomeDTO.getDate())
                .profile(profile)
                .category(category)
                .build();
    }

    private IncomeDTO toDTO(IncomeEntity entity){
        return IncomeDTO.builder()
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
