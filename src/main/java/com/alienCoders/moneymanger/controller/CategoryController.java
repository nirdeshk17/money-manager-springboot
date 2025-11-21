package com.alienCoders.moneymanger.controller;


import com.alienCoders.moneymanger.dto.CategoryDTO;
import com.alienCoders.moneymanger.entity.CategoryEntity;
import com.alienCoders.moneymanger.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<?> saveCategory(@RequestBody CategoryDTO categoryDTO){
       try {
           CategoryDTO savedCategory=categoryService.saveCategory(categoryDTO);
           return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
       }
       catch (RuntimeException e){
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message",e.getMessage()));
       }

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
            @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO updatedCategoryDto = categoryService.updateCategory(categoryId, categoryDTO);
        return ResponseEntity.ok(updatedCategoryDto);
    }

}
