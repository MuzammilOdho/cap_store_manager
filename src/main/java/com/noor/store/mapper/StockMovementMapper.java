package com.noor.store.mapper;

import com.noor.store.dto.StockMovementDTO;
import com.noor.store.dto.StockMovementDTO.*;
import com.noor.store.model.CategoryStockMovement;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StockMovementMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "categoryId", expression = "java(entity.getCategory()!=null ? entity.getCategory().getId() : null)")
    @Mapping(target = "categoryName", expression = "java(entity.getCategory()!=null ? entity.getCategory().getName() : null)")
    @Mapping(target = "movementType", expression = "java(entity.getMovementType()!=null ? entity.getMovementType().name() : null)")
    StockMovementDTO.Response toResponse(CategoryStockMovement entity);
}
