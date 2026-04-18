package com.test.movie_app_mvvm.View;

import com.test.movie_app_mvvm.ViewModel.*;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class AppView {
    @FXML private TableView<Map<String, Object>> movieTable;
    @FXML private TableColumn<Map<String, Object>, Object> idMovie;
    @FXML private TableColumn<Map<String, Object>, String> movieName;

    @FXML private Text titleText;
    @FXML private Text directorText;
    @FXML private Text screenwriterText;
    @FXML private Text durationText;
    @FXML private Text yearText;
    @FXML private TableView<String> movieActorsTable;
    @FXML private TableColumn<String, String> movieActors;

    @FXML private ImageView image1, image2, image3;
    @FXML private ChoiceBox<String> categoryFilter;

    @FXML private TableView<Map<String, Object>> actorsTable;
    @FXML private TableColumn<Map<String, Object>, Object> idActor, actorAge;
    @FXML private TableColumn<Map<String, Object>, String> actorName, actorGender;
    @FXML private TableView<String> actorMoviesTable;
    @FXML private TableColumn<String, String> actorMovies;
    @FXML private TextField actorNameAdd, actorAgeAdd, actorGenderAdd;

    @FXML private TableView<Map<String, Object>> directorTable;
    @FXML private TableColumn<Map<String, Object>, Object> idDirector, directorAge;
    @FXML private TableColumn<Map<String, Object>, String> directorName, directorGender;
    @FXML private TextField directorNameAdd, directorAgeAdd, directorGenderAdd;

    @FXML private TableView<Map<String, Object>> screenwriterTable;
    @FXML private TableColumn<Map<String, Object>, Object> idScreenwriter, screenwriterAge;
    @FXML private TableColumn<Map<String, Object>, String> screenwriterName, screenwriterGender;
    @FXML private TextField screenwriterNameAdd, screenwriterAgeAdd, screenwriterGenderAdd;

    @Autowired private ActorVM actorVM;
    @Autowired private MovieVM movieVM;
    @Autowired private DirectorVM directorVM;
    @Autowired private ScreenwriterVM screenwriterVM;


    @FXML
    public void initialize() {
        setupMovieTab();
        setupActorTab();
        setupDirectorTab();
        setupScreenwriterTab();
    }

    private void setupMovieTab() {
        movieTable.setItems(movieVM.getMoviesList());
        idMovie.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().get("id")));
        movieName.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().get("name"))));

        titleText.textProperty().bind(movieVM.getTitle());
        directorText.textProperty().bind(movieVM.getDirector());
        screenwriterText.textProperty().bind(movieVM.getScreenwriter());
        durationText.textProperty().bind(movieVM.getDuration());
        yearText.textProperty().bind(movieVM.getYear());

        movieActorsTable.setItems(movieVM.getActorNames());
        movieActors.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()));

        categoryFilter.setItems(movieVM.getCategories());

        categoryFilter.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {
            movieVM.getSelectedCategory().set(val);
            movieVM.getFilterCommand().execute();
        });

        movieTable.getSelectionModel().selectedItemProperty().addListener((obs, old, val) ->
                movieVM.getSelectedMovieMap().set(val)
        );

        bindImageView(image1, movieVM.getImageUrl1());
        bindImageView(image2, movieVM.getImageUrl2());
        bindImageView(image3, movieVM.getImageUrl3());

        setupMessageListener(movieVM.getStatusEvent());
    }


    private void bindImageView(ImageView view, StringProperty urlProperty) {
        urlProperty.addListener((obs, old, url) -> {
            String uri = url.startsWith("file:") || url.startsWith("http")
                    ? url
                    : new java.io.File(url).toURI().toString();
            view.setImage(new Image(uri));
            view.setVisible(true);
        });
    }

    private void setupActorTab() {
        actorsTable.setItems(actorVM.getActorsList());
        idActor.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().get("id")));
        actorName.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().get("name"))));
        actorAge.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().get("age")));
        actorGender.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().get("gender"))));

        actorNameAdd.textProperty().bindBidirectional(actorVM.nameProperty());
        actorAgeAdd.textProperty().bindBidirectional(actorVM.ageProperty());
        actorGenderAdd.textProperty().bindBidirectional(actorVM.genderProperty());

        actorMoviesTable.setItems(actorVM.getActorMovies());
        actorMovies.setCellValueFactory(d -> new SimpleStringProperty(d.getValue()));

        actorsTable.getSelectionModel().selectedItemProperty().addListener((obs, old, val) ->
                actorVM.selectedActorProperty().set(val)
        );

        setupMessageListener(actorVM.messageProperty());
    }

    private void setupDirectorTab() {
        directorTable.setItems(directorVM.getDirectorsList());
        idDirector.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().get("id")));
        directorName.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().get("name"))));
        directorAge.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().get("age")));
        directorGender.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().get("gender"))));

        directorNameAdd.textProperty().bindBidirectional(directorVM.nameProperty());
        directorAgeAdd.textProperty().bindBidirectional(directorVM.ageProperty());
        directorGenderAdd.textProperty().bindBidirectional(directorVM.genderProperty());

        directorTable.getSelectionModel().selectedItemProperty().addListener((obs, old, val) ->
                directorVM.selectedDirectorProperty().set(val)
        );

        setupMessageListener(directorVM.messageProperty());
    }

    private void setupScreenwriterTab() {
        screenwriterTable.setItems(screenwriterVM.getScreenwritersList());
        idScreenwriter.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().get("id")));
        screenwriterName.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().get("name"))));
        screenwriterAge.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().get("age")));
        screenwriterGender.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().get("gender"))));

        screenwriterNameAdd.textProperty().bindBidirectional(screenwriterVM.nameProperty());
        screenwriterAgeAdd.textProperty().bindBidirectional(screenwriterVM.ageProperty());
        screenwriterGenderAdd.textProperty().bindBidirectional(screenwriterVM.genderProperty());

        screenwriterTable.getSelectionModel().selectedItemProperty().addListener((obs, old, val) ->
                screenwriterVM.selectedScreenwriterProperty().set(val)
        );

        setupMessageListener(screenwriterVM.messageProperty());
    }

    private void setupMessageListener(javafx.beans.property.ObjectProperty<Event<String>> eventProperty) {
        eventProperty.addListener((obs, old, ev) -> {
            Optional.ofNullable(ev.getContentIfNotHandled()).ifPresent(this::showAlert);
        });
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML public void addActor()      { actorVM.getSaveCommand().execute(); }
    @FXML public void modifyActor()   { actorVM.getUpdateUIFieldsCommand().execute(); }
    @FXML public void deleteActor()   { actorVM.getDeleteCommand().execute(); }
    @FXML public void getMovies()     { actorVM.getLoadMoviesCommand().execute(); }

    @FXML public void addDirector()    { directorVM.getSaveCommand().execute(); }
    @FXML public void deleteDirector() { directorVM.getDeleteCommand().execute(); }
    @FXML public void modifyDirector() { directorVM.getUpdateUIFieldsCommand().execute(); }

    @FXML public void addScreenwriter()    { screenwriterVM.getSaveCommand().execute(); }
    @FXML public void deleteScreenwriter() { screenwriterVM.getDeleteCommand().execute(); }
    @FXML public void modifyScreenwriter() { screenwriterVM.getUpdateUIFieldsCommand().execute(); }

    @FXML public void deleteMovie() { movieVM.getDeleteCommand().execute(); }

    @FXML public void addMovie()    { movieVM.getAddMovieCommand().execute(); }
    @FXML public void modifyMovie() { movieVM.getModifyMovieCommand().execute(); }


}