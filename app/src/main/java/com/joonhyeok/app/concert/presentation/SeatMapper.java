package com.joonhyeok.app.concert.presentation;

import com.joonhyeok.app.common.DtoMapper;
import com.joonhyeok.app.concert.domain.Seat;
import com.joonhyeok.openapi.models.SeatResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SeatMapper extends DtoMapper<SeatResponse, Seat> {
}
