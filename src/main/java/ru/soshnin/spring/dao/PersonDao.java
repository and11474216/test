package ru.soshnin.spring.dao;

import org.springframework.stereotype.Component;
import ru.soshnin.spring.models.Person;
import ru.soshnin.spring.models.Visit;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Component
public class PersonDao {

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static double PRICE = 10000;

    String url = "jdbc:postgresql://localhost:5432/PersonBase";
    String user = "postgres";
    String password = "1";

    public List<Person> index() {
        List<Person> people = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("select * from person");
        ) {
            while (resultSet.next()) {
                Person person = new Person();
                person.setName(resultSet.getString(2));
                person.setId(resultSet.getInt(1));
                people.add(person);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return people;
    }

    public Person show(int id) {
        Person person = new Person();
        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("select * from person where id_card=" + id);
        ) {
            resultSet.next();
            person.setName(resultSet.getString(2));
            person.setSurname(resultSet.getString(3));
            person.setPhone(resultSet.getString(4));
            person.setBegin(resultSet.getDate(7));
            person.setEnd(resultSet.getDate(6));
            person.setId(resultSet.getInt(1));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return person;
    }

    public void save(Person person) {
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement preparedStatement = connection.prepareStatement("insert into person (name, surname, phone, begin, \"end\", price) values (?,?,?,?,?,?)");
        ) {
            preparedStatement.setString(1, person.getName());
            preparedStatement.setString(2, person.getSurname());
            preparedStatement.setString(3, person.getPhone());
            preparedStatement.setDate(5, Date.valueOf(person.getBegin().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().plusYears(1)));
            preparedStatement.setDate(4, Date.valueOf(person.getBegin().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()));
            preparedStatement.setDouble(6, PRICE);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Person price(int id) {
        Person person = show(id);


        Date temp = new Date(person.getBegin().getTime());
        LocalDate begin = temp.toLocalDate();
        LocalDate plusYears = begin.plusYears(1);
        LocalDate plusFiveYears = begin.plusYears(5);
        LocalDate today = LocalDate.now();

        int beginYear = begin.getYear();
        int todayYear = today.getYear();
        int countYear = beginYear - todayYear;

//        Date temp2 = new Date(person.getEnd().getTime());
//        LocalDate end = temp2.toLocalDate().plusYears(1);
//        System.out.println(end);
        int numberOfVisitsForLastYear = 0;
        int numberOfVisits = 0;

        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("select count(*) from visits where date_of_visit >= NOW()::date - interval '1 Year' and  id_card=" + id);
        ) {
            //посчиать из второй таблицы посещения по id за последний год
            resultSet.next();
            numberOfVisitsForLastYear = resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("select count(*) from visits where id_card=" + id);
        ) {
            //посчиать из второй таблицы посещения по id за все время
            resultSet.next();
            numberOfVisits = resultSet.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (plusFiveYears.isBefore(today) && ((numberOfVisits / countYear) > 180)) {
            person.setPrice(PRICE * 0.5);
            return person;
        } else if (plusYears.isAfter(today) && (numberOfVisitsForLastYear > 78)) {
            person.setPrice(PRICE * 0.9);
            return person;
        } else {
            person.setPrice(PRICE);
            return person;
        }
    }

    public void addPrice(int id, double price) {
        Person person = show(id);
        person.setPrice(price);
        Date tempDate = new Date(person.getEnd().getTime());
        LocalDate end = tempDate.toLocalDate().plusYears(1);

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement preparedStatement = connection.prepareStatement("update person set price = (?), \"end\" = (?) where id_card=" + id);
        ) {
            preparedStatement.setDate(2, Date.valueOf(end));
            preparedStatement.setDouble(1, person.getPrice());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addVisit(int id) {
        Person person = show(id);
        person.setPresence(true);
        LocalDate today = LocalDate.now();

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement preparedStatement = connection.prepareStatement("update person set presence = (?) where id_card=" + id);
        ) {
            preparedStatement.setBoolean(1, person.isPresence());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement preparedStatement = connection.prepareStatement("insert into visits (id_card, date_of_visit) values (?,?)");
        ) {
            preparedStatement.setInt(1, person.getId());
            preparedStatement.setDate(2, Date.valueOf(today));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Person> presence() {
        List<Person> people = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("select * from person where presence = true");
        ) {
            while (resultSet.next()) {
                Person person = new Person();
                person.setSurname(resultSet.getString(3));
                person.setName(resultSet.getString(2));
                person.setId(resultSet.getInt(1));
                people.add(person);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return people;
    }

    public void out(int id){
        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement preparedStatement = connection.prepareStatement("update person set presence = (?) where id_card=" + id);
        ) {
            preparedStatement.setBoolean(1, false);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
