package emailClient.Controllers;

import emailClient.Main;
import emailClient.Models.MessageModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.mail.*;
import java.io.IOException;



public class HomeController {

    public static Stage dialogStage;

    @FXML
    private TableView<MessageModel> messageTable;
    @FXML
    private TableColumn<MessageModel, String> emailSubjectColunm;

    public static ObservableList<MessageModel> messageData = FXCollections.observableArrayList();

    public HomeController() {
    }

    @FXML
    private void initialize() {
        showInboxSubject();
    }

    @FXML
    private void handleCreateMessageDialog() {
        showCreateMailDialog();
    }

    private void showCreateMailDialog() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(HomeController.class.getResource("../Views/fxml/sendMailDialog.fxml"));
            AnchorPane mailDialogPane = (AnchorPane) loader.load();

            dialogStage = new Stage();
            dialogStage.setTitle("Soạn thư");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(Main.primaryStage);
            Scene scene = new Scene(mailDialogPane);
            dialogStage.setScene(scene);
            dialogStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void getInbox() {
        // create the POP3 store object and connect with the pop server
        try {
            Store store = LoginController.session.getStore("pop3s");
            store.connect();

            // create the folder object and open it
            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);

            // retrieve the messages from the folder in an array and print it
            Message[] messages = emailFolder.getMessages();
            System.out.println("messages.length---" + messages.length);

            for (int i = 0, n = messages.length; i < n; i++) {
                Message message = messages[i];
                String recipient = String.valueOf(message.getFrom()[0]);
                messageData.add(new MessageModel(i, recipient, message.getSubject(), message.getContent().toString()));
//                System.out.println(Main.messageData.get(i).getMailSubject());
/*
                System.out.println("---------------------------------");
                System.out.println("Email Number " + (i + 1));
                System.out.println("Subject: " + message.getSubject());
                System.out.println("From: " + message.getFrom()[0]);
                System.out.println("Text: " + message.getContent().toString());
*/
            }
            // close the store and folder objects

            emailFolder.close(false);
            store.close();
        } catch (NoSuchProviderException e) {
            System.out.println("Catch session get store");
            e.printStackTrace();
        } catch (MessagingException e) {
            System.out.println("Catch store connect");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Catch add messagedata");
            e.printStackTrace();
        }
    }


    public void showInboxSubject() {
        getInbox();
        System.out.println(messageData.get(1).mailSubjectProperty());
        messageTable.setItems(messageData);
        emailSubjectColunm.setCellValueFactory(messageModelStringCellDataFeatures
                -> messageModelStringCellDataFeatures.getValue().mailSubjectProperty());
    }

}
