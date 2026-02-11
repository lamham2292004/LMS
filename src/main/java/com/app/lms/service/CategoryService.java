package com.app.lms.service;

import com.app.lms.dto.request.categoryRequest.CategoryCreateRequest;
import com.app.lms.dto.request.categoryRequest.CategoryUpdateRequest;
import com.app.lms.dto.response.CategoryResponse;
import com.app.lms.entity.Category;
import com.app.lms.exception.AppException;
import com.app.lms.exception.ErroCode;
import com.app.lms.mapper.CategoryMapper;
import com.app.lms.repository.CategoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryService {
    final CategoryRepository categoryRepository;
    final CategoryMapper categoryMapper;

    @CacheEvict(value = "categories", allEntries = true)
    public CategoryResponse createCategory(CategoryCreateRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new AppException(ErroCode.NAME_CATEGORY_INVALID);
        }
        Category category = categoryMapper.toCategoryMapper(request);

        return categoryMapper.toCategoryResponse(categoryRepository.save(category));
    }

    @Cacheable(value = "categories", key = "'all'")
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toCategoryResponse).collect(Collectors.toList());
    }

    public CategoryResponse getCategoryById(Long categoryId) {
        return categoryMapper.toCategoryResponse(categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErroCode.CATEGORY_NO_EXISTED)));

    }

    public CategoryResponse updateCategory(Long categoryId, CategoryUpdateRequest request) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AppException(ErroCode.CATEGORY_NO_EXISTED));
        categoryMapper.updateCategory(category, request);
        return categoryMapper.toCategoryResponse(categoryRepository.save(category));
    }

    public void deleteCategory(Long categoryId) {
        categoryRepository.deleteById(categoryId);
    }
}
