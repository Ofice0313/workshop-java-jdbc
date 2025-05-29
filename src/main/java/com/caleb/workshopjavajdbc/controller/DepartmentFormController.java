package com.caleb.workshopjavajdbc.controller;

import com.caleb.workshopjavajdbc.db.DbException;
import com.caleb.workshopjavajdbc.gui.listeners.DataChangeListener;
import com.caleb.workshopjavajdbc.gui.util.Alerts;
import com.caleb.workshopjavajdbc.gui.util.Constraints;
import com.caleb.workshopjavajdbc.gui.util.Utils;
import com.caleb.workshopjavajdbc.model.entities.Department;
import com.caleb.workshopjavajdbc.model.exceptions.ValidationException;
import com.caleb.workshopjavajdbc.model.services.DepartmentService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.*;

public class DepartmentFormController implements Initializable {

    private Department entity;

    private DepartmentService departmentService;

    private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

    @FXML
    private TextField txtId;
    @FXML
    private TextField txtName;
    @FXML
    private Label labelErrorName;
    @FXML
    private Button btSave;
    @FXML
    private Button btCancel;

    public void setDepartment(Department entity){
        this.entity = entity;
    }

    public void setDepartmentService(DepartmentService service){
        this.departmentService = service;
    }

    public void subscribeDataChangeListener(DataChangeListener listener){
        dataChangeListeners.add(listener);
    }

    @FXML
    public void onBtSaveAction(ActionEvent event){
        if(entity == null){
            throw new IllegalStateException("Entity was null!");
        }
        if(departmentService == null){
            throw new IllegalStateException("Service was null!");
        }
        try {
            entity = getFormData();
            departmentService.saveOrUpdate(entity);
            notifyDataChangeListeners();
            Utils.currentStage(event).close();
        }
        catch (ValidationException e){
            setErrorMessages(e.getErrors());
        }
        catch (DbException e){
            Alerts.showAlert("Error saving object", null, e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void notifyDataChangeListeners() {
        for(DataChangeListener listener: dataChangeListeners){
            listener.onDataChanged();
        }
    }

    private Department getFormData() {
        Department obj = new Department();

        ValidationException exception = new ValidationException("Validation error");

        obj.setId(Utils.tryParseToInt(txtId.getText()));

        if(txtName.getText() == null || txtName.getText().trim().equals("")){
            exception.addError("name", "Field can't be empty");
        }
        obj.setName(txtName.getText());

        if(exception.getErrors().size() > 0){
            throw exception;
        }

        return obj;
    }

    @FXML
    public void onBtCancelAction(ActionEvent event){
        Utils.currentStage(event).close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    private void initializeNodes(){
        Constraints.setTextFieldInteger(txtId);
        Constraints.setTextFieldMaxLength(txtName, 30);
    }

    public void updateFormData(){
        if(entity == null){
           throw new IllegalStateException("entity was null!");
        }
        txtId.setText(String.valueOf(entity.getId()));
        txtName.setText(entity.getName());
    }

    private void setErrorMessages(Map<String, String> errors){
        Set<String> fields = errors.keySet();

        if(fields.contains("name")){
            labelErrorName.setText(errors.get("name"));
            labelErrorName.setVisible(true);
        }
    }
}
