package com.example.kakeibo2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TransactionController {

    // 「http://localhost:8080/transactions」にアクセスがあったら動くメソッド
    @GetMapping("/transactions")
    public String list() {
        // templates/transactions/list.html を呼び出す
        return "transactions/list";
    }
}