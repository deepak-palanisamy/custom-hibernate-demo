package net.breezeware.propel.hibernate.h2;

import org.h2.tools.Server;

import java.sql.SQLException;

public class Launcher {
    public static void main(String[] args) throws SQLException {
        Server.main("-ifNotExists");
    }
}
