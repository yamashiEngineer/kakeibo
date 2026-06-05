package com.example.kakeibo2.service;

import com.example.kakeibo2.entity.Transaction;
import com.example.kakeibo2.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAllByOrderByTxnDateAsc();
    }

    public Transaction getTransactionById(Integer id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("指定された取引データ（ID: " + id + "）が見つかりません。"));
    }

    @Transactional
    public void saveTransaction(Transaction transaction) {
        // try-catchは不要。例外はそのまま上に放り投げる
        transactionRepository.save(transaction);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteTransaction(Integer id) {
        try {
            transactionRepository.deleteById(id);
        } catch (Exception e) {
            System.err.println("【エラー】データ削除に失敗しました: " + e.getMessage());
            throw new RuntimeException("データの削除に失敗しました。");
        }
    }

    public List<Map<String, Object>> getMonthlySummary() {
        return transactionRepository.findMonthlySummary();
    }
}