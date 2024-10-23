package com.joonhyeok.app.user.application;

import com.joonhyeok.app.user.application.dto.AccountBalanceQuery;
import com.joonhyeok.app.user.application.dto.AccountBalanceQueryResult;
import com.joonhyeok.app.user.application.dto.AccountChargeCommand;
import com.joonhyeok.app.user.application.dto.AccountChargeResult;
import com.joonhyeok.app.user.domain.Account;
import com.joonhyeok.app.user.domain.User;
import com.joonhyeok.app.user.domain.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AccountService {
    private final UserRepository userRepository;

    @Transactional
    public AccountChargeResult charge(AccountChargeCommand command) {
        long userId = command.userId();
        long chargeAmount = command.amount();

        User user = userRepository.findWithLockById(userId).orElseThrow(
                () -> new EntityNotFoundException("존재하지 않는 유저입니다. userId = " + userId)
        );

        Account account = user.chargePoint(chargeAmount);

        return new AccountChargeResult(account.getBalance(), account.getModifiedAt());
    }

    public AccountBalanceQueryResult query(AccountBalanceQuery query) {
        Long userId = query.userId();
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("존재하지 않는 유저입니다. userId = " + userId)
        );

        Account account = user.getAccount();

        return new AccountBalanceQueryResult(account.getBalance());
    }
}
