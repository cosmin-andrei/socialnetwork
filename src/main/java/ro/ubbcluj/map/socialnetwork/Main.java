package ro.ubbcluj.map.socialnetwork;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ro.ubbcluj.map.socialnetwork.domain.Utilizator;
import ro.ubbcluj.map.socialnetwork.domain.validators.*;
import ro.ubbcluj.map.socialnetwork.repository.*;
import ro.ubbcluj.map.socialnetwork.repository.PagingRepository.UserDBPagingRepository;
import ro.ubbcluj.map.socialnetwork.service.CerereService;
import ro.ubbcluj.map.socialnetwork.service.ConversationService;
import ro.ubbcluj.map.socialnetwork.service.PrietenieService;
import ro.ubbcluj.map.socialnetwork.service.UtilizatorService;

public class Main extends Application {

    final String url = "jdbc:postgresql://localhost:5432/socialnetwork";
    final String username = "postgres";
    final String password = "2003";

    UserDBRepository repoUtilizator = new UserDBRepository(new UtilizatorValidator(), url, username, password);
    UserDBPagingRepository userDBPagingRepository = new UserDBPagingRepository(new UtilizatorValidator(), url, username, password);
    UtilizatorService serv = new UtilizatorService(userDBPagingRepository, repoUtilizator);

    CerereValidator cerereValidator = new CerereValidator();
    PrietenieValidator prietenieValidator = new PrietenieValidator();
    CerereDBRepository cerereDBRepository = new CerereDBRepository(cerereValidator, url, username, password);
    PrietenieDBRepository prietenieDBRepository = new PrietenieDBRepository(prietenieValidator, url, username, password);
    UtilizatorValidator utilizatorValidator = new UtilizatorValidator();
    UserDBRepository userDBRepository = new UserDBPagingRepository(utilizatorValidator, url, username, password);
    CerereService cerereService = new CerereService(prietenieDBRepository, userDBRepository, cerereDBRepository);
    PrietenieService prietenieService = new PrietenieService(repoUtilizator, prietenieDBRepository);
    ConversationDBRepository conversationDBRepository = new ConversationDBRepository(new ConversationValidator(), url, username, password);
    ConversationService conversationService = new ConversationService(conversationDBRepository, prietenieDBRepository, userDBRepository);

    public static void main(String[] args) {
        launch(args);
    }

    public void loginStage(Stage primaryStage) throws Exception {
        FXMLLoader loginLoader = new FXMLLoader();
        loginLoader.setLocation(getClass().getResource("login-view.fxml"));

        VBox loginVBox = loginLoader.load();  // Schimbați aici VBox în loc de AnchorPane
        LoginController loginController = loginLoader.getController();
        loginController.setMain(this);

        Scene scene = new Scene(loginVBox);  // Și aici

        primaryStage.setTitle("Retea de socializare");
        primaryStage.setScene(scene);

        loginController.setService(serv, primaryStage);

        primaryStage.show();
    }



    public void openUserStage(Utilizator user) throws Exception {
        FXMLLoader userLoader = new FXMLLoader();
        userLoader.setLocation(getClass().getResource("user-view.fxml"));


        Stage userStage = new Stage();
        Scene userScene = new Scene(userLoader.load());

        userStage.setTitle("Panou user");
        userStage.setScene(userScene);

        UserController userController = userLoader.getController();
        userController.setService(user, serv, cerereService, prietenieService, conversationService, userStage);

        userStage.show();
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        loginStage(primaryStage);
    }

}
