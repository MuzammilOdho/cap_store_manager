package com.noor.store.mapper;

import com.noor.store.dto.CategoryDTO;
import com.noor.store.model.Category;
import org.mapstruct.*;
import java.time.Instant;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "currentStock", ignore = true )
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Category toEntity(CategoryDTO.Request request);

    @Mapping(target = "createdAt", source = "createdAt")
    CategoryDTO.Response toResponse(Category category);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(@MappingTarget Category target, CategoryDTO.Request request);
}
