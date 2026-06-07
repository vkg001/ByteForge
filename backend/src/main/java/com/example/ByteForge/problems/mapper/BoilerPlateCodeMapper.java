package com.example.ByteForge.problems.mapper;

import com.example.ByteForge.problems.dto.request.BoilerPlateCodeRequestDto;
import com.example.ByteForge.problems.dto.response.BoilerPlateCodeResponseDto;
import com.example.ByteForge.problems.entities.BoilerPlateCodeEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BoilerPlateCodeMapper {

    public BoilerPlateCodeEntity toEntity(BoilerPlateCodeRequestDto dto) {
        if (dto == null) {
            return null;
        }

        BoilerPlateCodeEntity entity = new BoilerPlateCodeEntity();
        entity.setLanguageCode(dto.getLanguageCode());
        entity.setUserCode(dto.getUserCode());
        entity.setPrependCode(dto.getPrependCode());
        entity.setAppendCode(dto.getAppendCode());

        return entity;
    }

    public List<BoilerPlateCodeEntity> toEntityList(List<BoilerPlateCodeRequestDto> dtoList) {
        if (dtoList == null) {
            return new ArrayList<>();
        }

        List<BoilerPlateCodeEntity> res = new ArrayList<>();
        for (var dto : dtoList) {
            res.add(toEntity(dto));
        }

        return res;
    }

    public BoilerPlateCodeResponseDto toResponseDto(BoilerPlateCodeEntity entity, Boolean setPrepend, Boolean setAppend) {
        if (entity == null) {
            return null;
        }

        BoilerPlateCodeResponseDto responseDto = new BoilerPlateCodeResponseDto();
        responseDto.setLanguageCode(entity.getLanguageCode());
        responseDto.setUserCode(entity.getUserCode());

        // Safely evaluate potentially null Boolean objects and map accordingly
        responseDto.setPrependCode(Boolean.TRUE.equals(setPrepend) ? entity.getPrependCode() : "");
        responseDto.setAppendCode(Boolean.TRUE.equals(setAppend) ? entity.getAppendCode() : "");

        return responseDto;
    }

    public List<BoilerPlateCodeResponseDto> toResponseDtoList(List<BoilerPlateCodeEntity> entityList, Boolean setPrepend, Boolean setAppend) {
        if (entityList == null) {
            return new ArrayList<>();
        }

        List<BoilerPlateCodeResponseDto> res = new ArrayList<>();
        for (var entity : entityList) {
            res.add(toResponseDto(entity, setPrepend, setAppend));
        }

        return res;
    }
}