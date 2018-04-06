package db;

import java.sql.Connection;

public abstract class DBConnectionFactory {
        String url = "jdbc:mysql://localhost/SRA";//jdbc:mysql://localhost/SRA
        String username="michael";//root
        String password="gochioco";//delfin

    public static DBConnectionFactory getInstance(){
        return new DBConnectionFactoryImpl();
    }
    
    public abstract Connection getConnection();
}
