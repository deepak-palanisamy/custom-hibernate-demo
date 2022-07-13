package net.breezeware.propel;

import net.breezeware.propel.di.ApplicationConfig;
import net.breezeware.propel.di.ApplicationContext;
import net.breezeware.propel.service.impl.TransactionServiceImpl;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public class Application {
    public static void main(String[] args) throws SQLException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
//        Transaction transaction1 = new Transaction("CREDIT", 20000, "1234", "4321");
//        Transaction transaction2 = new Transaction("DEBIT", 7000, "4321", "1234");
//        Transaction transaction3 = new Transaction("CREDIT", 1500, "1234", "4321");
//        Transaction transaction4 = new Transaction("DEBIT", 100, "4321", "1234");

//        Hibernate<Transaction> transactionHibernate = Hibernate.getConnection();

//        transactionHibernate.write(transaction1);
//        transactionHibernate.write(transaction2);
//        transactionHibernate.write(transaction3);
//        transactionHibernate.write(transaction4);
//        Transaction storedTransaction1 = transactionHibernate.read(Transaction.class, 5);
//        System.out.println("Transaction 1 from database - " + storedTransaction1);
//        List<Transaction> storedTransactions1 = transactionHibernate.read(Transaction.class, "type", "DEBIT");
//        System.out.println("Transactions from database - " + storedTransactions1);


        // DI implementation
        ApplicationContext applicationContext = new ApplicationContext(ApplicationConfig.class);

        TransactionServiceImpl transactionServiceImpl = applicationContext.getBean(TransactionServiceImpl.class);
        int totalTransaction1 = transactionServiceImpl.getTotalTransaction("1234");
        System.out.println("totalTransaction1 - " + totalTransaction1);
        int totalTransaction2 = transactionServiceImpl.getTotalTransaction("4321");
        System.out.println("totalTransaction2 - " + totalTransaction2);

    }
}