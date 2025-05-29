package com.caleb.workshopjavajdbc.controller;

import com.caleb.workshopjavajdbc.HelloApplication;
import com.caleb.workshopjavajdbc.db.DbIntegrityException;
import com.caleb.workshopjavajdbc.gui.listeners.DataChangeListener;
import com.caleb.workshopjavajdbc.gui.util.Alerts;
import com.caleb.workshopjavajdbc.gui.util.Utils;
import com.caleb.workshopjavajdbc.model.entities.Department;
import com.caleb.workshopjavajdbc.model.services.DepartmentService;
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
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class DepartmentListController implements Initializable, DataChangeListener {

    private DepartmentService departmentService;

    @FXML
    private TableView<Department> departmentTableView;
    @FXML
    private TableColumn<Department, Integer> tableColumnId;
    @FXML
    private TableColumn<Department, String> tableColumnName;
    @FXML
    private TableColumn<Department, Department> tableColumnEDIT;
    @FXML
    private TableColumn<Department, Department> tableColumnREMOVE;
    @FXML
    private Button buttonNew;

    private ObservableList<Department> observableList;

    @FXML
    public void onBtNewAction(ActionEvent event) throws IOException {
        Stage parentStage = Utils.currentStage(event);
        Department obj = new Department();
        createDialogForm(obj, "/com/caleb/workshopjavajdbc/DepartmentForm.fxml", parentStage);
    }

    public void setDepartmentService(DepartmentService service){
        this.departmentService = service;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeNodes();
    }

    private void initializeNodes() {
        tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));

        Stage stage = (Stage) HelloApplication.getScene().getWindow();
        departmentTableView.prefHeightProperty().bind(stage.heightProperty());
    }

    public void updateTableView(){
        if(departmentService == null){
            throw new IllegalStateException("Service was null!");
        }
        List<Department> list = departmentService.findAll();
        observableList = FXCollections.observableArrayList(list);
        departmentTableView.setItems(observableList);
        initEditButtons();
        initRemoveButtons();
    }

    private void createDialogForm(Department obj, String absoluteName, Stage parentStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
        Pane pane = loader.load();

        DepartmentFormController controller = loader.getController();
        controller.setDepartment(obj);
        controller.setDepartmentService(new DepartmentService());
        controller.subscribeDataChangeListener(this);
        controller.updateFormData();

        Stage dialogStage = new Stage();
        dialogStage.setTitle("Enter Department data");
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
        tableColumnEDIT.setCellFactory(param -> new TableCell<Department, Department>(){
            private final Button button = new Button("edit");

            @Override
            protected void updateItem(Department item, boolean empty) {
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
                                        "/com/caleb/workshopjavajdbc/DepartmentForm.fxml", Utils.currentStage(event));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
            }
        });
    }

    private void initRemoveButtons(){
        tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
        tableColumnREMOVE.setCellFactory(param -> new TableCell<Department, Department>(){
        private final Button button = new Button("remove");
            @Override
            protected void updateItem(Department item, boolean empty) {
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

    private void removeEntity(Department item) {
       Optional<ButtonType> result = Alerts.showConfirmation("Confirmation", "Are you sure to delete?");
        if(result.get() == ButtonType.OK){
            if(departmentService == null) {
                throw new IllegalStateException("Service was null");
            }
            try {
                departmentService.remove(item);
                updateTableView();
            }
            catch (DbIntegrityException e){
                Alerts.showAlert("Error removing object", null, e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
}
