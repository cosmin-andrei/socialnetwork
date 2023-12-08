package ro.ubbcluj.map.socialnetwork.repository;

import ro.ubbcluj.map.socialnetwork.domain.Message;
import ro.ubbcluj.map.socialnetwork.domain.Tuple;
import ro.ubbcluj.map.socialnetwork.domain.validators.MessageValidator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MessageDBRepository implements Repository<Tuple<Long, Long>, Message> {

    private final MessageValidator validator;
    private final String url;
    private final String username;
    private final String password;

    public MessageDBRepository(MessageValidator validator, String url, String username, String password) {
        this.validator = validator;
        this.url = url;
        this.username = username;
        this.password = password;
    }


    @Override
    public Optional<Message> findOne(Tuple<Long, Long> longLongTuple) {
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM \"messages\" WHERE sender = ? and receiver =?;")) {
            statement.setLong(1, longLongTuple.getLeft());
            statement.setLong(2, longLongTuple.getRight());
            try (ResultSet resultSet = statement.executeQuery()) {

                Long idSender = resultSet.getLong("sender");
                Long idReceiver = resultSet.getLong("receiver");
                String text = resultSet.getString("text");
                Timestamp date = resultSet.getTimestamp("data");

                Message message = new Message(idSender, idReceiver, text);
                message.setId(new Tuple<>(idSender, idReceiver));
                message.setDate(date.toLocalDateTime());

                return Optional.of(message);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Iterable<Message> findAll() {
        List<Message> messages = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement statement = connection.createStatement()) {

            try (ResultSet resultSet = statement.executeQuery("SELECT * FROM messages")) {

                while (resultSet.next()) {
                    Long idSender = resultSet.getLong("sender");
                    Long idReceiver = resultSet.getLong("receiver");
                    String text = resultSet.getString("text");
                    Timestamp date = resultSet.getTimestamp("date");

                    Message message = new Message(idSender, idReceiver, text);
                    message.setId(new Tuple<>(idSender,idReceiver));
                    message.setDate(date.toLocalDateTime());

                    messages.add(message);
                }

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return messages;
    }


    @Override
    public Optional<Message> save(Message entity) {

        if (entity == null)
            throw new IllegalArgumentException("entity must be not null");

        validator.validate(entity);

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("insert into \"messages\"(sender, receiver, text, date) values (?,?,?,?)")) {

            statement.setLong(1, entity.getIdSender());
            statement.setLong(2, entity.getIdReceiver());
            statement.setString(3, entity.getText());
            statement.setDate(4, Date.valueOf(entity.getDate().toLocalDate()));

            statement.executeUpdate();

        }catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.of(entity);
    }

    @Override
    public Optional<Message> delete(Tuple<Long, Long> longLongTuple) {
        return Optional.empty();
    }


    @Override
    public Optional<Message> update(Message entity) {
        return Optional.empty();
    }


}
