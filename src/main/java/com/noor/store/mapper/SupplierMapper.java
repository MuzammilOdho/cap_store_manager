package com.noor.store.mapper;

import com.noor.store.dto.SupplierDTO;
import com.noor.store.model.Supplier;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SupplierMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Supplier toEntity(SupplierDTO.Request request);

    SupplierDTO.Response toResponse(Supplier s);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(@MappingTarget Supplier target, SupplierDTO.Request request);
}
