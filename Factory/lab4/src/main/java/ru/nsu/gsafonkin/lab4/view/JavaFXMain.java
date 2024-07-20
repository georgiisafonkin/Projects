package ru.nsu.gsafonkin.lab4.view;

import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import ru.nsu.gsafonkin.lab4.controller.SlidersController;
import ru.nsu.gsafonkin.lab4.model.IModelListener;
import ru.nsu.gsafonkin.lab4.model.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavaFXMain extends Application implements IModelListener {
    private Model model;
    StackPane mainPain;
    GridPane interfacePane;
    StackPane factorySchemeStackPane;
    VBox dataVbox;
    HBox buttonsHBox;
    VBox slidersVBox;
    SlidersController controller;
    private Map<String, LabelListener> dataLabels = new HashMap<>();
    @Override
    public void start(Stage primaryStage) throws Exception {
        //Turn on model
        model = new Model(this, "/ru/nsu/gsafonkin/lab4/config.txt");

        //Creating controller
        controller = new SlidersController(model);

        //Main Pane
        mainPain = new StackPane();

        //Interface panel with data and controls
        interfacePane = new GridPane();

        //Buttons
        buttonsHBox = new HBox();
        Map<String, Button> buttons = new HashMap<>();
        buttons.put("Start", new Button("Start"));
        buttons.get("Start").setMinSize(200, 65);
        GridPane.setVgrow(buttons.get("Start"), Priority.ALWAYS);
        buttons.put("Stop", new Button("Stop"));
        buttons.get("Stop").setMinSize(200, 65);
        GridPane.setVgrow(buttons.get("Stop"), Priority.ALWAYS);
        buttons.put("Reset", new Button("Reset"));
        buttons.get("Reset").setMinSize(200, 65);
        GridPane.setVgrow(buttons.get("Reset"), Priority.ALWAYS);
        buttonsHBox.getChildren().add(buttons.get("Start"));
        buttonsHBox.getChildren().add(buttons.get("Stop"));
        buttonsHBox.getChildren().add(buttons.get("Reset"));
        buttonsHBox.setSpacing(20);

        //Factory scheme panel
        factorySchemeStackPane = new StackPane();
        Image scheme = new Image(JavaFXMain.class.getResource("/ru/nsu/gsafonkin/lab4/factory.png").openStream());
        ImageView schemeView = new ImageView(scheme);
        schemeView.setFitHeight(480);
        schemeView.setFitWidth(640);
        factorySchemeStackPane.getChildren().add(schemeView);

        //Data panel
        dataVbox = new VBox();

        dataLabels.put("Accessory storage", new LabelListener(model.getAccessoriesStorage(), "Accessory storage: "));
        model.getAccessoriesStorage().setStorageListener(dataLabels.get("Accessory storage"));
        dataVbox.getChildren().add(dataLabels.get("Accessory storage"));

        dataLabels.put("Body storage", new LabelListener(model.getBodiesStorage(), "Body storage: "));
        model.getBodiesStorage().setStorageListener(dataLabels.get("Body storage"));
        dataVbox.getChildren().add(dataLabels.get("Body storage"));

        dataLabels.put("Engines storage", new LabelListener(model.getEnginesStorage(),"Engines storage: "));
        model.getEnginesStorage().setStorageListener(dataLabels.get("Engines storage"));
        dataVbox.getChildren().add(dataLabels.get("Engines storage"));

        dataLabels.put("Cars storage", new LabelListener(model.getCarsStorage(), "Cars storage:"));
        model.getCarsStorage().setStorageListener(dataLabels.get("Cars storage"));
        dataVbox.getChildren().add(dataLabels.get("Cars storage"));

        //Sliders panel
        slidersVBox = new VBox(25);
        final int slidersNumber = 4;
        List<String> slidersNames = new ArrayList<>();
        slidersNames.add("Engine supply");
        slidersNames.add("Body supply");
        slidersNames.add("Accessory supply");
        slidersNames.add("Car requesting");
        List<Label> labels = new ArrayList<>();
        List<Slider> sliders = new ArrayList<>();
        for (int i = 0; i < slidersNumber; ++i) {
            labels.add(new Label(slidersNames.get(i)));
            sliders.add(new Slider(1, 10, 1));
            sliders.get(sliders.size() - 1).setBlockIncrement(1);
            sliders.get(sliders.size() - 1).setSnapToTicks(true);
            int finalI = i;
            sliders.get(sliders.size() - 1).valueProperty().addListener((observable, oldValue, newValue) -> {
                labels.get(finalI).setText("Time Interval between " + slidersNames.get(finalI) + " is " + newValue.intValue() + " seconds");
                controller.notifyNewTimeout(slidersNames.get(finalI), newValue.longValue());
            });
            sliders.get(sliders.size() - 1).setMaxWidth(500);
            sliders.get(sliders.size() - 1).setShowTickMarks(true);
            sliders.get(sliders.size() - 1).setShowTickLabels(true);
            sliders.get(sliders.size() - 1).setMajorTickUnit(1);
            sliders.get(sliders.size() - 1).setMinorTickCount(0);
            sliders.get(sliders.size() - 1).setSnapToTicks(true);
            sliders.get(sliders.size() - 1).setOrientation(Orientation.HORIZONTAL);
            slidersVBox.getChildren().add(labels.get(labels.size() - 1));
            slidersVBox.getChildren().add(sliders.get(sliders.size() - 1));
        }

        interfacePane.setAlignment(Pos.CENTER);
        interfacePane.getColumnConstraints().add(new ColumnConstraints(360));
        interfacePane.getColumnConstraints().add(new ColumnConstraints(640));
        interfacePane.getRowConstraints().add(new RowConstraints(65));
        interfacePane.getRowConstraints().add(new RowConstraints(480));
        GridPane.setConstraints(dataVbox, 0, 0);
        GridPane.setConstraints(slidersVBox, 0, 1);
        GridPane.setConstraints(schemeView, 1, 1);
        GridPane.setConstraints(buttonsHBox, 1, 0);
        interfacePane.getChildren().add(dataVbox);
        interfacePane.getChildren().add(slidersVBox);
        interfacePane.getChildren().add(schemeView);
        interfacePane.getChildren().add(buttonsHBox);
        interfacePane.setGridLinesVisible(true);

        mainPain.getChildren().add(interfacePane);
        Image truckImg = new Image(JavaFXMain.class.getResource("/ru/nsu/gsafonkin/lab4/truck.png").openStream());
        ImageView truckView = new ImageView(truckImg);
        truckView.setFitHeight(180);
        truckView.setFitWidth(240);
        mainPain.getChildren().add(truckView);
        mainPain.getChildren().get(1).setTranslateX(512);
        mainPain.getChildren().get(1).setTranslateY(256);

        // Create a scene and add the layout to it
        Scene scene = new Scene(mainPain, 1280, 720);

        // Set the stage title and add the scene to it
        primaryStage.setTitle("Factory");
        primaryStage.setScene(scene);
        primaryStage.setOnHidden(event -> {
            if (!primaryStage.isShowing()) {
                model.safeCompletion();
            }
        });
        primaryStage.setResizable(false);

        // Show the stage
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void onModelChanged() {

    }
}