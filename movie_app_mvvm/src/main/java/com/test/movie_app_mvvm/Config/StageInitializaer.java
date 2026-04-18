package com.test.movie_app_mvvm.Config;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class StageInitializaer implements ApplicationListener<StageReadyEvent> {

    @Value("classpath:/Demo.fxml")
    private Resource fxml;
    private final ApplicationContext context;

    public StageInitializaer(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(fxml.getURL());

            fxmlLoader.setControllerFactory(context::getBean);

            Parent parent = fxmlLoader.load();

            Stage stage = event.getStage();
            stage.setScene(new Scene(parent));
            stage.setTitle("Movie App");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}