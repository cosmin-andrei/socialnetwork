package ro.ubbcluj.map.socialnetwork;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import ro.ubbcluj.map.socialnetwork.controller.MessageAlert;
import ro.ubbcluj.map.socialnetwork.domain.CererePrietenie;
import ro.ubbcluj.map.socialnetwork.domain.Tuple;
import ro.ubbcluj.map.socialnetwork.domain.Utilizator;
import ro.ubbcluj.map.socialnetwork.observer.Observer;
import ro.ubbcluj.map.socialnetwork.service.CerereService;
import ro.ubbcluj.map.socialnetwork.service.PrietenieService;
import ro.ubbcluj.map.socialnetwork.service.UtilizatorService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

public class RequestFriendController implements Observer {

    @FXML
    private Pagination bttnPage;
    @FXML
    private TextField filterUsername;
    @FXML
    private TextField filterPage;
    private CerereService cerereService;
    private UtilizatorService userService;
    private final ObservableList<Utilizator> model = FXCollections.observableArrayList();
    private Utilizator utilizator;
    private PrietenieService prietenieService;
    private Stage stage;


    @FXML
    TableView<Utilizator> tableViewUser;
    @FXML
    TableColumn<Utilizator, String> tableColumnPrenume;
    @FXML
    TableColumn<Utilizator, String> tableColumnUsername;
    @FXML
    TableColumn<Utilizator, String>  tableColumnNume;


    @FXML
    public void initialize() {
        tableColumnUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        tableColumnNume.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        tableColumnPrenume.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        tableViewUser.setItems(model);

        filterUsername.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                // Obține toți utilizatorii
                Collection<Utilizator> all = userService.getAll();

                // Filtrare în funcție de conținutul câmpului filterUsername
                Collection<Utilizator> filteredUsers = new ArrayList<>();
                for (Utilizator user : all) {
                    if (user.getUsername().toLowerCase().contains(newValue.toLowerCase())) {
                        filteredUsers.add(user);
                    }
                }

                // Actualizează modelul cu utilizatorii filtrați
                model.setAll(filteredUsers);
            } catch (SQLException e) {
                MessageAlert.showErrorMessage(null, "Eroare: " + e.getMessage());
            }
        });

        filterPage.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                userService.setPage(1);
                userService.setPageSize(Integer.parseInt(newValue));
                initModel();
            } catch (NumberFormatException e) {
                MessageAlert.showErrorMessage(null, "Introduceți un număr valid pentru pagină.");
            } catch (SQLException e) {
                MessageAlert.showErrorMessage(null, "Eroare: " + e.getMessage());
            }
        });

    }

    public void setRequestService(UtilizatorService userService, CerereService cerereService, PrietenieService prietenieService, Stage stage, Utilizator utilizator) throws SQLException {
        this.userService = userService;
        this.cerereService = cerereService;
        this.prietenieService= prietenieService;
        this.stage = stage;
        this.utilizator = utilizator;
        cerereService.registerObserver(this);
        initModel();
    }


    private void initModel() throws SQLException {

        Collection<Utilizator> all;
        if (!filterPage.getText().isEmpty()) {
            userService.setPage(1);
            userService.setPageSize(Integer.parseInt(filterPage.getText()));
            all = userService.getUsers();
        }
        else {
            all = userService.getAll();
        }


        Iterator<Utilizator> iterator = all.iterator();
        while (iterator.hasNext()) {
            Utilizator user = iterator.next();
            if (Objects.equals(user.getId(), utilizator.getId())) {
                iterator.remove();
            }
            else if(prietenieService.verifyPrietenie(utilizator.getId(), user.getId())){
                iterator.remove();
            }
            else if(cerereService.verifyCerere(utilizator.getId(), user.getId())){
                iterator.remove();
            }
        }

        model.setAll(all);
    }


    public void handleAdd() {
        Utilizator user = tableViewUser.getSelectionModel().getSelectedItem();
        if (user != null) {
            CererePrietenie cererePrietenie = new CererePrietenie();
            cererePrietenie.setId(new Tuple<>(utilizator.getId(), user.getId()));
            cererePrietenie.setStatus("PENDING");
            try{
                cerereService.addCerere(cererePrietenie);
                MessageAlert.showMessage(stage, Alert.AlertType.INFORMATION, "Cerere prietenie", "Cererea a fost trimisă cu succes.");
                stage.close();
            } catch (Exception e){
                MessageAlert.showErrorMessage(null, "Eroare: " + e.getMessage());
            }
        } else {
            MessageAlert.showErrorMessage(null, "Niciun utilizator selectat.");
        }
    }
    @Override
    public void update() throws SQLException {
        initModel();
    }

}
