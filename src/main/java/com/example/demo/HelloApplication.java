package com.example.demo;


import com.example.demo.connection.PositionDao;
import com.example.demo.connection.EmployeeDao;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class HelloApplication extends Application {

    private Stage primaryStage;
    private EmployeeDao employeeDao;
    private PositionDao positionDao;
    //private SalaryDao salaryDao;
    private TableView<Employee> employeeTable = new TableView<>();

    private TextField firstNameField = new TextField();
    private TextField lastNameField = new TextField();

    private TableView<Position> positionTable = new TableView<>();
    private TextField positionNameField = new TextField();

    private TextField salaryField = new TextField();

    private ComboBox<Employee> employeeListView = new ComboBox<>();
    private ComboBox<Position> positionListView = new ComboBox<>();

    ComboBox<String> positionComboBox = new ComboBox<>();

/*

    private TableView<Salary> salaryTableView = new TableView<>();*/


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Employees");

        this.employeeDao = new EmployeeDao();
        this.positionDao = new PositionDao();
        //this.salaryDao = new SalaryDao();
        // Set up home page
        VBox homePage = createEmployeePage();
         // Set up scene and show the stage
        Scene scene = new Scene(homePage);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void navigateToAnotherPage(String page) {
        VBox anotherPage = null;

        switch (page){
            case "employee":{
                anotherPage = createEmployeePage();
                break;
            }
            case "position":{
                anotherPage = createAnotherPage();
                break;
            }
            /*case "salary":{
                anotherPage = createSalaryPage();
                break;
            }*/

        }
        Scene anotherScene = new Scene(anotherPage);
        primaryStage.setScene(anotherScene);
    }


    private VBox createEmployeePage(){
;
        salaryField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                salaryField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        TableColumn<Employee, Integer> idcolumn = new TableColumn<>("id");
        idcolumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        List<Position> positions = positionDao.fetchAll();

        List<String> positionNames = positions.stream()
                .map(Position::getName)
                .collect(Collectors.toList());

        positionComboBox.getItems().clear();
        positionComboBox.getItems().addAll(positionNames);

        positionComboBox.setOnAction(event -> {
            Employee selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();
            String selectedPosition = positionComboBox.getValue();
            if (selectedEmployee != null && selectedPosition != null) {
                Position position = positionDao.findPositionByName(selectedPosition);
                if (position != null) {
                    selectedEmployee.setPosition(position);
                }
            }
        });


        TableColumn<Employee, String> firstNameColumn = new TableColumn<>("First Name");
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<Employee, String> lastNameColumn = new TableColumn<>("Last Name");
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<Employee, Double> salaryColumn = new TableColumn<>("salary");
        salaryColumn.setCellValueFactory(new PropertyValueFactory<>("salary"));

        TableColumn<Employee, String> positionColumn = new TableColumn<>("position");
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));

        if(employeeTable.getColumns().isEmpty()){
            employeeTable.getColumns().addAll(idcolumn, firstNameColumn, lastNameColumn, salaryColumn, positionColumn);
        }

        employeeTable.setItems(FXCollections.observableArrayList(employeeDao.fetchAll()));
        employeeTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Double-click
                Employee selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();
                if (selectedEmployee != null) {
                    firstNameField.setText(selectedEmployee.getFirstName());
                    lastNameField.setText(selectedEmployee.getLastName());
                    salaryField.setText(Double.toString(selectedEmployee.getSalary()));

                    String selectedPosition = positionComboBox.getValue();
                    System.out.println(selectedPosition);
                    if (selectedPosition != null) {
                        Position position = positionDao.findPositionByName(selectedPosition);
                        if (position != null) {
                            selectedEmployee.setPosition(position);
                        }
                    }
                }
            }
        });

        // Set up form fields
        GridPane formPane = new GridPane();
        formPane.setHgap(10);
        formPane.setVgap(10);
        formPane.setPadding(new Insets(10));
        formPane.add(new Label("First Name:"), 0, 0);
        formPane.add(firstNameField, 1, 0);
        formPane.add(new Label("Last Name:"), 0, 1);
        formPane.add(lastNameField, 1, 1);
        formPane.add(new Label("Salary:"), 0, 2);
        formPane.add(salaryField, 1, 2);
        formPane.add(new Label("Position:"), 0, 3);
        formPane.add(positionComboBox, 1, 3);
        formPane.setStyle("-fx-padding: 10px;");


        // Set up buttons
        Button addButton = new Button("Add");
        addButton.setStyle("-fx-background-color: #c9b5a4; -fx-text-fill: #ffffff; -fx-padding: 8px 12px; -fx-font-size: 14px; -fx-border-color: none; -fx-cursor: pointer;");
        addButton.setOnAction(e -> addEmployee());
        Button updateButton = new Button("Update");
        updateButton.setOnAction(e -> updateEmployee());
        updateButton.setStyle("-fx-background-color: #c9b5a4; -fx-text-fill: #ffffff; -fx-padding: 8px 12px; -fx-font-size: 14px; -fx-border-color: none; -fx-cursor: pointer;");

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> deleteEmployee());
        deleteButton.setStyle("-fx-background-color: #c9b5a4; -fx-text-fill: #ffffff; -fx-padding: 8px 12px; -fx-font-size: 14px; -fx-border-color: none; -fx-cursor: pointer;");

        HBox buttonPane = new HBox(10);
        buttonPane.setPadding(new Insets(10));
        buttonPane.getChildren().addAll(addButton, updateButton, deleteButton);

        firstNameField.setStyle("-fx-background-color: #ffffff; -fx-padding: 5px; -fx-font-size: 14px;");
        lastNameField.setStyle("-fx-background-color: #ffffff; -fx-padding: 5px; -fx-font-size: 14px;");
        salaryField.setStyle("-fx-background-color: #ffffff; -fx-padding: 5px; -fx-font-size: 14px;");

        positionComboBox.setStyle("-fx-background-color: #ffffff; -fx-padding: 5px; -fx-font-size: 14px;");
        Button navigateDi = new Button("positions");
        navigateDi.setOnAction(e -> navigateToAnotherPage("position"));

        HBox nav = new HBox(10);
        nav.setPadding(new Insets(10));
        nav.getChildren().addAll(navigateDi);

        // Set up root layout
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        root.getChildren().addAll(nav, formPane, buttonPane, employeeTable);
        return root;
    }


    private VBox createAnotherPage() {
        TableColumn<Position, Integer> idcolumn = new TableColumn<>("id");
        idcolumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Position, String> nameColumn = new TableColumn<>("name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        if(positionTable.getColumns().isEmpty()){
            positionTable.getColumns().addAll(idcolumn, nameColumn);
        }

        positionTable.setItems(FXCollections.observableArrayList(positionDao.fetchAll()));
        positionTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Double-click
                Position selectedItem =  positionTable.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    positionNameField.setText(selectedItem.getName());
                }
            }
        });

        // Set up form fields
        GridPane formPane = new GridPane();
        formPane.setHgap(10);
        formPane.setVgap(10);
        formPane.setPadding(new Insets(10));
        formPane.add(new Label("name:"), 0, 0);
        formPane.add(positionNameField, 1, 0);


        // Set up buttons
        Button addButton = new Button("Add");
        addButton.setStyle("-fx-background-color: #c9b5a4; -fx-text-fill: #ffffff; -fx-padding: 8px 12px; -fx-font-size: 14px; -fx-border-color: none; -fx-cursor: pointer;");

        addButton.setOnAction(e -> addPosition());
        Button updateButton = new Button("Update");
        updateButton.setStyle("-fx-background-color: #c9b5a4; -fx-text-fill: #ffffff; -fx-padding: 8px 12px; -fx-font-size: 14px; -fx-border-color: none; -fx-cursor: pointer;");

        updateButton.setOnAction(e -> updatePosition());
        Button deleteButton = new Button("Delete");
        deleteButton.setStyle("-fx-background-color: #c9b5a4; -fx-text-fill: #ffffff; -fx-padding: 8px 12px; -fx-font-size: 14px; -fx-border-color: none; -fx-cursor: pointer;");

        deleteButton.setOnAction(e -> deletePosition());

        HBox buttonPane = new HBox(10);
        buttonPane.setPadding(new Insets(10));
        buttonPane.getChildren().addAll(addButton, updateButton, deleteButton);



        Button navigateT = new Button("employees");
        navigateT.setOnAction(e -> navigateToAnotherPage("employee"));

        HBox nav = new HBox(10);
        nav.setPadding(new Insets(10));
        nav.getChildren().addAll(navigateT);

        // Set up root layout
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        root.getChildren().addAll(nav, formPane, buttonPane, positionTable);
        return root;
    }



    private void deletePosition() {
        Position selected = positionTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete position");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to delete the selected position?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                positionDao.delete(selected.getId());
               positionTable.setItems(FXCollections.observableArrayList(positionDao.fetchAll()));

            }
        }
    }

    private void updatePosition() {
        Position selected = positionTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Position d = new Position();
            d.setName(positionNameField.getText());
            d.setId(selected.getId());
            positionDao.update(d);
            positionTable.setItems(FXCollections.observableArrayList(positionDao.fetchAll()));
            clearField();
        }
    }

    private void addPosition() {
        String name = positionNameField.getText();
        Position d = new Position(name);
        positionDao.insert(d);
        positionTable.setItems(FXCollections.observableList(positionDao.fetchAll()));
        clearField();
    }


    private void addEmployee() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        Double salary = Double.parseDouble(salaryField.getText());
        String selectedPosition = positionComboBox.getValue();

        Position position = positionDao.findPositionByName(selectedPosition);
        int positionId = position.getId();
        Employee employee = new Employee(firstName, lastName,  position,salary);

        employeeDao.insert(employee);
        employeeTable.setItems(FXCollections.observableArrayList(employeeDao.fetchAll()));

        clearFields();
    }

    private void updateEmployee() {
        Employee selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();
        if (selectedEmployee != null) {
            Employee t = new Employee();
            t.setFirstName(firstNameField.getText());
            t.setLastName(lastNameField.getText());
            t.setSalary(Double.parseDouble(salaryField.getText()));
            String selectedPosition = positionComboBox.getValue();
            if (selectedPosition != null) {
                // Получение объекта Position по имени
                Position position = positionDao.findPositionByName(selectedPosition);
                if (position != null) {
                    t.setPosition(position);
                }
            }
            t.setId(selectedEmployee.getId());
            employeeDao.update(t);
            employeeTable.setItems(FXCollections.observableArrayList(employeeDao.fetchAll()));
            clearFields();
        }
    }

    private void deleteEmployee() {
        Employee selectedEmployee = employeeTable.getSelectionModel().getSelectedItem();
        if (selectedEmployee != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete employee");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to delete the selected teacher?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                employeeDao.delete(selectedEmployee.getId());
                employeeTable.setItems(FXCollections.observableArrayList(employeeDao.fetchAll()));

            }
        }
    }

    private void clearFields() {
        firstNameField.clear();
        lastNameField.clear();
        salaryField.clear();
    }
    private void clearField(){
        positionNameField.clear();
    }
}