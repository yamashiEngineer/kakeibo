package com.example.kakeibo2.repository;

import com.example.kakeibo2.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    // 仕様：txn_dateの昇順で取得
    List<Transaction> findAllByOrderByTxnDateAsc();

    // 発展仕様：GROUP BY と SUM() を使用した月別集計
    @Query(value = "SELECT " +
            "DATE_FORMAT(txn_date, '%Y-%m') AS month, " +
            "SUM(CASE WHEN type = 'income' THEN amount ELSE 0 END) AS totalIncome, " +
            "SUM(CASE WHEN type = 'expense' THEN amount ELSE 0 END) AS totalExpense, " +
            "SUM(CASE WHEN type = 'income' THEN amount ELSE -amount END) AS balance " +
            "FROM transactions " +
            "GROUP BY DATE_FORMAT(txn_date, '%Y-%m') " +
            "ORDER BY month DESC", nativeQuery = true)
    List<Map<String, Object>> findMonthlySummary();
}