module ru.nsu.gsafonkin.lab5_client {
    requires javafx.controls;
    requires javafx.fxml;
    requires jakarta.xml.bind;

    opens ru.nsu.gsafonkin.lab5_client.protocol.commands;
    opens ru.nsu.gsafonkin.lab5_client to javafx.fxml;
    exports ru.nsu.gsafonkin.lab5_client;
    exports ru.nsu.gsafonkin.lab5_client.protocol.commands;
}