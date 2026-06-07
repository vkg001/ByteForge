package com.example.ByteForge.problems.mapper;

import com.example.ByteForge.problems.dto.request.ExampleRequestDto;
import com.example.ByteForge.problems.dto.response.ExampleResponseDto;
import com.example.ByteForge.problems.entities.ExampleEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ExampleMapper {

    public ExampleEntity toEntity(ExampleRequestDto dto) {
        if (dto == null) {
            return null;
        }

        ExampleEntity entity = new ExampleEntity();
        entity.setInput(dto.getInput());
        entity.setOutput(dto.getOutput());
        entity.setExplanation(dto.getExplanation());

        return entity;
    }

    public List<ExampleEntity> toEntityList(List<ExampleRequestDto> dtoList) {
        if (dtoList == null) {
            return new ArrayList<>();
        }

        List<ExampleEntity> res = new ArrayList<>();
        for (var dto : dtoList) {
            res.add(toEntity(dto));
        }

        return res;
    }

    public ExampleResponseDto toResponseDto(ExampleEntity entity) {
        if (entity == null) {
            return null;
        }

        ExampleResponseDto responseDto = new ExampleResponseDto();
        responseDto.setInput(entity.getInput());
        responseDto.setOutput(entity.getOutput());
        responseDto.setExplanation(entity.getExplanation());

        return responseDto;
    }

    public List<ExampleResponseDto> toResponseDtoList(List<ExampleEntity> entityList) {
        if (entityList == null) {
            return new ArrayList<>();
        }

        List<ExampleResponseDto> res = new ArrayList<>();
        for (var entity : entityList) {
            res.add(toResponseDto(entity));
        }

        return res;
    }
}