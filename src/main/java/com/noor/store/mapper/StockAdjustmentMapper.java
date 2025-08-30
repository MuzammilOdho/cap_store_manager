package com.noor.store.mapper;

import com.noor.store.dto.StockAdjustmentDTO;
import com.noor.store.model.Category;
import com.noor.store.model.StockAdjustment;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StockAdjustmentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", expression = "java(categoryFromId(req.categoryId()))")
    @Mapping(target = "adjustmentType", expression = "java(com.noor.store.model.StockAdjustment.AdjustmentType.valueOf(req.adjustmentType()))")
    @Mapping(target = "adjustmentDate", expression = "java(java.time.LocalDateTime.now())")
    StockAdjustment toEntity(StockAdjustmentDTO.Request req);

    @Mapping(target = "categoryId", expression = "java(entity.getCategory()!=null ? entity.getCategory().getId() : null)")
    @Mapping(target = "categoryName", expression = "java(entity.getCategory()!=null ? entity.getCategory().getName() : null)")
    @Mapping(target = "adjustmentType", expression = "java(entity.getAdjustmentType()!=null ? entity.getAdjustmentType().name() : null)")
    StockAdjustmentDTO.Response toResponse(StockAdjustment entity);

    default com.noor.store.model.Category categoryFromId(Long id) {
        if (id == null) return null;
        Category c = new Category();
        c.setId(id);
        return c;
    }
}
