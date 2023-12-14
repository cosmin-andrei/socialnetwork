package ro.ubbcluj.map.socialnetwork.repository;

import ro.ubbcluj.map.socialnetwork.domain.Message;
import ro.ubbcluj.map.socialnetwork.domain.Utilizator;
import ro.ubbcluj.map.socialnetwork.domain.validators.MessageValidator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MessageDBRepository implements Repository<Long, Message> {

    private final MessageValidator validator;

    private Connection connection;

    public MessageDBRepository(MessageValidator validator, String url, String username, String password) {
        this.validator = validator;
        try {
            connection = DriverManager.getConnection(url, username, password);
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Optional<Message> findOne(Long id) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM messages WHERE id=?")) {

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Long idSender = resultSet.getLong("sender");
                    Long idReceiver = resultSet.getLong("receiver");
                    String text = resultSet.getString("text");
                    Timestamp date = resultSet.getTimestamp("date");
                    Long idReply = resultSet.getLong("idReply");

                    Utilizator sender = new Utilizator(null, null, null);
                    sender.setId(idSender);

                    Utilizator receiver = new Utilizator(null, null, null);
                    receiver.setId(idReceiver);

                    Message message = new Message(sender, receiver, text);
                    message.setId(id);
                    message.setDate(date.toLocalDateTime());
                    message.setIdReply(idReply);

                    return Optional.of(message);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }



    @Override
    public Iterable<Message> findAll() {
        List<Message> messages = new ArrayList<>();

        try (Statement statement = connection.createStatement()) {

            try (ResultSet resultSet = statement.executeQuery("SELECT * FROM messages")) {

                while (resultSet.next()) {
                    Long idSender = resultSet.getLong("sender");
                    Long idReceiver = resultSet.getLong("receiver");
                    String text = resultSet.getString("text");
                    Timestamp date = resultSet.getTimestamp("date");
                    Long id = resultSet.getLong("id");
                    Long idReply = resultSet.getLong("idReply");

                    Utilizator sender = new Utilizator(null, null, null);
                    sender.setId(idSender);

                    Utilizator receiver = new Utilizator(null, null, null);
                    receiver.setId(idReceiver);

                    Message message = new Message(sender, receiver, text);
                    message.setId(id);
                    message.setDate(date.toLocalDateTime());
                    message.setIdReply(idReply);

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

        try (PreparedStatement statement = connection.prepareStatement("insert into \"messages\"(sender, receiver, text, date, id, idreply) values (?,?,?,?,?,?)")) {

            statement.setLong(1, entity.getSender().getId());
            statement.setLong(2, entity.getReceiver().getId());
            statement.setString(3, entity.getText());
            statement.setDate(4, Date.valueOf(entity.getDate().toLocalDate()));
            statement.setLong(5, entity.getId());
            if(entity.getIdReply() != 0)
                statement.setLong(6, entity.getIdReply());
            else {
                statement.setNull(6, Types.BIGINT);
            }

            statement.executeUpdate();

        }catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.of(entity);
    }

    @Override
    public Optional<Message> delete(Long id) {
        return Optional.empty();
    }


    @Override
    public Optional<Message> update(Message entity) {
        return Optional.empty();
    }


}
