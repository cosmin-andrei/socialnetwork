package ro.ubbcluj.map.socialnetwork;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ContextMenuEvent;
import ro.ubbcluj.map.socialnetwork.controller.MessageAlert;
import ro.ubbcluj.map.socialnetwork.domain.Message;
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
        fromColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSender().getFirstName()));
        toColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getReceiver().getFirstName()));
        messagesColumn.setCellValueFactory(new PropertyValueFactory<>("text"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        tableViewMessages.setItems(model);
    }

    private void initModel() {
        Collection<Message> all = messageService.conversation(fromUser.getId(), toUser.getId());
        model.setAll(all);
    }

    public void handleSend() {
        Message selectedMessage = tableViewMessages.getSelectionModel().getSelectedItem();
        String msgAreaText = msgArea.getText();
        try {
            Message message = new Message(fromUser, toUser, msgAreaText);
            if (selectedMessage != null) {
                message.setIdReply(selectedMessage.getId());
                String replyInfo = "Reply la: " + selectedMessage.getText() + "\n";
                msgAreaText = replyInfo + msgAreaText;
            }
            message.setText(msgAreaText);
            messageService.addMessage(message);
            initModel();
            tableViewMessages.getSelectionModel().clearSelection();
            msgArea.clear();
        } catch (Exception e) {
            MessageAlert.showErrorMessage(null, "Eroare: " + e.getMessage());
        }
    }


    @Override
    public void update() throws SQLException {
        initModel();
    }


}
