package org.example.atm_maven_jfx.Windows.Biometry;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.RectVector;
import org.bytedeco.opencv.opencv_core.Size;
import org.bytedeco.opencv.opencv_objdetect.CascadeClassifier;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR2RGB;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;
import static org.bytedeco.opencv.global.opencv_objdetect.CASCADE_SCALE_IMAGE;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.atm_maven_jfx.Windows.BlockMenu.BlockWindow;

public class RecognitionTestScene {
    private final Stage primaryStage;
    private static final Logger logger = Logger.getLogger(RecognitionTestScene.class.getName());
    private static final String SERVER_URL = "http://127.0.0.1:8080/recognize";
    private VideoCapture capture;
    private ImageView imageView;
    private Canvas canvas;
    private GraphicsContext gc;
    private Label resultLabel;
    private final RectVector faces = new RectVector();
    private CascadeClassifier faceDetector;

    public RecognitionTestScene(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Scene createScene() throws Exception {
        String cascadePath = "/org/example/atm_maven_jfx/openCV/haarcascade_frontalface_default.xml";
        try (InputStream is = getClass().getResourceAsStream(cascadePath)) {
            if (is == null) {
                throw new RuntimeException("Файл каскада не найден по пути: " + cascadePath);
            }
            Path tempFile = Files.createTempFile("haarcascade_", ".xml");
            Files.copy(is, tempFile, StandardCopyOption.REPLACE_EXISTING);
            faceDetector = new CascadeClassifier(tempFile.toString());
            if (faceDetector.empty()) {
                throw new RuntimeException("Не удалось загрузить каскад по пути: " + tempFile);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Ошибка при загрузке каскада", e);
            throw e;
        }

        Label userInfo = new Label("Посмотрите в камеру, пока не появится квадрат, а после нажмите \"Обработать\"");
        userInfo.setWrapText(true);
        userInfo.setStyle("""
        -fx-font-size: 32px;
        -fx-font-weight: bold;
        -fx-text-fill: white;
        -fx-background-color: rgba(0,0,0,0.5);
        -fx-padding: 15 20;
        -fx-background-radius: 10;
        -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);
    """);

        imageView = new ImageView();
        imageView.setFitWidth(640);
        imageView.setFitHeight(480);
        imageView.setPreserveRatio(true);
        imageView.setStyle("-fx-border-color: white; -fx-border-width: 3px;");

        canvas = new Canvas(640, 480);
        gc = canvas.getGraphicsContext2D();

        StackPane videoPane = new StackPane(imageView, canvas);
        videoPane.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0, 0, 5);");

        resultLabel = new Label("Результат будет здесь...");
        resultLabel.setWrapText(true);
        resultLabel.setStyle("""
        -fx-font-size: 20px;
        -fx-text-fill: red;
        -fx-background-color: rgba(255,255,255,0.1);
        -fx-padding: 15 20;
        -fx-background-radius: 8;
        -fx-max-width: 600px;
    """);

        Button sendButton = new Button("Отправить запрос на /recognize");
        sendButton.setStyle("-fx-font-size: 16px; -fx-padding: 10 20; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        sendButton.setOnAction(_ -> {
            if (faces.size() > 0) {
                try {
                    sendPostRequest(resultLabel);
                } catch (Exception ex) {
                    logger.log(Level.SEVERE, "Ошибка при отправке POST-запроса", ex);
                    resultLabel.setText("Ошибка: " + ex.getMessage());
                }
            } else {
                resultLabel.setText("Ошибка: Лицо не обнаружено. Подождите, пока появится квадрат.");
            }
        });
        Button backButton = new Button("Назад");
        backButton.setStyle("""
        -fx-font-size: 16px;
        -fx-padding: 10 20;
        -fx-background-color: #f44336;
        -fx-text-fill: white;
        -fx-cursor: hand;
        """);
        backButton.setOnAction(_ -> {
            // Останавливаем камеру
            if (capture != null && capture.isOpened()) {
                capture.release();
            }

            // Переключаемся обратно на BlockWindow
            BlockWindow.showWithPreloader(primaryStage);
        });

        VBox root = new VBox(20, userInfo, videoPane, sendButton, resultLabel, backButton);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: red;");

        startCamera();
        return new Scene(root, 1920, 1080);
    }

    private void startCamera() {
        capture = new VideoCapture();
        if (capture.open(0)) {
            new AnimationTimer() {
                long lastUpdate = 0;

                @Override
                public void handle(long now) {
                    if (now - lastUpdate >= 33_000_000) {
                        updateCameraFrame();
                        lastUpdate = now;
                    }
                }
            }.start();
        } else {
            logger.severe("Не удалось открыть камеру.");
        }
    }

    private void updateCameraFrame() {
        Mat frame = new Mat();
        if (capture.read(frame)) {
            try {
                detectFace(frame);
                Image image = matToJavaFXImage(frame);
                imageView.setImage(image);
                drawFaceRectangle();
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Ошибка преобразования кадра", e);
            }
        }
    }

    private void detectFace(Mat frame) {
        Mat gray = new Mat();
        cvtColor(frame, gray, opencv_imgproc.COLOR_BGR2GRAY);
        opencv_imgproc.equalizeHist(gray, gray);
        faceDetector.detectMultiScale(
                gray,
                faces,
                1.1,
                3,
                CASCADE_SCALE_IMAGE,
                new Size(),
                new Size()
        );
    }

    private void drawFaceRectangle() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        for (int i = 0; i < faces.size(); i++) {
            Rect face = faces.get(i);
            gc.setStroke(Color.DARKRED);
            gc.setLineWidth(10);
            gc.strokeRect(face.x(), face.y(), face.width(), face.height());
        }
    }

    private Image matToJavaFXImage(Mat originalMat) {
        Mat rgbMat = new Mat();
        cvtColor(originalMat, rgbMat, COLOR_BGR2RGB);
        int width = rgbMat.cols();
        int height = rgbMat.rows();
        byte[] data = new byte[width * height * 3];
        rgbMat.data().get(data);
        ByteBuffer buffer = ByteBuffer.wrap(data);
        WritableImage image = new WritableImage(width, height);
        PixelWriter writer = image.getPixelWriter();
        writer.setPixels(0, 0, width, height, PixelFormat.getByteRgbInstance(), buffer, width * 3);
        return image;
    }

    private void sendPostRequest(Label resultLabel) {
        System.out.println("[DEBUG] Начало подготовки изображения для отправки");

        try {
            // 1. Получаем изображение из ImageView
            WritableImage writableImage = imageView.snapshot(null, null);
            int width = (int) writableImage.getWidth();
            int height = (int) writableImage.getHeight();

            // 2. Создаём BufferedImage
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            WritableRaster raster = bufferedImage.getRaster();
            DataBufferInt dataBuffer = (DataBufferInt) raster.getDataBuffer();
            int[] pixelData = dataBuffer.getData();

            // 3. Копируем пиксели
            PixelReader pixelReader = writableImage.getPixelReader();
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int argb = pixelReader.getArgb(x, y);
                    pixelData[y * width + x] = argb & 0x00FFFFFF; // Игнорируем альфа-канал
                }
            }

            // 4. Подготавливаем байты изображения
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", baos);
            byte[] imageBytes = baos.toByteArray();

            // 5. Настраиваем соединение
            HttpURLConnection connection = (HttpURLConnection) new URL(SERVER_URL).openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=----boundary");

            // 6. Отправляем данные
            try (OutputStream os = connection.getOutputStream()) {
                String header = """
                ------boundary\r
                Content-Disposition: form-data; name="file"; filename="face.jpg"\r
                Content-Type: image/jpeg\r
                \r
                """;
                os.write(header.getBytes(StandardCharsets.UTF_8));
                os.write(imageBytes);
                os.write("\r\n------boundary--\r\n".getBytes(StandardCharsets.UTF_8));
            }

            // 7. Обрабатываем ответ
            int responseCode = connection.getResponseCode();
            System.out.println("[DEBUG] Код ответа сервера: " + responseCode);

            try (InputStream is = responseCode == 200 ? connection.getInputStream() : connection.getErrorStream()) {
                String responseString = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                System.out.println("[DEBUG] Ответ сервера: " + responseString);

                ObjectMapper mapper = new ObjectMapper();

                Platform.runLater(() -> {
                    try {
                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            RecognitionResponse response = mapper.readValue(responseString, RecognitionResponse.class);

                            if (response.isSuccess()) {
                                handleSuccessResponse(response);
                            } else {
                                resultLabel.setText("Распознавание не удалось");
                            }
                        } else {
                            JsonNode errorNode = mapper.readTree(responseString);
                            String errorMsg = errorNode.path("error").asText("Неизвестная ошибка");
                            String details = errorNode.path("details").asText("");
                            resultLabel.setText("Ошибка: " + errorMsg + "\n" + details);
                        }
                    } catch (IOException ex) {
                        handleJsonParseError(ex, responseString);
                    }
                });
            }
        } catch (Exception e) {
            Platform.runLater(() -> resultLabel.setText("Ошибка: " + getRootCauseMessage(e)));
            logger.log(Level.SEVERE, "Ошибка при отправке запроса", e);
        }
    }

    private void handleSuccessResponse(RecognitionResponse response) {
        String resultsInfo = response.getResults().stream()
                .map(r -> String.format("Карта: %s | Совпадение: %s | Уверенность: %.2f%%",
                        r.getCardClass(),
                        r.isMatched() ? "Да" : "Нет",
                        r.getConfidence()))
                .collect(Collectors.joining("\n"));

        String confirmationText = String.format(
                """
                        Лучшее совпадение:
                        ФИО: %s
                        Номер карты: %s
                        Точность: %.2f%%
                        
                        Все результаты:
                        %s""",
                response.getBestMatch().getFullName(),
                response.getClientInfo().getCardNumber(), // Исправлено здесь
                response.getBestMatch().getConfidence(),
                resultsInfo
        );


        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение личности");
        alert.setHeaderText("Успешное распознавание!");
        alert.setContentText(confirmationText);


        ButtonType yesButton = new ButtonType("Подтвердить", ButtonBar.ButtonData.YES);
        
        ButtonType noButton = new ButtonType("Отмена", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(yesButton, noButton);

        alert.getDialogPane().setStyle("-fx-font-size: 14px;");
        alert.getDialogPane().lookupButton(yesButton).setStyle("-fx-font-weight: bold;");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == yesButton) {
            PinCodeForBiometry pinScene = new PinCodeForBiometry(
                    primaryStage,
                    response.getClientInfo().getCardNumber() // Исправлено здесь
            );
            primaryStage.setScene(pinScene.getScene());
        } else {
            resultLabel.setText("Действие отменено пользователем");
        }
    }

    private String getRootCauseMessage(Throwable e) {
        while (e.getCause() != null) e = e.getCause();
        return e.getMessage() != null ? e.getMessage() : "Unknown error";
    }

    private void handleJsonParseError(IOException ex, String responseBody) {
        String errorMsg = String.format("Ошибка парсинка JSON: %s\nОтвет сервера: %s",
                ex.getMessage(),
                responseBody);
        logger.log(Level.SEVERE, errorMsg);
        Platform.runLater(() ->
                resultLabel.setText("Ошибка обработки ответа сервера. Проверьте логи."));
    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RecognitionResponse {
        @JsonProperty("success")
        private boolean success;

        @JsonProperty("best_match")
        private BestMatch bestMatch;

        @JsonProperty("client_info")
        private ClientInfo clientInfo;

        @JsonProperty("results")
        private List<RecognitionResult> results;

        // Геттеры
        public boolean isSuccess() { return success; }
        public BestMatch getBestMatch() { return bestMatch; }
        public ClientInfo getClientInfo() { return clientInfo; }
        public List<RecognitionResult> getResults() { return results; }
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class BestMatch {

            @JsonProperty("confidence")
            private double confidence;

            @JsonProperty("full_name")
            private String fullName;

            @JsonProperty("card_number")
            private String cardNumber;

            public double getConfidence() { return confidence; }

            public String getFullName() { return fullName; }
            public String getCardNumber() { return cardNumber; }
        }
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class ClientInfo {

            @JsonProperty("card_number")
            private String cardNumber;

            @JsonProperty("balance")
            private double balance;

            public String getCardNumber() { return cardNumber; }
            public double getBalance() { return balance; }
        }
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class RecognitionResult {
            @JsonProperty("class")
            private String cardClass;

            @JsonProperty("confidence")
            private double confidence;

            @JsonProperty("matched")
            private boolean matched;

            // Геттеры
            public String getCardClass() { return cardClass; }
            public double getConfidence() { return confidence; }

            public boolean isMatched() { return matched; }
        }
    }
}