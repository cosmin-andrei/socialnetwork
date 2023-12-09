package ro.ubbcluj.map.socialnetwork;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import ro.ubbcluj.map.socialnetwork.controller.MessageAlert;
import ro.ubbcluj.map.socialnetwork.domain.Message;
import ro.ubbcluj.map.socialnetwork.domain.Utilizator;
import ro.ubbcluj.map.socialnetwork.service.MessageService;

import java.util.List;

public class ChannelController {

    @FXML
    private TextArea msgArea;
    private Stage stage;
    private MessageService messageService;
    private Utilizator fromUser;
    private List<Utilizator> toUsers;


    public void setService(MessageService messageService, Stage stage, Utilizator fromUser, List<Utilizator> toUsers) {
        this.fromUser = fromUser;
        this.stage = stage;
        this.toUsers = toUsers;
        this.messageService=messageService;
    }


    public void handleSend() {
        System.out.println(toUsers);
        String msgAreaText = msgArea.getText();
        System.out.println(msgAreaText);
        try{
            for(Utilizator user: toUsers){
                Message message = new Message(fromUser, user, msgAreaText);
                messageService.addMessage(message);
                System.out.println("Am fost pe aici");
            }
            stage.close();
        }catch (Exception e){
            MessageAlert.showErrorMessage(null, "Eroare: " + e.getMessage());
        }
    }



}
