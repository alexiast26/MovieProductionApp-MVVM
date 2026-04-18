package com.test.movie_app_mvvm.ViewModel;

import com.test.movie_app_mvvm.Model.*;
import com.test.movie_app_mvvm.Model.Service.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class MovieVM {
    private final MovieService movieService;
    private final WindowManager windowManager;

    @Getter private final StringProperty title = new SimpleStringProperty("");
    @Getter private final StringProperty director = new SimpleStringProperty("");
    @Getter private final StringProperty screenwriter = new SimpleStringProperty("");
    @Getter private final StringProperty duration = new SimpleStringProperty("");
    @Getter private final StringProperty year = new SimpleStringProperty("");

    @Getter private final StringProperty imageUrl1 = new SimpleStringProperty("");
    @Getter private final StringProperty imageUrl2 = new SimpleStringProperty("");
    @Getter private final StringProperty imageUrl3 = new SimpleStringProperty("");

    @Getter private final ObservableList<Map<String, Object>> moviesList = FXCollections.observableArrayList();
    @Getter private final ObservableList<String> actorNames = FXCollections.observableArrayList();
    @Getter private final ObservableList<String> categories = FXCollections.observableArrayList();

    @Getter private final ObjectProperty<Map<String, Object>> selectedMovieMap = new SimpleObjectProperty<>();
    @Getter private final ObjectProperty<Event<String>> statusEvent = new SimpleObjectProperty<>(new Event<>(""));
    @Getter private final ObjectProperty<String> selectedCategory = new SimpleObjectProperty<>("All");

    @Getter private final ICommand deleteCommand;
    @Getter private final ICommand filterCommand;
    @Getter private final ICommand addMovieCommand;
    @Getter private final ICommand modifyMovieCommand;

    public MovieVM(MovieService movieService, WindowManager windowManager, MovieFormVM movieFormVM) {
        this.movieService = movieService;
        this.windowManager = windowManager;

        this.addMovieCommand = () -> {
            movieFormVM.resetFields();
            windowManager.showMovieForm("Add New Movie");
            refreshTable();
            selectedMovieMap.set(null);
        };

        this.modifyMovieCommand = () -> {
            Map<String, Object> selected = selectedMovieMap.get();
            if (selected == null) {
                statusEvent.set(new Event<>("Please select a movie to modify."));
                return;
            }
            movieFormVM.loadMovieData((Integer) selected.get("id"));
            windowManager.showMovieForm("Modify Movie: " + selected.get("name"));
            refreshTable();
            selectedMovieMap.set(null);
        };
        this.deleteCommand = this::executeDelete;
        this.filterCommand = this::executeFilter;


        categories.add("All");
        categories.addAll(Arrays.stream(Category.values()).map(Enum::name).toList());

        selectedMovieMap.addListener((obs, old, val) -> {
            if (val != null) {
                loadMovieDetails((Integer) val.get("id"));
            } else {
                clearDetails();
            }
        });

        refreshTable();
    }

    public void refreshTable() {
        List<Map<String, Object>> mapped = movieService.getMovies().stream()
                .map(m -> Map.of("id", (Object) m.getMovieId(), "name", m.getName()))
                .toList();
        moviesList.setAll(mapped);
    }

    private void loadMovieDetails(Integer id) {
        Movie m = movieService.getMovieById(id);
        if (m != null) {
            title.set(m.getName());
            director.set(m.getDirector() != null ? m.getDirector().getName() : "N/A");
            screenwriter.set(m.getScreenwriter() != null ? m.getScreenwriter().getName() : "N/A");
            duration.set(m.getDuration() + " min");
            year.set(String.valueOf(m.getReleaseYear()));

            actorNames.setAll(m.getActors().stream().map(Actor::getName).toList());

            List<String> urls = m.getImages().stream().map(Image::getImgUrl).toList();
            imageUrl1.set(urls.size() > 0 ? urls.get(0) : "");
            imageUrl2.set(urls.size() > 1 ? urls.get(1) : "");
            imageUrl3.set(urls.size() > 2 ? urls.get(2) : "");
        }
    }

    private void clearDetails() {
        title.set("");
        director.set("");
        screenwriter.set("");
        duration.set("");
        year.set("");
        actorNames.clear();
        imageUrl1.set("");
        imageUrl2.set("");
        imageUrl3.set("");
    }

    private void executeDelete() {
        Map<String, Object> selected = selectedMovieMap.get();
        if (selected == null) {
            statusEvent.set(new Event<>("Please select a movie to delete."));
            return;
        }

        Movie movie = movieService.getMovieById((Integer) selected.get("id"));
        if (movie == null) {
            statusEvent.set(new Event<>("Movie no longer exists. Please refresh."));
            refreshTable();
            return;
        }

        movieService.deleteMovie(movie);
        statusEvent.set(new Event<>("Movie deleted successfully."));

        clearDetails();
        selectedMovieMap.set(null);
        refreshTable();
    }

    private void executeFilter() {
        String categoryName = selectedCategory.get();
        List<Movie> allMovies = movieService.getMovies();
        List<Movie> filteredMovies;

        if (categoryName == null || categoryName.equals("All")) {
            filteredMovies = allMovies;
        } else {
            try {
                Category targetCategory = Category.valueOf(categoryName);
                filteredMovies = allMovies.stream()
                        .filter(m -> m.getCategory() == targetCategory)
                        .toList();
            } catch (IllegalArgumentException e) {
                statusEvent.set(new Event<>("Invalid category selected."));
                filteredMovies = allMovies;
            }
        }

        List<Map<String, Object>> mapped = filteredMovies.stream()
                .map(m -> Map.of("id", (Object) m.getMovieId(), "name", m.getName()))
                .toList();

        moviesList.setAll(mapped);
    }
}