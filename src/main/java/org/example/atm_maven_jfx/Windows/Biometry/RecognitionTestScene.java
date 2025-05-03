package org.example.atm_maven_jfx.Windows.Biometry;

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
import org.example.atm_maven_jfx.Database.DatabaseService.*;

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

import static org.bytedeco.opencv.global.opencv_imgproc.COLOR_BGR2RGB;
import static org.bytedeco.opencv.global.opencv_imgproc.cvtColor;
import static org.bytedeco.opencv.global.opencv_objdetect.CASCADE_SCALE_IMAGE;

import com.fasterxml.jackson.annotation.JsonProperty;

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
        userInfo.setStyle("-fx-font-size: 32px; -fx-font-weight: bold;");

        imageView = new ImageView();
        imageView.setFitWidth(640);
        imageView.setFitHeight(480);
        imageView.setPreserveRatio(true);

        canvas = new Canvas(640, 480);
        gc = canvas.getGraphicsContext2D();

        StackPane videoPane = new StackPane(imageView, canvas);

        resultLabel = new Label("Результат будет здесь...");
        resultLabel.setWrapText(true);

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

        VBox root = new VBox(20, userInfo, videoPane, sendButton, resultLabel);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #f9f9f9;");

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

// 2. Создаём BufferedImage нужного размера и типа
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

// 3. Копируем пиксели вручную
            PixelReader pixelReader = writableImage.getPixelReader();
            WritableRaster raster = bufferedImage.getRaster();
            DataBufferInt dataBuffer = (DataBufferInt) raster.getDataBuffer();
            int[] pixelData = dataBuffer.getData();

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int argb = pixelReader.getArgb(x, y);
                    int r = (argb >> 16) & 0xFF;
                    int g = (argb >> 8) & 0xFF;
                    int b = argb & 0xFF;
                    pixelData[y * width + x] = (r << 16) | (g << 8) | b;
                }
            }

            // 4. Подготавливаем байты изображения
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", baos);
            byte[] imageBytes = baos.toByteArray();

            // 4. Настраиваем соединение
            HttpURLConnection connection = (HttpURLConnection) new URL(SERVER_URL).openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=----boundary");

            // 5. Отправляем данные
            try (OutputStream os = connection.getOutputStream()) {
                // Записываем multipart заголовок
                String header = """
                        ------boundary\r
                        Content-Disposition: form-data; name="file"; filename="face.jpg"\r
                        Content-Type: image/jpeg\r
                        \r
                        """;
                os.write(header.getBytes(StandardCharsets.UTF_8));

                // Записываем само изображение
                os.write(imageBytes);

                // Записываем конец запроса
                String footer = "\r\n------boundary--\r\n";
                os.write(footer.getBytes(StandardCharsets.UTF_8));
            }

            // 6. Обрабатываем ответ
            int responseCode = connection.getResponseCode();
            System.out.println("[DEBUG] Код ответа сервера: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (InputStream is = connection.getInputStream()) {
                    // Читаем ответ как строку для отладки
                    String responseString = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                    System.out.println("[DEBUG] Ответ сервера: " + responseString);

                    // Парсим JSON
                    ObjectMapper mapper = new ObjectMapper();
                    RecognitionResponse response = mapper.readValue(responseString, RecognitionResponse.class);

                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Подтверждение");
                        alert.setHeaderText("Вы распознаны!");
                        alert.setContentText("Вы действительно хотите продолжить как: " + response.getFullName() + "?");

                        ButtonType yesButton = new ButtonType("Да", ButtonBar.ButtonData.YES);
                        ButtonType noButton = new ButtonType("Нет", ButtonBar.ButtonData.NO);
                        alert.getButtonTypes().setAll(yesButton, noButton);

                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.isPresent() && result.get() == yesButton) {
                            PinCodeForBiometry pinScene = new PinCodeForBiometry(primaryStage, response.getCardNumber());
                            primaryStage.setScene(pinScene.getScene());
                        } else {
                            resultLabel.setText("Подтверждение отменено. Попробуйте снова.");
                        }
                    });
                }
            } else {
                String errorResponse = new String(connection.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
                Platform.runLater(() -> resultLabel.setText("Ошибка сервера: " + errorResponse));
            }
        } catch (Exception e) {
            Platform.runLater(() -> resultLabel.setText("Ошибка: " + e.getMessage()));
            e.printStackTrace();
        }
    }

    private void handleJsonParseError(IOException ex, String responseBody) {
        logger.log(Level.SEVERE, "Ошибка парсинга JSON: " + responseBody, ex);
        resultLabel.setText("Ошибка обработки ответа сервера: " + ex.getMessage());
    }
    public static class RecognitionResponse {
        @JsonProperty("FullName")
        private String fullName;

        @JsonProperty("CardNumber")
        private String cardNumber;

        @JsonProperty("success")
        private boolean success;

        // Геттеры
        public String getFullName() { return fullName; }
        public String getCardNumber() { return cardNumber; }
        public boolean isSuccess() { return success; }
    }


}