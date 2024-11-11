package com.joonhyeok.app.concert.infra;

import com.joonhyeok.app.concert.domain.Concert;
import com.joonhyeok.app.concert.domain.ConcertRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

public interface ConcertJpaRepository extends ConcertRepository, JpaRepository<Concert, Long> {

}
