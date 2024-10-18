package com.joonhyeok.app.concert.presentation;

import com.joonhyeok.app.common.DtoMapper;
import com.joonhyeok.app.concert.domain.PerformanceDate;
import com.joonhyeok.openapi.models.PerformanceDateResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PerformanceDateMapper extends DtoMapper<PerformanceDateResponse, PerformanceDate> {
}
