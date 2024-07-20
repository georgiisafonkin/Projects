package ru.nsu.gsafonkin.lab5_client;

import jakarta.xml.bind.JAXBException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.DirectoryChooser;
import javafx.stage.WindowEvent;
import org.w3c.dom.Text;
import ru.nsu.gsafonkin.lab5_client.protocol.XMLHandler;
import ru.nsu.gsafonkin.lab5_client.protocol.commands.*;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class EpistulaClient extends Application implements IClient {
    private VBox welcomeVBox;
    private VBox communicationVBox;
    private VBox filesVBox;
    private String nickname;
    private Socket socket;
    private ServerWriter sender;
    private ServerReader reader;
    private IFileManager fileManager = new FileManager();
    private boolean isResponseReceived = true;
    private CommandType cmdType = null;
    private ListView<String> messagesListView = new ListView<>();
    private ListView<FileLabel> filesLabelsListView = new ListView<>();
    private XMLHandler xmlCreator;
    private FileChooser fileChooser = new FileChooser();
    @Override
    public void start(Stage stage) throws IOException {

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent event) {
                stopHandle();
                try {
                    if (socket != null)
                        socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        });

        try {
            xmlCreator = new XMLHandler(this);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }

        //Functional welcome panel
        welcomeVBox = new VBox();
        Image iconImg = new Image(EpistulaClient.class.getResource("/ru/nsu/gsafonkin/lab5_client/start_icon.png").openStream());
        ImageView iconView = new ImageView(iconImg);
        iconView.setFitHeight(256);
        iconView.setFitWidth(256);
        welcomeVBox.getChildren().add(iconView);
        welcomeVBox.getChildren().add(new Label("Welcome to Epistula!\nPlease type your nickname and password to connect to server.\nAlso you can upload your own profile picture!"));

        TextField ipTextField = new TextField("45.142.36.163");
        ipTextField.setPromptText("IP ADDRESS");
        ipTextField.setMaxWidth(512);
        welcomeVBox.getChildren().add(ipTextField);

        TextField portTextField = new TextField("8000");
        portTextField.setPromptText("PORT");
        portTextField.setMaxWidth(512);
        welcomeVBox.getChildren().add(portTextField);

        TextField nicknameTextField = new TextField();
        nicknameTextField.setPromptText("Login");
        nicknameTextField.setMaxWidth(512);
        welcomeVBox.getChildren().add(nicknameTextField);
        PasswordField passwordField = new PasswordField();
        passwordField.setMaxWidth(512);
        passwordField.setPromptText("Password");
        welcomeVBox.getChildren().add(passwordField);
        Button connectionButton = new Button("Connect!");
        welcomeVBox.getChildren().add(connectionButton);
        Button setAvatarButton = new Button("Set avatar");
        welcomeVBox.getChildren().add(setAvatarButton);

        //Communication Panel
        communicationVBox = new VBox();
        communicationVBox.setVisible(false);
        communicationVBox.getChildren().add(messagesListView);
        messagesListView.setMinSize(1280, 180);
        List<Button> communicationButtons = new ArrayList<>(Arrays.asList(new Button("Send"), new Button("Attach file"),
                new Button("List"), new Button("File List"), new Button("Disconnect")));
        TextField messageTextField = new TextField();
        communicationVBox.getChildren().add(messageTextField);
        for (Button b : communicationButtons) {
            communicationVBox.getChildren().add(b);
        }

        //File manager panel
        filesVBox = new VBox();
        filesVBox.setVisible(false);
        filesVBox.getChildren().add(filesLabelsListView);
        filesLabelsListView.setMinSize(1280, 180);
        List<Button> fileManagerButtons = new ArrayList<>(Arrays.asList(new Button("Back")));
        for(Button b : fileManagerButtons) {
            filesVBox.getChildren().add(b);
        }
        filesLabelsListView.setOnEditStart(e -> {
            filesLabelsListView.getItems().get(filesLabelsListView.getEditingIndex()).getOnMouseClicked();
        });
        filesLabelsListView.setCellFactory(lv -> new ListCell<FileLabel>() {
            @Override
            protected void updateItem(FileLabel item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    FileLabel label = item;
                    label.setOnMouseClicked(event -> {
                        if (event.getClickCount() == 2) {
                            DirectoryChooser directoryChooser = new DirectoryChooser();
                            File selectedDirectory = directoryChooser.showDialog(null);
                            if (selectedDirectory == null) {
                            }
                            String savePath = selectedDirectory.getAbsolutePath() + File.separator + label.getText();
                            System.out.println("Save path: " + savePath);
                            fileManager.setPath(Path.of(savePath));
                            int id = fileManager.getIDbyName(label.getText());
                            cmdType = CommandType.DOWNLOAD;
                            sendMessage(xmlCreator.createXML(new Download(id)));
                        }
                    });
                    setGraphic(label);
                }
            }
        });

        //Some functional elements configuration

        //send button
        communicationButtons.get(0).setOnAction(event -> {
            cmdType = CommandType.MESSAGE;
            sendMessage(xmlCreator.createXML(new Message(messageTextField.getCharacters().toString())));
            messageTextField.clear();
        });

        //attaching file
        communicationButtons.get(1).setOnAction(event -> {
            File selectedFile = fileChooser.showOpenDialog(null);
            if (selectedFile != null) {
                String fileName = selectedFile.getName();

                String filePath = selectedFile.getAbsolutePath();

                String mimeType;
                try {
                    mimeType = Files.probeContentType(Path.of(filePath));
                } catch (IOException e) {
                    System.out.println("ERROR: WRONG FILE PATH");
                    throw new RuntimeException(e);
                }
                InputStream isr;
                try {
                    isr = new FileInputStream(selectedFile);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                String encoding = "base64";
                byte[] content = new byte[(int)selectedFile.length()];
                String content2 = null;
                try {
                    isr.read(content, 0, (int)selectedFile.length());
                    content2 = Base64.getEncoder().encodeToString(content);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                cmdType = CommandType.UPLOAD;
                sendMessage(xmlCreator.createXML(new Upload(fileName, mimeType, encoding, content2)));
            }
        });

        //list button
        communicationButtons.get(2).setOnAction(event -> {
            cmdType = CommandType.LIST;
            sendMessage(xmlCreator.createXML(new ru.nsu.gsafonkin.lab5_client.protocol.commands.List()));
        });

        //File manager button
        communicationButtons.get(3).setOnAction(event -> {
            communicationVBox.setVisible(false);
            filesVBox.setVisible(true);
        });

        //File manage "back" button
        fileManagerButtons.get(0).setOnAction(event -> {
            filesVBox.setVisible(false);
            communicationVBox.setVisible(true);
        });

        //disconnect button
        communicationButtons.get(4).setOnAction(event -> {
            cmdType = CommandType.LOGOUT;
            sendMessage(xmlCreator.createXML(new Logout()));
            messagesListView.getItems().clear();
            messagesListView.refresh();
            communicationVBox.setVisible(false);
            welcomeVBox.setVisible(true);
        });


        //CONNECTION HERE!
        connectionButton.setOnAction(event -> {
            String ip = ipTextField.getText();
            int port = Integer.valueOf(portTextField.getText());
            connect(ip, port);
            this.nickname = nicknameTextField.getText();
            String eventMsg = xmlCreator.createXML(new Login(nicknameTextField.getText(), passwordField.getText()));
            cmdType = CommandType.LOGIN;
            sendMessage(eventMsg);
        } );

        //SET AVATAR HERE
        setAvatarButton.setOnAction(event -> {
            File selectedAvatar = fileChooser.showOpenDialog(null);
        });

        //main panel
        StackPane main = new StackPane(welcomeVBox, communicationVBox, filesVBox);

        //scene
        Scene scene = new Scene(main, 1280, 720);
        stage.setTitle("Epistula Chat Systems");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void connect(String host, int port) {
        try {
            if (socket == null) {
                socket = new Socket(host, port);
                sender = new ServerWriter(this, socket.getOutputStream());
                reader = new ServerReader(this, socket.getInputStream());
                sender.start();
                reader.start();
            }
            if (socket.getPort() != port || !socket.getInetAddress().equals(host)) {
                socket.close();
                sender.interrupt();
                reader.interrupt();

                socket = new Socket(host, port);
                sender = new ServerWriter(this, socket.getOutputStream());
                reader = new ServerReader(this, socket.getInputStream());
                sender.start();
                reader.start();
            }
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stopHandle() {
        if (sender != null)
            sender.interrupt();
        if (reader != null)
            reader.interrupt();
    }

    @Override
    public void sendMessage(String msg) {
        sender.getMessagesToWrite().add(msg);
    }

    @Override
    public void recieveMessage(String inputMsg) {
        System.out.println("Received message: " + inputMsg);
        String msg = processMessage(inputMsg);
        if (msg != null && !msg.isBlank()) {
            Platform.runLater(() -> {messagesListView.getItems().add(msg);});
        }
    }

    @Override
    public String processMessage(String bytes) {
        if (bytes != null && !bytes.isBlank()) {
            String msg = xmlCreator.parseXml(bytes);
            if (cmdType == CommandType.LOGIN && msg.equals("SUCCESS")) {
                welcomeVBox.setVisible(false);
                communicationVBox.setVisible(true);
                return "";
            }
            if (msg.equals("Server: Incorrect username or password")) {
                Platform.runLater(() -> {welcomeVBox.getChildren().add(new Label(msg));});
            }
            if (msg.contains("Server: ") && !msg.equals("Server: Incorrect username or password")) {
                Platform.runLater(() -> {welcomeVBox.getChildren().add(new Label(msg));});
            }
            if (cmdType == CommandType.MESSAGE && msg.equals("SUCCESS")) {
                return null;
            }
            return msg;
        }
        return null;
    }
    @Override
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    @Override
    public boolean isResponseReceived() {
        return isResponseReceived;
    }
    @Override
    public void setResponseReceived(boolean responseReceived) {
        isResponseReceived = responseReceived;
    }
    @Override
    public CommandType getCmdType() {
        return cmdType;
    }
    @Override
    public void setCmdType(CommandType cmdType) {
        this.cmdType = cmdType;
    }

    @Override
    public boolean isClientSocketConnected() {
        return socket.isConnected();
    }

    @Override
    public boolean isClientSocketClosed() {
        return socket.isClosed();
    }

    @Override
    public void receiveFile(String name, int id) {
        fileManager.getFiles().put(name, id);
        Platform.runLater(() -> {
            filesLabelsListView.getItems().add(new FileLabel(name, id));
        });
    }
    @Override
    public IFileManager getFileManager() {
        return fileManager;
    }

}