package com.safayildirim.ozanappcase.service;

import com.safayildirim.ozanappcase.model.Transaction;
import org.springframework.data.repository.CrudRepository;

public interface TransactionRepository extends CrudRepository<Transaction, Long> {
}
