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
        root.setStyle("-fx-background-color: #f4f4f4;");

        // Заголовок
        Label titleLabel = new Label("Управление Услугами");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Таблица услуг
        TableView<Service> tableView = new TableView<>();
        tableView.setPrefSize(100, 300);
        tableView.setEditable(true);
        tableView.setMaxWidth(800);
        tableView.columnResizePolicyProperty().set(TableView.UNCONSTRAINED_RESIZE_POLICY);

        // Колонка "Выбрать" для выбора строк
        TableColumn<Service, Boolean> selectColumn = new TableColumn<>("Выбрать");
        selectColumn.setCellValueFactory(cellData ->
                cellData.getValue().selectedProperty()
        );
        selectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectColumn));
        selectColumn.setEditable(true);
        selectColumn.setPrefWidth(100); // Фиксированная ширина

        // Колонка "Название"
        TableColumn<Service, String> nameColumn = new TableColumn<>("Название");
        nameColumn.setCellValueFactory(cellData ->
                cellData.getValue().nameProperty()
        );
        nameColumn.setPrefWidth(100); // Фиксированная ширина

        // Колонка "Статус" с чекбоксами
        TableColumn<Service, Boolean> activeStatusColumn = new TableColumn<>("Статус");
        activeStatusColumn.setCellValueFactory(cellData ->
                cellData.getValue().activeStatusProperty()
        );
        activeStatusColumn.setCellFactory(CheckBoxTableCell.forTableColumn(activeStatusColumn));
        activeStatusColumn.setEditable(true);
        activeStatusColumn.setPrefWidth(100); // Фиксированная ширина

        // Добавляем колонки в таблицу
        tableView.getColumns().addAll(selectColumn, nameColumn, activeStatusColumn);

        // Кнопки
        Button addButton = new Button("Добавить");
        addButton.setOnAction(event -> addService(tableView));

        Button deleteButton = new Button("Удалить");
        deleteButton.setOnAction(event -> deleteSelectedServices(tableView));

        Button saveButton = new Button("Сохранить Конфигурацию");
        saveButton.setOnAction(event -> saveConfiguration());

        HBox buttonBox = new HBox(10, addButton, deleteButton, saveButton);
        buttonBox.setAlignment(Pos.CENTER);

        // Кнопка "Назад"
        Button backButton = new Button("Назад");
        backButton.setOnAction(event -> primaryStage.setScene(previousScene));

        // Загрузка данных из БД
        loadServicesFromDatabase(tableView);

        root.getChildren().addAll(titleLabel, tableView, buttonBox, backButton);
        return new Scene(root, 800, 600);
    }

    private void loadServicesFromDatabase(TableView<Service> tableView) {
        try {
            List<Service> loadedServices = DatabaseService.loadServices();
            services.clear();
            services.addAll(loadedServices);
            tableView.setItems(null); // Очищаем данные
            tableView.setItems(services); // Устанавливаем новые данные
            tableView.refresh(); // Обновляем таблицу

            // Отладочная информация
            System.out.println("Загружено услуг: " + services.size());
            for (Service service : services) {
                System.out.println("ID=" + service.getId() + ", Name=" + service.getName() + ", ActiveStatus=" + service.isActiveStatus());
            }
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
            if (service.isSelected()) { // Используем метод isSelected()
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

    private void saveConfiguration() {
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
        showSuccessAlert();
        System.out.println("Конфигурация успешно сохранена.");
    }

    private void showErrorAlert(String message) {
        System.out.println("Показываем ошибку: " + "Ошибка" + " - " + message);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    private void showSuccessAlert() {
        System.out.println("Показываем успех: " + "Успех" + " - " + "Конфигурация успешно сохранена.");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Успех");
        alert.setHeaderText("Конфигурация успешно сохранена.");
        alert.showAndWait();
    }

    public Scene getScene() {
        return scene;
    }

    public static class Service {
        private final long id;
        private final StringProperty name;
        private final BooleanProperty activeStatus;
        private final BooleanProperty selected; // Поле для отслеживания состояния выбора

        public Service(long id, String name, boolean activeStatus) {
            this.id = id;
            this.name = new SimpleStringProperty(name);
            this.activeStatus = new SimpleBooleanProperty(activeStatus);
            this.selected = new SimpleBooleanProperty(false); // По умолчанию услуга не выбрана
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

        public boolean isActiveStatus() {
            return activeStatus.get();
        }

        public BooleanProperty activeStatusProperty() {
            return activeStatus;
        }

        public boolean isSelected() {
            return selected.get();
        }

        public BooleanProperty selectedProperty() {
            return selected;
        }

    }
}