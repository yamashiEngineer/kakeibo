package com.example.kakeibo2.controller;

import com.example.kakeibo2.entity.Transaction;
import com.example.kakeibo2.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    // ■ 機能1: 一覧表示
    @GetMapping("/transactions")
    public String list(Model model) {
        List<Transaction> list = transactionService.getAllTransactions();

        // t -> "income".equals(t.getType())はヌルぽしない良い書き方
        int totalIncome = list.stream().filter(t -> "income".equals(t.getType())).mapToInt(Transaction::getAmount).sum();
        int totalExpense = list.stream().filter(t -> "expense".equals(t.getType())).mapToInt(Transaction::getAmount).sum();

        model.addAttribute("transactions", list);
        model.addAttribute("totalIncome", totalIncome);
        model.addAttribute("totalExpense", totalExpense);
        model.addAttribute("balance", totalIncome - totalExpense);
        return "transactions/list";
    }

    // ■ 機能2: 登録フォーム表示
    @GetMapping("/transactions/new")
    public String showCreateForm(Model model) {
        model.addAttribute("transaction", new Transaction());
        return "transactions/form";
    }

    // ■ 機能3: 編集フォーム表示
    @GetMapping("/transactions/{id}/edit")
    public String showEditForm(@PathVariable("id") Integer id, Model model) {
        // Serviceから必ずデータが取れる前提（なければ勝手に例外が飛ぶ）
        Transaction transaction = transactionService.getTransactionById(id);
        model.addAttribute("transaction", transaction);
        return "transactions/form";
    }

    // ■ 機能2 & 3 共通: 保存処理（新規登録・更新を1つに集約してコード削減）
    @PostMapping({"/transactions", "/transactions/{id}"})
    public String save(@PathVariable(value = "id", required = false) Integer id, @ModelAttribute Transaction transaction) {
        if (id != null) {
            // 画面側に ID を持たせていない、またはセキュリティのため
            transaction.setId(id);
        }
        transactionService.saveTransaction(transaction);
        return "redirect:/transactions";
    }

    // ■ 機能4: 削除処理 (POST必須要件)
    @PostMapping("/transactions/{id}/delete")
    public String delete(@PathVariable("id") Integer id) {
        transactionService.deleteTransaction(id);
        return "redirect:/transactions";
    }

    // ■ 機能5: 月別集計
    @GetMapping("/summary")
    public String summary(Model model) {
        model.addAttribute("summaryList", transactionService.getMonthlySummary());
        return "summary/index";
    }

    // ■ 異常系（データがなかった時）の処理は、ここに完全に隔離する
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleNotFoundException(IllegalArgumentException ex, Model model) {
        // ユーザーに何が起きたか（エラー内容）を伝えて、エラー画面へ送る
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }

    // ■ 業務ロジック上の例外（データなし、バリデーション外の矛盾など）をキャッチしてエラー画面を表示する
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleBusinessException(IllegalArgumentException ex, Model model) {

        // 1. 開発者向けに、コンソールにエラーログを残す（原因究明のため）
        log.error("【業務エラー発生】", ex);

        // 2. ユーザー向けのエラーメッセージをModelに詰める
        // ex.getMessage() で Serviceクラスで書いたメッセージ（「指定されたIDは見つかりません」など）が取得できます
        model.addAttribute("errorMessage", ex.getMessage());

        // 3. 表示したいHTMLテンプレートのパスを文字列で返す
        // src/main/resources/templates/error.html を表示させる
        return "error";
    }
}