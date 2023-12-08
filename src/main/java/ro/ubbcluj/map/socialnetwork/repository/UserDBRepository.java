package ro.ubbcluj.map.socialnetwork.repository;


import ro.ubbcluj.map.socialnetwork.domain.Utilizator;
import ro.ubbcluj.map.socialnetwork.domain.validators.UtilizatorValidator;

import java.sql.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class UserDBRepository implements Repository<Long, Utilizator> {

    private final UtilizatorValidator validator;
    private String url;
    private String username;
    private String password;

    public UserDBRepository(UtilizatorValidator validator, String url, String username, String password) {
        this.validator = validator;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public Optional<Utilizator> findOne(Long longID) {
        try(Connection connection = DriverManager.getConnection(url, username, password);
            PreparedStatement statement = connection.prepareStatement("select * from users " +
                    "where id = ?");

        ) {
            statement.setInt(1, Math.toIntExact(longID));
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                String username = resultSet.getString("username");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String password = resultSet.getString("password");
                Utilizator u = new Utilizator(username, firstName, lastName);
                u.setPassword(password);
                u.setId(longID);
                return Optional.of(u);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Iterable findAll() throws SQLException {
        Set<Utilizator> users = new HashSet<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("select * from users");
             ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next())
            {
                Long id= resultSet.getLong("id");
                String username = resultSet.getString("username");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
//                String password = resultSet.getString("password");
                Utilizator user=new Utilizator(username,firstName,lastName);
                user.setId(id);
                users.add(user);

            }
            return users;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Utilizator> save(Utilizator utilizator) {

        if (utilizator==null)
            throw new IllegalArgumentException("entity must be not null");

        validator.validate(utilizator);

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO users(id, username, first_name, last_name, password) VALUES (?, ?, ?, ?, ?)"
             )) {
            statement.setLong(1, utilizator.getId());
            statement.setString(2, utilizator.getUsername());
            statement.setString(3, utilizator.getFirstName());
            statement.setString(4, utilizator.getLastName());
            statement.setString(5, utilizator.getPassword());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Utilizator> delete(Long aLong) {

        if (findOne(aLong).isEmpty())
            throw new IllegalArgumentException("ID inexistent");

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM users WHERE id =" + aLong);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Utilizator> update(Utilizator utilizator) {
        if (utilizator == null) {
            throw new IllegalArgumentException("Eroare, user null");
        }

        validator.validate(utilizator);

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE users SET first_name = ?, last_name = ? WHERE id = ?"
             )) {
            statement.setString(1, utilizator.getFirstName());
            statement.setString(2, utilizator.getLastName());
            statement.setLong(3, utilizator.getId());
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        return Optional.empty();
    }

    public int countUsers() throws SQLException {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM users");
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return resultSet.getInt(1);
            } else {
                throw new SQLException("Nu s-a putut obține numărul de înregistrări.");
            }
        }
    }

}
