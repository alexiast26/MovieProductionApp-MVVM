package com.test.movie_app_mvvm.View;

import com.test.movie_app_mvvm.ViewModel.Event;
import com.test.movie_app_mvvm.ViewModel.MovieFormVM;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class MovieFormView {
    @FXML private TextField titleField, yearField, durationField, directorField, screenwriterField;
    @FXML private ChoiceBox<String> genraBox, categoryBox;
    @FXML private TableView<Map<String, Object>> addActorsTable, existingActorsTable;
    @FXML private TableColumn<Map<String, Object>, String> colAvailableActors, colExistingActors;
    @FXML private Label lblMessage;

    @Autowired private MovieFormVM formVM;

    @FXML
    public void initialize() {
        titleField.textProperty().bindBidirectional(formVM.getTitle());
        yearField.textProperty().bindBidirectional(formVM.getYear());
        durationField.textProperty().bindBidirectional(formVM.getDuration());
        directorField.textProperty().bindBidirectional(formVM.getDirectorInput());
        screenwriterField.textProperty().bindBidirectional(formVM.getScreenwriterInput());
        lblMessage.textProperty().bind(formVM.getMessage().asString());


        genraBox.setItems(formVM.getGenreOptions());
        genraBox.valueProperty().bindBidirectional(formVM.getSelectedGenre());
        categoryBox.setItems(formVM.getCategoryOptions());
        categoryBox.valueProperty().bindBidirectional(formVM.getSelectedCategory());

        addActorsTable.setItems(formVM.getAvailableActors());
        existingActorsTable.setItems(formVM.getSelectedActors());
        addActorsTable.getSelectionModel().selectedItemProperty().addListener((obs, old, val) ->
                formVM.getSelectedAvailableActor().set(val)
        );
        existingActorsTable.getSelectionModel().selectedItemProperty().addListener((obs, old, val) ->
                formVM.getSelectedExistingActor().set(val)
        );

        setupTableColumns();
        formVM.getShouldClose().addListener((obs, old, val) -> {
            Stage stage = (Stage) titleField.getScene().getWindow();
            stage.close();
        });
        setupMessageListener(formVM.getMessage());
    }

    private void setupMessageListener(ObjectProperty<Event<String>> eventProperty) {
        eventProperty.addListener((obs, old, ev) -> {
            Optional.ofNullable(ev.getContentIfNotHandled()).ifPresent(this::showAlert);
        });
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void setupTableColumns() {
        colAvailableActors.setCellValueFactory(d ->
                new SimpleStringProperty(String.valueOf(d.getValue().get("name")))
        );
        colExistingActors.setCellValueFactory(d ->
                new SimpleStringProperty(String.valueOf(d.getValue().get("name")))
        );
    }

    @FXML
    public void saveData() {
        formVM.getSaveCommand().execute();
    }

    @FXML
    public void addActorToMovie() {
        formVM.getAddActorCommand().execute();
    }

    @FXML
    public void removeActorFromList() {
        formVM.getRemoveActorCommand().execute();
    }

    @FXML
    public void chooseImage() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Select Movie Images (Max 3)");

        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
        );

        List<File> files = fc.showOpenMultipleDialog(titleField.getScene().getWindow());
        formVM.setImages(files);
    }

    @FXML
    public void clearImages() {
        formVM.clearImages();
    }
}