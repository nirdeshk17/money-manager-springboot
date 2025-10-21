package com.alienCoders.moneymanger.controller;


import com.alienCoders.moneymanger.dto.CategoryDTO;
import com.alienCoders.moneymanger.entity.CategoryEntity;
import com.alienCoders.moneymanger.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDTO> saveCategory( CategoryDTO categoryDTO){
        CategoryDTO savedCategory=categoryService.saveCategory(categoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
    }

    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getCategories(){
        List<CategoryDTO> categoryDTOList=categoryService.getCaterogyForCurrentUser();
        return ResponseEntity.ok(categoryDTOList);
    }

    @GetMapping("/{type}")
    public ResponseEntity<List<CategoryDTO>> getCategoriesType(@PathVariable String type){
        List<CategoryDTO> categoryDTOList=categoryService.getCategoryTypeForCurrentUser(type);
        return ResponseEntity.ok(categoryDTOList);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(
            @PathVariable Long categoryId,
            @ModelAttribute CategoryDTO categoryDTO) {
        CategoryDTO updatedCategoryDto = categoryService.updateCategory(categoryId, categoryDTO);
        return ResponseEntity.ok(updatedCategoryDto);
    }

}
