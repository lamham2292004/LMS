package com.app.lms.mapper;

import com.app.lms.dto.request.categoryRequest.CategoryCreateRequest;
import com.app.lms.dto.request.categoryRequest.CategoryUpdateRequest;
import com.app.lms.dto.response.CategoryResponse;
import com.app.lms.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "Spring", uses = {CourseMapper.class})
public interface CategoryMapper {

    Category toCategoryMapper(CategoryCreateRequest request);

    CategoryResponse toCategoryResponse(Category category);

    void updateCategory(@MappingTarget Category category, CategoryUpdateRequest request);
}
