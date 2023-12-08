package ro.ubbcluj.map.socialnetwork;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import ro.ubbcluj.map.socialnetwork.controller.MessageAlert;
import ro.ubbcluj.map.socialnetwork.domain.Message;
import ro.ubbcluj.map.socialnetwork.domain.Tuple;
import ro.ubbcluj.map.socialnetwork.domain.Utilizator;
import ro.ubbcluj.map.socialnetwork.observer.Observer;
import ro.ubbcluj.map.socialnetwork.service.MessageService;

import java.sql.SQLException;
import java.util.Collection;

public class MessageController implements Observer {

    @FXML
    private TextArea msgArea;
    @FXML
    TableView<Message> tableViewMessages;
    @FXML
    TableColumn<Message, String> fromColumn;
    @FXML
    TableColumn<Message, String> toColumn;
    @FXML
    TableColumn<Message, String> messagesColumn;
    @FXML
    TableColumn<Message, String> dateColumn;

    private MessageService messageService;
    private final ObservableList<Message> model = FXCollections.observableArrayList();
    private Utilizator fromUser;
    private Utilizator toUser;

    public MessageController() {
    }

    public void setService(MessageService messageService, Utilizator fromUser, Utilizator toUser) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.messageService=messageService;

        messageService.registerObserver(this);
        initModel();
    }

    public void initialize() {
        fromColumn.setCellValueFactory(new PropertyValueFactory<>("from"));
        toColumn.setCellValueFactory(new PropertyValueFactory<>("to"));
        messagesColumn.setCellValueFactory(new PropertyValueFactory<>("messages"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        tableViewMessages.setItems(model);
    }

    private void initModel() {
        Collection<Message> all = messageService.conversation(fromUser.getId(), toUser.getId());
        model.setAll(all);
    }

    public void handleSend() {
        String msgAreaText = msgArea.getText();
        System.out.println(msgAreaText);
        try{
            Message message = new Message(fromUser.getId(), toUser.getId(), msgAreaText);
            message.setId(new Tuple<>(fromUser.getId(), toUser.getId()));
            messageService.addMessage(message);
            initModel();
        }catch (Exception e){
            MessageAlert.showErrorMessage(null, "Eroare: " + e.getMessage());
        }
    }

    @Override
    public void update() throws SQLException {
        initModel();
    }



}
