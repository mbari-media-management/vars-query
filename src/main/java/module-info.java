open module org.mbari.vars.query {
    requires sdkfx;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires java.sql;
    requires java.desktop;
    requires java.scripting;
    requires jfxtras.controls;
//    requires java.awt;
    exports java.lang to com.google.guice;
}