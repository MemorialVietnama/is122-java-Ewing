module org.example.atm_maven_jfx {
    requires javafx.controls;
    requires javafx.fxml;

    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens org.example.atm_maven_jfx to javafx.fxml;
    opens org.example.atm_maven_jfx.AdminSrc.Window.Service to javafx.base;
    exports org.example.atm_maven_jfx;
}