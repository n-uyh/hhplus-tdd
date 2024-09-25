package io.hhplus.tdd.point.repository;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PointHistoryRepo implements PointHistoryRepository {

    private final PointHistoryTable pointHistoryTable;

    @Override
    public PointHistory insert(long userId, long amount, TransactionType type) {
        return pointHistoryTable.insert(userId, amount, type, System.currentTimeMillis());
    }

    @Override
    public List<PointHistory> selectAllByUserId(long userId) {
        return pointHistoryTable.selectAllByUserId(userId);
    }
}
