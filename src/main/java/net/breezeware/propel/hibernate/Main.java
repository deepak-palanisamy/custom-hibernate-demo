package net.breezeware.propel.hibernate;

import net.breezeware.propel.hibernate.entity.Transaction;
import net.breezeware.propel.hibernate.hibernate.Hibernate;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException, IllegalAccessException {
        Transaction transaction1 = new Transaction("CREDIT", 20000, "1234", "4321");
        Transaction transaction2 = new Transaction("DEBIT", 7000, "4321", "1234");
        Transaction transaction3 = new Transaction("CREDIT", 1500, "1234", "4321");
        Transaction transaction4 = new Transaction("DEBIT", 100, "4321", "1234");

        Hibernate<Transaction> transactionHibernate = Hibernate.getConnection();

//        transactionHibernate.write(transaction1);
//        transactionHibernate.write(transaction2);
//        transactionHibernate.write(transaction3);
        transactionHibernate.write(transaction4);
    }
}