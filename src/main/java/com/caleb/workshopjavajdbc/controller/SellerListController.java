package com.caleb.workshopjavajdbc.controller;

import com.caleb.workshopjavajdbc.HelloApplication;
import com.caleb.workshopjavajdbc.db.DbIntegrityException;
import com.caleb.workshopjavajdbc.gui.listeners.DataChangeListener;
import com.caleb.workshopjavajdbc.gui.util.Alerts;
import com.caleb.workshopjavajdbc.gui.util.Utils;
import com.caleb.workshopjavajdbc.model.entities.Seller;
import com.caleb.workshopjavajdbc.model.services.DepartmentService;
import com.caleb.workshopjavajdbc.model.services.SellerService;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class SellerListController implements Initializable, DataChangeListener {

    private SellerService SellerService;

    @FXML
    private TableView<Seller> SellerTableView;
    @FXML
    private TableColumn<Seller, Integer> tableColumnId;
    @FXML
    private TableColumn<Seller, String> tableColumnName;
    @FXML
    private TableColumn<Seller, Seller> tableColumnEDIT;
    @FXML
    private TableColumn<Seller, Seller> tableColumnREMOVE;
    @FXML
    private TableColumn<Seller, String> tableColumnEmail;
    @FXML
    private TableColumn<Seller, Date> tableColumnBirthDate;
    @FXML
    private TableColumn<Seller, Double> tableColumnBaseSalary;
    @FXML
    private Button buttonNew;

    private ObservableList<Seller> observableList;

    @FXML
    public void onBtNewAction(ActionEvent event) throws IOException {
        Stage parentStage = Utils.currentStage(event);
        Seller obj = new Seller();
        createDialogForm(obj, "/com/caleb/workshopjavajdbc/SellerForm.fxml", parentStage);
    }

    public void setSellerService(SellerService service){
        this.SellerService = service;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeNodes();
    }

    private void initializeNodes() {
        tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        tableColumnBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        Utils.formatTableColumnDate(tableColumnBirthDate, "dd/MM/yyyy");
        tableColumnBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
        Utils.formatTableColumnDouble(tableColumnBaseSalary, 2);

        Stage stage = (Stage) HelloApplication.getScene().getWindow();
        SellerTableView.prefHeightProperty().bind(stage.heightProperty());
    }

    public void updateTableView(){
        if(SellerService == null){
            throw new IllegalStateException("Service was null!");
        }
        List<Seller> list = SellerService.findAll();
        observableList = FXCollections.observableArrayList(list);
        SellerTableView.setItems(observableList);
        initEditButtons();
        initRemoveButtons();
    }

    private void createDialogForm(Seller obj, String absoluteName, Stage parentStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
        Pane pane = loader.load();

        SellerFormController controller = loader.getController();
        controller.setSeller(obj);
        controller.setService(new SellerService(), new DepartmentService());
        controller.loadAssociatedObjects();
        controller.subscribeDataChangeListener(this);
        controller.updateFormData();

        Stage dialogStage = new Stage();
        dialogStage.setTitle("Enter Seller data");
        dialogStage.setScene(new Scene(pane));
        dialogStage.setResizable(false);
        dialogStage.initOwner(parentStage);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.showAndWait();
    }

    @Override
    public void onDataChanged() {
        updateTableView();
    }

    private void initEditButtons(){
        tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumnEDIT.setCellFactory(param -> new TableCell<Seller, Seller>(){
            private final Button button = new Button("edit");

            @Override
            protected void updateItem(Seller item, boolean empty) {
                super.updateItem(item, empty);

                if(item == null){
                    setGraphic(null);
                    return;
                }

                setGraphic(button);
                button.setOnAction(
                        event -> {
                            try {
                                createDialogForm(item,
                                        "/com/caleb/workshopjavajdbc/SellerForm.fxml", Utils.currentStage(event));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
            }
        });
    }

    private void initRemoveButtons(){
        tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumnREMOVE.setCellFactory(param -> new TableCell<Seller, Seller>(){
        private final Button button = new Button("remove");
            @Override
            protected void updateItem(Seller item, boolean empty) {
                super.updateItem(item, empty);

                if(item == null){
                    setGraphic(null);
                    return;
                }

                setGraphic(button);
                button.setOnAction(event -> removeEntity(item));
            }
        });
    }

    private void removeEntity(Seller item) {
       Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Are you sure to delete?");
        if(result.get() == ButtonType.OK){
            if(SellerService == null) {
                throw new IllegalStateException("Service was null");
            }
            try {
                SellerService.remove(item);
                updateTableView();
            }
            catch (DbIntegrityException e){
                Alerts.showAlert("Error removing object", null, e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
}
