package net.breezeware.propel.hibernate.hibernate;

import net.breezeware.propel.hibernate.annotation.Column;
import net.breezeware.propel.hibernate.annotation.PrimaryKey;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.Boolean.TRUE;

public class Hibernate<T> {

    private Connection connection;
    private AtomicInteger id = new AtomicInteger(0);

    public static <T> Hibernate<T> getConnection() throws SQLException {
        return new Hibernate<T>();
    }

    private Hibernate() throws SQLException {
        this.connection = DriverManager.getConnection("jdbc:h2:/home/deepak/Work/accounts/breezeware/code/propel/reflection-annotation/custom-hibernate-demo/db/h2/custom", "custom", "");
    }

    public void write(T t) throws SQLException, IllegalAccessException {

        Class<?> tClass = t.getClass();

        Field[] declaredFields = tClass.getDeclaredFields();
        Field primaryKeyField = null;
        List<Field> columnFields = new ArrayList<Field>();
        StringJoiner columnFieldsJoiner = new StringJoiner(",");
        for (Field field : declaredFields) {
            field.setAccessible(TRUE);
            if (field.isAnnotationPresent(PrimaryKey.class)) {
                try {
                    primaryKeyField = field;
                    System.out.println("Primary key field name - " + field.getName() + " and value - " + field.get(t));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }

            } else if (field.isAnnotationPresent(Column.class)) {
                try {
                    columnFieldsJoiner.add(field.getName());
                    columnFields.add(field);
                    System.out.println("Column field name - " + field.getName() + " and value - " + field.get(t));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
//        System.out.println("columnFieldsJoiner - " + columnFieldsJoiner);

//        System.out.println("columnFieldJoiner length() - " + columnFieldsJoiner.length());
        int queryParamValueFieldsCount = columnFields.size() + 1;
        String queryParamFields = IntStream.range(0, queryParamValueFieldsCount)
                .mapToObj(i -> "?")
                .collect(Collectors.joining(","));
//        System.out.println("queryParamFields - " + queryParamFields);

        String writeSqlQuery = "insert into "
                + tClass.getSimpleName() +
                " (" + primaryKeyField.getName() + "," + columnFieldsJoiner + ") "
                + "values (" + queryParamFields.toString() + ");";
        System.out.println(writeSqlQuery);

        PreparedStatement preparedStatement = connection.prepareStatement(writeSqlQuery);
//        if (primaryKeyField.getType() == int.class)
//            preparedStatement.setInt(1, id.incrementAndGet());
//        System.out.println("preparedStatement - " + preparedStatement);

        int paramIndexWithoutPrimaryKey = 1;
        for (Field field : declaredFields) {
//            System.out.println("field - " + field.getName());
            field.setAccessible(TRUE);
//            System.out.println("field type - " + field.getType());
            if (field.getType() == String.class) {
//                System.out.println("paramIndexWithoutPrimaryKey - " + paramIndexWithoutPrimaryKey);
                preparedStatement.setString(paramIndexWithoutPrimaryKey, (String) field.get(t));
                paramIndexWithoutPrimaryKey += 1;
//                System.out.println("paramIndexWithoutPrimaryKey - " + paramIndexWithoutPrimaryKey);
//                System.out.println("preparedStatement - " + preparedStatement);
            } else if (field.getType() == int.class) {
//                System.out.println("paramIndexWithoutPrimaryKey - " + paramIndexWithoutPrimaryKey);
                if (field == primaryKeyField)
                    preparedStatement.setInt(paramIndexWithoutPrimaryKey, id.incrementAndGet());
                else{
                preparedStatement.setInt(paramIndexWithoutPrimaryKey, (int) field.get(t));
                }
                paramIndexWithoutPrimaryKey += 1;
//                System.out.println("paramIndexWithoutPrimaryKey - " + paramIndexWithoutPrimaryKey);
//                System.out.println("preparedStatement - " + preparedStatement);
            }
        }
        System.out.println("preparedStatement - " + preparedStatement);

        preparedStatement.executeUpdate();
    }
}
