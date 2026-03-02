package com.example.thy.mapper;

import org.modelmapper.ModelMapper; // ModelMapper bağımlılığı eklediğini varsayıyorum
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ApplicationMapper {

    private final ModelMapper modelMapper;


    public <S, T> T map(S source, Class<T> targetClass) {
        if (source == null) return null;
        return modelMapper.map(source, targetClass);
    }

    public <S, T> List<T> mapList(List<S> sourceList, Class<T> targetClass) {
        return sourceList.stream()
                .map(source -> map(source, targetClass))
                .collect(Collectors.toList());
    }
}