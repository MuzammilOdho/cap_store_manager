package com.noor.store.mapper;

import com.noor.store.dto.PaymentDTO;
import com.noor.store.model.Payment;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true) // service will set order
    Payment toEntity(PaymentDTO.Request request);

    @Mapping(target = "orderId", expression = "java(p.getOrder()!=null ? p.getOrder().getId() : null)")
    @Mapping(target = "orderNumber", expression = "java(p.getOrder()!=null ? p.getOrder().getOrderNumber() : null)")
    PaymentDTO.Response toResponse(Payment p);
}
