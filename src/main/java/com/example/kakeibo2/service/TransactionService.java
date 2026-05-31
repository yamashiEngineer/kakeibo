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

    public Optional<Transaction> getTransactionById(Integer id) {
        return transactionRepository.findById(id);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveTransaction(Transaction transaction) {
        try {
            transactionRepository.save(transaction);
        } catch (Exception e) {
            System.err.println("【エラー】データ保存に失敗しました: " + e.getMessage()); // スタックトレース単体出力を回避
            throw new RuntimeException("データの保存に失敗しました。");
        }
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