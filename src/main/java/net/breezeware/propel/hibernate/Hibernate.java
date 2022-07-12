package net.breezeware.propel.hibernate;

import net.breezeware.propel.annotation.Column;
import net.breezeware.propel.annotation.PrimaryKey;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
                primaryKeyField = field;
//                    System.out.println("Primary key field name - " + field.getName() + " and value - " + field.get(t));

            } else if (field.isAnnotationPresent(Column.class)) {
                columnFieldsJoiner.add(field.getName());
                columnFields.add(field);
//                    System.out.println("Column field name - " + field.getName() + " and value - " + field.get(t));
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
                + "values (" + queryParamFields.toString() + ")";
//        System.out.println(writeSqlQuery);

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
                else {
                    preparedStatement.setInt(paramIndexWithoutPrimaryKey, (int) field.get(t));
                }
                paramIndexWithoutPrimaryKey += 1;
//                System.out.println("paramIndexWithoutPrimaryKey - " + paramIndexWithoutPrimaryKey);
//                System.out.println("preparedStatement - " + preparedStatement);
            }
        }
//        System.out.println("preparedStatement - " + preparedStatement);

        preparedStatement.executeUpdate();
    }

    public <T> T read(Class<T> tClass, int key) throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Field[] declaredFields = tClass.getDeclaredFields();
        Field primaryKeyField = null;
        for (Field field : declaredFields) {
            if (field.isAnnotationPresent(PrimaryKey.class)) {
                primaryKeyField = field;
                break;
            }
        }
        String readSqlQuery = "select * from " + tClass.getSimpleName() + " where " + primaryKeyField.getName() + " = " + key;
        PreparedStatement preparedStatement = connection.prepareStatement(readSqlQuery);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();

        T t = tClass.getConstructor().newInstance();
        for (Field field : declaredFields) {
            field.setAccessible(TRUE);
            if (field == primaryKeyField)
                field.set(t, resultSet.getInt(primaryKeyField.getName()));
            else if (field.isAnnotationPresent(Column.class)) {
                if (field.getType() == int.class)
                    field.set(t, resultSet.getInt(field.getName()));
                else if (field.getType() == String.class)
                    field.set(t, resultSet.getString(field.getName()));
            }
        }
        return t;
    }

    public <T> List<T> read(Class<T> tClass, String key, String value) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Field[] declaredFields = tClass.getDeclaredFields();
//        Field primaryKeyField = null;
        Field resultField = null;
        for (Field field : declaredFields) {
//            if (field.isAnnotationPresent(PrimaryKey.class)) {
//                primaryKeyField = field;
//            }
            if (field.getName().equals(key)) {
                resultField = field;
            }
        }
        String readSqlQuery = "select * from " + tClass.getSimpleName() + " where " + resultField.getName() + " = '" + value + "'";
        PreparedStatement preparedStatement = connection.prepareStatement(readSqlQuery);
        ResultSet resultSet = preparedStatement.executeQuery();
//        System.out.println("ResultSet - " + resultSet);
//        resultSet.next();
        List<T> ts = new ArrayList<>();
        while (resultSet.next()) {
            T t = tClass.getConstructor().newInstance();

            for (Field field : declaredFields) {
                field.setAccessible(TRUE);
                if (field.getType() == int.class)
                    field.set(t, resultSet.getInt(field.getName()));
                else if (field.getType() == String.class)
                    field.set(t, resultSet.getString(field.getName()));
//                if (field == primaryKeyField)
//                    field.set(t, resultSet.getInt(primaryKeyField.getName()));
//                else if (field.isAnnotationPresent(Column.class)) {
//                    if (field.getType() == int.class)
//                        field.set(t, resultSet.getInt(field.getName()));
//                    else if (field.getType() == String.class)
//                        field.set(t, resultSet.getString(field.getName()));
//                }
            }
            ts.add(t);
        }
//        System.out.println("ts - " + ts);

        return ts;
    }
}
