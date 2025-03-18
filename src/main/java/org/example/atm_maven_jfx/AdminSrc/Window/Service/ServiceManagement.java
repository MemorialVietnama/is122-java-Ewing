package org.example.atm_maven_jfx.AdminSrc.Window.Service;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.atm_maven_jfx.Database.DatabaseService;

import java.sql.SQLException;
import java.util.List;

public class ServiceManagement {
    private final Scene scene;
    private final ObservableList<Service> services = FXCollections.observableArrayList();

    public ServiceManagement(Stage primaryStage, Scene previousScene) {
        this.scene = createScene(primaryStage, previousScene);
    }

    private Scene createScene(Stage primaryStage, Scene previousScene) {
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setTranslateX(-500);
        root.setTranslateY(-500);
        root.setStyle("-fx-background-color: #f4f4f4;");

        // Заголовок
        Label titleLabel = new Label("Управление Услугами");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Таблица услуг
        TableView<Service> tableView = new TableView<>();
        tableView.setPrefSize(500, 300); // Фиксированный размер таблицы
        tableView.setMaxWidth(500);
        tableView.setEditable(true);

        // Колонка Название Услуги
        TableColumn<Service, String> nameColumn = new TableColumn<>("Название");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setPrefWidth(300);

        TableColumn<Service, Boolean> activeStatusColumn = new TableColumn<>("Статус");
        activeStatusColumn.setCellValueFactory(cellData -> cellData.getValue().activeStatusProperty());
        activeStatusColumn.setCellFactory(CheckBoxTableCell.forTableColumn(activeStatusColumn));
        activeStatusColumn.setPrefWidth(200);

        activeStatusColumn.setOnEditCommit(event -> {
            Service service = event.getRowValue();
            boolean newActiveStatus = event.getNewValue();
            service.setActiveStatus(newActiveStatus); // Обновляем состояние в модели
        });

        // Добавляем колонки в таблицу
        tableView.getColumns().addAll(nameColumn, activeStatusColumn);

        // Кнопки Добавить, Удалить и Сохранить
        Button addButton = new Button("Добавить");
        addButton.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-size: 14px; -fx-pref-width: 100px;");
        addButton.setOnAction(event -> addService(tableView));

        Button deleteButton = new Button("Удалить");
        deleteButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-size: 14px; -fx-pref-width: 100px;");
        deleteButton.setOnAction(event -> deleteSelectedServices(tableView));

        Button saveButton = new Button("Сохранить Конфигурацию");
        saveButton.setStyle("-fx-background-color: blue; -fx-text-fill: white; -fx-font-size: 14px; -fx-pref-width: 200px;");
        saveButton.setOnAction(event -> saveConfiguration(tableView));

        HBox buttonBox = new HBox(10, addButton, deleteButton, saveButton);
        buttonBox.setAlignment(Pos.CENTER);

        // Кнопка Назад
        Button backButton = new Button("Назад");
        backButton.setStyle("-fx-background-color: gray; -fx-text-fill: white; -fx-font-size: 14px; -fx-pref-width: 100px;");
        backButton.setOnAction(event -> primaryStage.setScene(previousScene));

        // Загрузка данных из БД
        loadServicesFromDatabase(tableView);

        root.getChildren().addAll(titleLabel, tableView, buttonBox, backButton);

        return new Scene(root, 800, 600); // Размер окна
    }

    private void loadServicesFromDatabase(TableView<Service> tableView) {
        try {
            List<Service> loadedServices = DatabaseService.loadServices();
            services.clear();
            services.addAll(loadedServices);
            tableView.setItems(null); // Очищаем данные
            tableView.setItems(services); // Устанавливаем новые данные
            tableView.refresh(); // Обновляем таблицу
        } catch (SQLException e) {
            System.out.println("Ошибка при загрузке услуг из базы данных: " + e.getMessage());
            showErrorAlert("Не удалось загрузить услуги из базы данных.");
        }
    }

    private void addService(TableView<Service> tableView) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Добавить Услугу");
        dialog.setHeaderText("Введите название новой услуги:");
        dialog.setContentText("Название:");

        dialog.showAndWait().ifPresent(name -> {
            if (!name.trim().isEmpty()) {
                try {
                    System.out.println("Проверка существования услуги с названием: " + name);
                    if (DatabaseService.isServiceExists(name)) {
                        System.out.println("Услуга с названием '" + name + "' уже существует.");
                        showErrorAlert("Услуга с таким названием уже существует.");
                        return;
                    }

                    if (DatabaseService.addService(name)) {
                        long id = DatabaseService.getLastInsertedId(); // Получаем ID новой услуги
                        System.out.println("Получен ID новой услуги: " + id);
                        Service newService = new Service(id, name, true); // По умолчанию активна
                        services.add(newService);
                        tableView.refresh();
                        System.out.println("Услуга успешно добавлена: ID=" + id + ", Name=" + name);
                    }
                } catch (SQLException e) {
                    System.out.println("Ошибка при добавлении услуги: " + e.getMessage());
                    showErrorAlert("Не удалось добавить услугу.");
                }
            }
        });
    }

    private void deleteSelectedServices(TableView<Service> tableView) {
        ObservableList<Service> selectedServices = FXCollections.observableArrayList();
        for (Service service : services) {
            if (service.isSelected()) {
                selectedServices.add(service);
            }
        }

        if (!selectedServices.isEmpty()) {
            System.out.println("Подтверждение удаления выбранных услуг...");
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Подтверждение удаления");
            alert.setHeaderText("Вы уверены, что хотите удалить выбранные услуги?");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    for (Service service : selectedServices) {
                        try {
                            System.out.println("Удаление услуги: ID=" + service.getId() + ", Name=" + service.getName());
                            if (DatabaseService.deleteService(service.getName())) {
                                services.remove(service);
                                System.out.println("Услуга успешно удалена: ID=" + service.getId());
                            }
                        } catch (SQLException e) {
                            System.out.println("Ошибка при удалении услуги: " + e.getMessage());
                            showErrorAlert("Не удалось удалить услугу.");
                        }
                    }
                    tableView.refresh();
                    System.out.println("Удаление завершено.");
                }
            });
        }
    }

    private void saveConfiguration(TableView<Service> tableView) {
        System.out.println("Сохранение конфигурации...");
        for (Service service : services) {
            try {
                System.out.println("Обновление статуса услуги: ID=" + service.getId() +
                        ", Name=" + service.getName() +
                        ", ActiveStatus=" + service.isActiveStatus());
                if (!DatabaseService.updateServiceStatus(service.getId(), service.isActiveStatus())) {
                    System.out.println("Ошибка при обновлении статуса услуги: ID=" + service.getId());
                    showErrorAlert("Не удалось сохранить конфигурацию для услуги: " + service.getName());
                }
            } catch (SQLException e) {
                System.out.println("Ошибка при сохранении конфигурации: " + e.getMessage());
                showErrorAlert("Не удалось сохранить конфигурацию.");
            }
        }
        showSuccessAlert("Конфигурация успешно сохранена.");
        System.out.println("Конфигурация успешно сохранена.");
    }

    private void showErrorAlert(String message) {
        System.out.println("Показываем ошибку: " + "Ошибка" + " - " + message);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    private void showSuccessAlert(String message) {
        System.out.println("Показываем успех: " + "Успех" + " - " + message);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Успех");
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    public Scene getScene() {
        return scene;
    }

    public static class Service {
        private final long id;
        private final StringProperty name; // Используем StringProperty для имени
        private final BooleanProperty activeStatus; // Используем BooleanProperty для статуса
        private boolean selected; // Поле для отслеживания состояния выбора

        public Service(long id, String name, boolean activeStatus) {
            this.id = id;
            this.name = new SimpleStringProperty(name);
            this.activeStatus = new SimpleBooleanProperty(activeStatus);
            this.selected = false; // По умолчанию услуга не выбрана
        }

        public long getId() {
            return id;
        }

        public String getName() {
            return name.get();
        }

        public StringProperty nameProperty() {
            return name;
        }

        public void setName(String name) {
            this.name.set(name);
        }

        public boolean isActiveStatus() {
            return activeStatus.get();
        }

        public BooleanProperty activeStatusProperty() {
            return activeStatus;
        }

        public void setActiveStatus(boolean activeStatus) {
            this.activeStatus.set(activeStatus);
        }

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }
    }
}