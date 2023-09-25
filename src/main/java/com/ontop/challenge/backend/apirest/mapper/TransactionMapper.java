package com.ontop.challenge.backend.apirest.mapper;

import com.ontop.challenge.backend.apirest.dto.transaction.TransactionDto;
import com.ontop.challenge.backend.apirest.entities.TransactionEntity;
import java.util.List;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@Mapper(componentModel = ComponentModel.SPRING)
public interface TransactionMapper {

//    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);

    @Mappings({
        @Mapping(source = "id", target = "id"),
        @Mapping(source = "amountSent", target = "amountSent"),
        @Mapping(source = "transactionFee", target = "transactionFee"),
        @Mapping(source = "recipientGets", target = "recipientGets"),
        @Mapping(source = "refundedAmount", target = "refundedAmount"),
        @Mapping(source = "associatedTransactionId", target = "associatedTransactionId"),
        @Mapping(source = "userId", target = "userId"),
        @Mapping(source = "createdAt", target = "createdAt", dateFormat = "yyyy-MM-dd HH:mm:ss"),
        @Mapping(source = "status", target = "status"),
        @Mapping(source = "message", target = "message"),
        @Mapping(source = "recipient", target = "recipient"),
    })
    TransactionDto toDto(TransactionEntity transactionEntity);

    @InheritInverseConfiguration
    TransactionEntity toEntity(TransactionDto transactionDto);

    List<TransactionDto> toListDto(List<TransactionEntity> transactionEntities);

    List<TransactionEntity> toListEntity(List<TransactionDto> transactionEntities);

    default Page<TransactionDto> toPageDto(Page<TransactionEntity> transactionEntityPage) {
        List<TransactionDto> transactionDtos = toListDto(transactionEntityPage.getContent());
        return new PageImpl<>(transactionDtos, transactionEntityPage.getPageable(), transactionEntityPage.getTotalElements());
    }
}
