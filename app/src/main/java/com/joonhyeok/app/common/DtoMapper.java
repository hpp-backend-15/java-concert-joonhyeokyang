package com.joonhyeok.app.common;

import java.util.List;

public interface DtoMapper<P, S> {

    P toPresentation(S serviceDto);

    S toService(P presentationDto);

    List<P> toPresentationList(List<S> serviceDtoList);

    List<S> toServiceList(List<P> presentationDtoList);

}