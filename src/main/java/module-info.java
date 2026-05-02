module com.cagritasoz.judge {
    /*
    The classpath is a list of locations where the Java Virtual Machine (JVM) looks for your compiled code (.class files),
    libraries (.jar files), and other resource files (like configuration or image files).
     */
    // JavaFX
    requires javafx.controls; // pulls in the JavaFX controls JAR
    requires javafx.fxml;

    // Jackson
    requires com.fasterxml.jackson.databind;

    // Logging
    requires org.slf4j;
    requires org.slf4j.simple;

    // Lombok — compile-only, no runtime presence
    requires static lombok;
    requires com.cagritasoz.judge;

    // Jackson needs reflective access to model classes for (de)serialization
    opens com.cagritasoz.model to com.fasterxml.jackson.databind;

    // JavaFX Application loader needs reflective access to the Application subclass
    opens com.cagritasoz.ui to javafx.graphics;

    // FXMLLoader needs reflective access to inject @FXML fields / call initialize()
    opens com.cagritasoz.ui.controller to javafx.fxml;
}
