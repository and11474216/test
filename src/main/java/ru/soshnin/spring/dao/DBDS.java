package ru.soshnin.spring.dao;

import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DBDS {
    private static Properties properties = null;
    private static PGSimpleDataSource dataSource;

    static {

        String url = null;
        String username = null;
        String password = null;

        try (InputStream in = DBDS.class.getClassLoader().getResourceAsStream("database.properties")) {

            properties = new Properties();
            properties.load(in);

            url = properties.getProperty("url");
            username = properties.getProperty("username");
            password = properties.getProperty("password");

            dataSource = new PGSimpleDataSource();
            dataSource.setURL(url);
            dataSource.setUser(username);
            dataSource.setPassword(password);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static DataSource getDataSource() {
        return dataSource;
    }
}
