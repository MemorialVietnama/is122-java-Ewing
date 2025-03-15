module org.example.atm_jfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires org.firebirdsql.jaybird;

    // Открываем пакет для javafx.base
    opens org.example.atm_jfx.AdminSrc.Windows.Incossations to javafx.base;
    opens org.example.atm_jfx to javafx.fxml;
    exports org.example.atm_jfx;
}