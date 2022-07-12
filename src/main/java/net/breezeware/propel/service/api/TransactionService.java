package net.breezeware.propel.service.api;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

public interface TransactionService {
    int getTotalTransaction(String accountNumber) throws SQLException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, InstantiationException;
}
