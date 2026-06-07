package com.example.ByteForge.problems.core.mappers;

import com.example.ByteForge.problems.core.dto.request.BoilerplateCodeRequestDto;
import com.example.ByteForge.problems.core.dto.response.BoilerplateCodeResponseDto;
import com.example.ByteForge.problems.core.entities.BoilerplateCodeEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BoilerplateCodeMapper {

    public BoilerplateCodeEntity toEntity(BoilerplateCodeRequestDto dto) {
        if (dto == null) {
            return null;
        }

        BoilerplateCodeEntity entity = new BoilerplateCodeEntity();
        entity.setLanguageCode(dto.getLanguageCode());
        entity.setUserCode(dto.getUserCode());
        entity.setPrependCode(dto.getPrependCode());
        entity.setAppendCode(dto.getAppendCode());

        return entity;
    }

    public List<BoilerplateCodeEntity> toEntityList(List<BoilerplateCodeRequestDto> dtoList) {
        if (dtoList == null) {
            return new ArrayList<>();
        }

        List<BoilerplateCodeEntity> res = new ArrayList<>();
        for (var dto : dtoList) {
            res.add(toEntity(dto));
        }

        return res;
    }

    public BoilerplateCodeResponseDto toResponseDto(BoilerplateCodeEntity entity, Boolean setPrepend, Boolean setAppend) {
        if (entity == null) {
            return null;
        }

        BoilerplateCodeResponseDto responseDto = new BoilerplateCodeResponseDto();
        responseDto.setLanguageCode(entity.getLanguageCode());
        responseDto.setUserCode(entity.getUserCode());

        // Safely evaluate potentially null Boolean objects and map accordingly
        responseDto.setPrependCode(Boolean.TRUE.equals(setPrepend) ? entity.getPrependCode() : "");
        responseDto.setAppendCode(Boolean.TRUE.equals(setAppend) ? entity.getAppendCode() : "");

        return responseDto;
    }

    public List<BoilerplateCodeResponseDto> toResponseDtoList(List<BoilerplateCodeEntity> entityList, Boolean setPrepend, Boolean setAppend) {
        if (entityList == null) {
            return new ArrayList<>();
        }

        List<BoilerplateCodeResponseDto> res = new ArrayList<>();
        for (var entity : entityList) {
            res.add(toResponseDto(entity, setPrepend, setAppend));
        }

        return res;
    }
}