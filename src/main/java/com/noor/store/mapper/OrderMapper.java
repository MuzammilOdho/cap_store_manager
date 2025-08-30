package com.noor.store.mapper;

import com.noor.store.dto.OrderDTO;
import com.noor.store.model.Order;
import com.noor.store.model.OrderItem;
import com.noor.store.model.Category;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface OrderMapper {

    @Mapping(target = "supplierId", expression = "java(order.getSupplier()!=null ? order.getSupplier().getId() : null)")
    @Mapping(target = "supplierName", expression = "java(order.getSupplier()!=null ? order.getSupplier().getName() : null)")
    @Mapping(target = "buyerId", expression = "java(order.getBuyer()!=null ? order.getBuyer().getId() : null)")
    @Mapping(target = "buyerName", expression = "java(order.getBuyer()!=null ? order.getBuyer().getName() : null)")
    @Mapping(target = "items", source = "items")
    OrderDTO.OrderResponse toResponse(Order order);

    @Mapping(target = "categoryId", expression = "java(item.getCategory()!=null ? item.getCategory().getId() : null)")
    @Mapping(target = "categoryName", expression = "java(item.getCategory()!=null ? item.getCategory().getName() : null)")
    OrderDTO.ItemResponse toItemResponse(OrderItem item);

    List<OrderDTO.ItemResponse> toItemResponses(List<OrderItem> items);

    // Request -> entity mapping (some fields ignored; service handles items)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderNumber", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "remainingAmount", ignore = true)
    @Mapping(target = "items", ignore = true)
    Order toEntity(OrderDTO.OrderRequest req);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "lineTotal", ignore = true)
    @Mapping(target = "costOfGoodsSold", ignore = true)
    OrderItem toItemEntity(OrderDTO.ItemRequest req, @Context Category category);
}
