package com.noor.store.mapper;

import com.noor.store.dto.MiscExpenseDTO;
import com.noor.store.model.MiscExpense;
import org.mapstruct.*;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MiscExpenseMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true)
    })
    MiscExpense toEntity(MiscExpenseDTO.Request request);

    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "description", source = "description"),
            @Mapping(target = "amount", source = "amount"),
            @Mapping(target = "expenseDate", source = "expenseDate"),
            @Mapping(target = "notes", source = "notes")
    })
    MiscExpenseDTO.Response toResponse(MiscExpense entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(@MappingTarget MiscExpense target, MiscExpenseDTO.Request request);
}
