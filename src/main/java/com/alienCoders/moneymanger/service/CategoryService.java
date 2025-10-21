package com.alienCoders.moneymanger.service;


import com.alienCoders.moneymanger.dto.CategoryDTO;
import com.alienCoders.moneymanger.entity.CategoryEntity;
import com.alienCoders.moneymanger.entity.ProfileEntity;
import com.alienCoders.moneymanger.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final ProfileService profileService;
    private final CategoryRepository categoryRepository;


    public CategoryDTO saveCategory(CategoryDTO categoryDTO) {
        ProfileEntity profile = profileService.getCurrentProfile();
        if (categoryRepository.existsByNameAndProfileId(categoryDTO.getName(), profile.getId())) {
            throw new RuntimeException("Category with this name already exist");
        }
        CategoryEntity newCategory = toEntity(categoryDTO, profile);
        newCategory = categoryRepository.save(newCategory);
        return toDto(newCategory);
    }

    public List<CategoryDTO> getCaterogyForCurrentUser() {
        ProfileEntity profileEntity = profileService.getCurrentProfile();
        List<CategoryEntity> categoryEntities = categoryRepository.findByProfileId(profileEntity.getId());
        return categoryEntities.stream().map(this::toDto).toList();
    }

    public List<CategoryDTO> getCategoryTypeForCurrentUser(String type) {
        ProfileEntity profileEntity = profileService.getCurrentProfile();
        List<CategoryEntity> categoryEntities = categoryRepository.findByTypeAndProfileId(type, profileEntity.getId());
        return categoryEntities.stream().map(this::toDto).toList();
    }

    public CategoryDTO updateCategory(Long categoryId, CategoryDTO dto) {
        ProfileEntity profileEntity = profileService.getCurrentProfile();
        CategoryEntity categoryEntity = categoryRepository.findByIdAndProfileId(categoryId, profileEntity.getId())
                .orElseThrow(() -> new RuntimeException("Category not found or not accessible"));
        categoryEntity.setName(dto.getName());
        categoryEntity.setIcon(dto.getIcon());
        categoryEntity = categoryRepository.save(categoryEntity);
        return toDto(categoryEntity);
    }

    //helper methods

    private CategoryEntity toEntity(CategoryDTO categoryDTO, ProfileEntity profileEntity) {
        return CategoryEntity.builder().name(categoryDTO.getName()).icon(categoryDTO.getIcon()).type(categoryDTO.getType()).profile(profileEntity).build();
    }

    private CategoryDTO toDto(CategoryEntity categoryEntity) {
        return CategoryDTO.builder().id(categoryEntity.getId()).name(categoryEntity.getName()).type(categoryEntity.getType()).profileId(categoryEntity.getProfile().getId()).icon(categoryEntity.getIcon()).createdAt(categoryEntity.getCreatedAt()).updatedAt(categoryEntity.getUpdatedAt()).build();
    }
}
