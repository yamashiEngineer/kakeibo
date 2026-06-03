package com.example.kakeibo2.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "transactions") //【point】記載必須
@Data
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer amount;
    private String category;
    private String memo;

    //【point】変数がキャメルケースなのでスネークケースで指定する
    @Column(name = "txn_date")
    private LocalDate txnDate;

    private String type; // "income" または "expense"
}