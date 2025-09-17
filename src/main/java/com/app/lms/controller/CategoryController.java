package com.app.lms.controller;

import com.app.lms.annotation.CurrentUser;
import com.app.lms.dto.auth.UserTokenInfo;
import com.app.lms.dto.request.ApiResponse;
import com.app.lms.dto.request.categoryRequest.CategoryCreateRequest;
import com.app.lms.dto.request.categoryRequest.CategoryUpdateRequest;
import com.app.lms.dto.response.CategoryResponse;
import com.app.lms.service.CategoryService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryController {
    CategoryService categoryService;

    @PostMapping("/createCategory")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LECTURER')")
    public ApiResponse<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryCreateRequest request,
            @CurrentUser UserTokenInfo currentUser) {

        ApiResponse<CategoryResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(categoryService.createCategory(request));
        return apiResponse;
    }

    @GetMapping("{categoryId}")
    // Public - tất cả user đã login đều xem được
    public ApiResponse<CategoryResponse> getCategory(@Valid @PathVariable("categoryId") Long categoryId) {
        ApiResponse<CategoryResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(categoryService.getCategoryById(categoryId));
        return apiResponse;
    }

    @GetMapping
    // Public - không cần authentication để browse categories
    public ApiResponse<List<CategoryResponse>> getAllCategory() {
        ApiResponse<List<CategoryResponse>> apiResponse = new ApiResponse<>();
        apiResponse.setResult(categoryService.getAllCategories());
        return apiResponse;
    }

    @PutMapping("{categoryId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LECTURER')")
    public ApiResponse<CategoryResponse> updateCategory(
            @Valid @PathVariable Long categoryId,
            @RequestBody CategoryUpdateRequest request,
            @CurrentUser UserTokenInfo currentUser) {

        ApiResponse<CategoryResponse> apiResponse = new ApiResponse<>();
        apiResponse.setResult(categoryService.updateCategory(categoryId, request));
        return apiResponse;
    }

    @DeleteMapping("{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ApiResponse.<String>builder()
                .result("Category deleted successfully")
                .build();
    }

}
