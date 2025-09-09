package com.app.lms.controller;

import com.app.lms.dto.request.ApiResponse;
import com.app.lms.dto.request.categoryRequest.CategoryCreateRequest;
import com.app.lms.dto.request.categoryRequest.CategoryUpdateRequest;
import com.app.lms.dto.response.CategoryResponse;
import com.app.lms.service.CategoryService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryController {
    final CategoryService categoryService;

    @PostMapping("createCategory")
    ApiResponse<CategoryResponse> createCategory(@Valid @RequestBody CategoryCreateRequest request) {
         ApiResponse<CategoryResponse> apiResponse = new ApiResponse<>();

         apiResponse.setResult(categoryService.createCategory(request));

         return apiResponse;
    }

    @GetMapping("{categoryId}")
    ApiResponse<CategoryResponse> getCategory(@Valid @PathVariable("categoryId") Long categoryId) {
        ApiResponse<CategoryResponse> apiResponse = new ApiResponse<>();

        apiResponse.setResult(categoryService.getCategoryById(categoryId));

        return apiResponse;
    }

    @GetMapping
    ApiResponse<List<CategoryResponse>> getAllCategory() {
        ApiResponse<List<CategoryResponse>> apiResponse = new ApiResponse<>();

        apiResponse.setResult(categoryService.getAllCategories());

        return apiResponse;
    }

    @PutMapping("{categoryId}")
    ApiResponse<CategoryResponse> updateCategory( @Valid @PathVariable Long categoryId, @RequestBody CategoryUpdateRequest request) {
        ApiResponse<CategoryResponse> apiResponse = new ApiResponse<>();

        apiResponse.setResult(categoryService.updateCategory(categoryId, request));

        return apiResponse;
    }

    @DeleteMapping("{categoryId}")
    String deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return "Deleted Category";
    }

}
