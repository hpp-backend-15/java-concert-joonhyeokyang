package com.joonhyeok.app.common;

import com.joonhyeok.openapi.models.SeatResponse;
import jakarta.validation.Valid;

import java.util.Collection;
import java.util.List;

public interface DtoMapper<P, S> {

    P toPresentation(S serviceDto);

    S toService(P presentationDto);

    List<P> toPresentationList(List<S> serviceDtoList);

    List<S> toServiceList(List<P> presentationDtoList);

}