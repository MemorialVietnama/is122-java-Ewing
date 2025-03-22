module org.example.atm_maven_jfx {
    requires javafx.controls;
    requires javafx.fxml;

    requires net.synedra.validatorfx;
    requires java.sql;

    opens org.example.atm_maven_jfx to javafx.fxml, javafx.base;
    opens org.example.atm_maven_jfx.AdminSrc.Window.Service to javafx.fxml, javafx.base;
    exports org.example.atm_maven_jfx;
}