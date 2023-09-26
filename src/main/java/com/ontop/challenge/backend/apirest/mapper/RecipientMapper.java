package com.ontop.challenge.backend.apirest.mapper;

import com.ontop.challenge.backend.apirest.dto.recipient.RecipientDto;
import com.ontop.challenge.backend.apirest.entities.RecipientEntity;
import java.util.List;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants.ComponentModel;
import org.mapstruct.Mappings;

@Mapper(componentModel = ComponentModel.SPRING)
public interface RecipientMapper {


    @Mappings({
        @Mapping(source = "id", target = "id"),
        @Mapping(source = "firstName", target = "firstName"),
        @Mapping(source = "lastName", target = "lastName"),
        @Mapping(source = "routingNumber", target = "routingNumber"),
        @Mapping(source = "nationalIdNumber", target = "nationalIdNumber"),
        @Mapping(source = "accountNumber", target = "accountNumber"),
        @Mapping(source = "bankName", target = "bankName")
    })
    RecipientDto toDto(RecipientEntity recipientEntity);

    @InheritInverseConfiguration
    RecipientEntity toEntity(RecipientDto recipientDto);

    List<RecipientDto> toListDto(List<RecipientEntity> recipientEntities);

    List<RecipientEntity> toListEntity(List<RecipientDto> recipientDtoList);

}
