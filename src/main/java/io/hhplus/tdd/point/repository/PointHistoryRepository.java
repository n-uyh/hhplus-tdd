package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public interface PointHistoryRepository {
    PointHistory insert(long userId, long amount, TransactionType type);
    List<PointHistory> selectAllByUserId(long userId);
}
