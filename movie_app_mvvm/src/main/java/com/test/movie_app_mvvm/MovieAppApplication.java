package com.test.movie_app_mvvm;

import com.test.movie_app_mvvm.Config.JavaFxApp;
import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MovieAppApplication {
    public static void main(String[] args) {
        Application.launch(JavaFxApp.class, args);
    }
}