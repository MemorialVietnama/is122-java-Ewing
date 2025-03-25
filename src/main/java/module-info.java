module org.example.atm_maven_jfx {
    requires javafx.controls;
    requires javafx.fxml;

    requires net.synedra.validatorfx;
    requires junit;
    requires org.firebirdsql.jaybird;

    opens org.example.atm_maven_jfx to javafx.fxml, javafx.base;
    opens org.example.atm_maven_jfx.AdminSrc.Window.Service to javafx.fxml, javafx.base;
    exports org.example.atm_maven_jfx;
    exports org.example.atm_maven_jfx.Database;
    exports org.example.atm_maven_jfx.AdminSrc;
    exports org.example.atm_maven_jfx.Windows.MainMenu;
    opens org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.OutPutMoney to javafx.base;
    exports org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.OutPutMoney;
}