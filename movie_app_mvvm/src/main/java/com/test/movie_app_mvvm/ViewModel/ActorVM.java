package com.test.movie_app_mvvm.ViewModel;

import com.test.movie_app_mvvm.Model.Actor;
import com.test.movie_app_mvvm.Model.Movie;
import com.test.movie_app_mvvm.Model.Service.ActorService;
import com.test.movie_app_mvvm.Model.Service.MovieService;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ActorVM {
    private final ActorService actorService;
    private final MovieService movieService;

    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty age = new SimpleStringProperty("");
    private final StringProperty gender = new SimpleStringProperty("");
    private final ObjectProperty<Event<String>> message = new SimpleObjectProperty<>(new Event<>(""));

    @Getter private final ObservableList<Map<String, Object>> actorsList = FXCollections.observableArrayList();
    private final ObservableList<String> movieList = FXCollections.observableArrayList();

    private final ObjectProperty<Map<String, Object>> selectedActor = new SimpleObjectProperty<>();

    @Getter private final ICommand saveCommand;
    @Getter private final ICommand deleteCommand;
    @Getter private final ICommand updateUIFieldsCommand;
    @Getter private final ICommand loadMoviesCommand;

    private Integer crtActorId = null;

    public ActorVM(ActorService actorService, MovieService movieService) {
        this.actorService = actorService;
        this.movieService = movieService;

        this.saveCommand = this::executeSave;
        this.deleteCommand = this::executeDelete;
        this.updateUIFieldsCommand = this::executeUpdateUIFields;
        this.loadMoviesCommand = this::executeLoadMovies;

        selectedActor.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                this.crtActorId = (Integer) newValue.get("id");
            } else {
                this.crtActorId = null;
            }
        });

        refreshTable();
    }

    private void executeSave() {
        try {
            if (!isValid()) {
                message.set(new Event<>("Please fill in all fields (name, age, gender)."));
                return;
            }

            Actor actor = (crtActorId == null) ? new Actor() : actorService.getActorById(crtActorId);
            if (actor != null) {
                actor.setName(name.get());
                actor.setAge(Integer.parseInt(age.get()));
                actor.setGender(gender.get());

                if (crtActorId == null) {
                    actorService.saveActor(actor);
                    message.set(new Event<>("Actor saved successfully!"));
                } else {
                    actorService.updateActor(actor);
                    message.set(new Event<>("Actor updated successfully!"));
                }
                resetState();
                refreshTable();
            }
        } catch (Exception e) {
            message.set(new Event<>("Error saving actor: " + e.getMessage()));
        }
    }

    private void executeDelete() {
        try {
            Map<String, Object> selectedMap = selectedActor.get();
            if (selectedMap == null) {
                message.set(new Event<>("Please select an actor to delete."));
                return;
            }
            Actor actor = actorService.getActorById((Integer) selectedMap.get("id"));
            actorService.deleteActor(actor);
            message.set(new Event<>("Actor deleted successfully!"));
            resetState();
            refreshTable();
        } catch (Exception e) {
            message.set(new Event<>("Error on delete: " + e.getMessage()));
        }
    }

    private void executeUpdateUIFields() {
        Map<String, Object> selected = selectedActor.get();
        if (selected == null) {
            message.set(new Event<>("Select an actor to edit."));
            return;
        }
        name.set(String.valueOf(selected.get("name")));
        age.set(String.valueOf(selected.get("age")));
        gender.set(String.valueOf(selected.get("gender")));
        message.set(new Event<>("Ready to edit: " + selected.get("name")));
    }

    private void executeLoadMovies() {
        Map<String, Object> selectedMap = selectedActor.get();
        if (selectedMap == null) {
            message.set(new Event<>("Select an actor to see their movies."));
            return;
        }
        Actor selected = actorService.getActorById((Integer) selectedMap.get("id"));
        if (selected != null) {
            List<String> movies = movieService.getMoviesByActor(selected)
                    .stream()
                    .map(Movie::getName)
                    .toList();
            movieList.setAll(movies);
            message.set(new Event<>("Found " + movies.size() + " movies for " + selected.getName()));
        }
    }

    public void refreshTable() {
        List<Actor> actors = actorService.getActors();
        List<Map<String, Object>> mappedActors = actors.stream().map(actor -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", actor.getActorId());
            map.put("name", actor.getName());
            map.put("age", actor.getAge());
            map.put("gender", actor.getGender());
            return map;
        }).toList();
        actorsList.setAll(mappedActors);
    }

    private void resetState() {
        crtActorId = null;
        name.set("");
        age.set("");
        gender.set("");
        movieList.clear();
    }

    private boolean isValid() {
        if (name.get() == null || name.get().isBlank()) return false;
        if (age.get() == null || age.get().isBlank()) return false;
        if (gender.get() == null || gender.get().isBlank()) return false;
        try {
            Integer.parseInt(age.get());
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public StringProperty nameProperty() { return name; }
    public StringProperty ageProperty() { return age; }
    public StringProperty genderProperty() { return gender; }
    public ObjectProperty<Event<String>> messageProperty() { return message; }
    public ObjectProperty<Map<String, Object>> selectedActorProperty() { return selectedActor; }
    public ObservableList<String> getActorMovies() { return movieList; }
}