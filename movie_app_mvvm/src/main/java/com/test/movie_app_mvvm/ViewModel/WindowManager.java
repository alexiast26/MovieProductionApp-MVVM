package com.test.movie_app_mvvm.ViewModel;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class WindowManager {
    private final ApplicationContext context;

    public WindowManager(ApplicationContext context) {
        this.context = context;
    }

    public void showMovieForm(String title) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/MovieForm.fxml"));
            loader.setControllerFactory(context::getBean);

            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}