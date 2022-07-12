package net.breezeware.propel.dao;

import net.breezeware.propel.annotation.Component;
import net.breezeware.propel.entity.Transaction;
import net.breezeware.propel.hibernate.Hibernate;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

@Component
public class TransactionDao {

    public List<Transaction> getTransactions(String accountNo) throws SQLException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException {
        Hibernate<Transaction> transactionHibernate = Hibernate.getConnection();
        List<Transaction> transactions = transactionHibernate.read(Transaction.class, "src", accountNo);
        return transactions;
    }

}
