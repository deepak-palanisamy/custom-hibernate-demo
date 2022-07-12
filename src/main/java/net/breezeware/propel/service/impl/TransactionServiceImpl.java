package net.breezeware.propel.service.impl;

import net.breezeware.propel.annotation.Autowired;
import net.breezeware.propel.annotation.Component;
import net.breezeware.propel.dao.TransactionDao;
import net.breezeware.propel.entity.Transaction;
import net.breezeware.propel.service.api.TransactionService;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

@Component
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionDao transactionDao;

    @Override
    public int getTotalTransaction(String accountNumber) throws SQLException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        List<Transaction> transactions = transactionDao.getTransactions(accountNumber);
        System.out.println("transactions - " + transactions);
        int totalTransactions = 0;
        for (Transaction transaction : transactions) {
            totalTransactions += transaction.getAmount();
        }
        return totalTransactions;
    }
}
