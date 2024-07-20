module ru.nsu.gsafonkin.lab4 {
    requires javafx.controls;
    requires javafx.fxml;


    opens ru.nsu.gsafonkin.lab4 to javafx.fxml;
    exports ru.nsu.gsafonkin.lab4.view;
    opens ru.nsu.gsafonkin.lab4.view to javafx.fxml;
}