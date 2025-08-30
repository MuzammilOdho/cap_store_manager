package com.noor.store.mapper;

import com.noor.store.dto.BuyerDTO;
import com.noor.store.model.Buyer;
import org.mapstruct.*;
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BuyerMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Buyer toEntity(BuyerDTO.Request request);

    BuyerDTO.Response toResponse(Buyer b);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(@MappingTarget Buyer target, BuyerDTO.Request request);
}
