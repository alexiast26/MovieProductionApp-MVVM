package com.test.movie_app_mvvm.ViewModel;

import com.test.movie_app_mvvm.Model.*;
import com.test.movie_app_mvvm.Model.Service.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import org.springframework.stereotype.Component;
import java.io.File;
import java.util.*;

@Component
public class MovieFormVM {
    private final MovieService movieService;
    private final ActorService actorService;
    private final DirectorService directorService;
    private final ScreenwriterService screenwriterService;
    private final ImageService imageService;

    @Getter private final StringProperty title = new SimpleStringProperty("");
    @Getter private final StringProperty year = new SimpleStringProperty("");
    @Getter private final StringProperty duration = new SimpleStringProperty("");
    @Getter private final StringProperty directorInput = new SimpleStringProperty("");
    @Getter private final StringProperty screenwriterInput = new SimpleStringProperty("");
    @Getter private final ObjectProperty<Event<String>> message = new SimpleObjectProperty<>(new Event<>(""));

    @Getter private final ObjectProperty<String> selectedGenre = new SimpleObjectProperty<>("");
    @Getter private final ObjectProperty<String> selectedCategory = new SimpleObjectProperty<>("");
    @Getter private final ObjectProperty<Map<String, Object>> selectedAvailableActor = new SimpleObjectProperty<>();
    @Getter private final ObjectProperty<Map<String, Object>> selectedExistingActor = new SimpleObjectProperty<>();
    @Getter private final BooleanProperty shouldClose = new SimpleBooleanProperty(false);

    @Getter private final ObservableList<Map<String, Object>> availableActors = FXCollections.observableArrayList();
    @Getter private final ObservableList<Map<String, Object>> selectedActors = FXCollections.observableArrayList();
    @Getter private final ObservableList<String> genreOptions = FXCollections.observableArrayList();
    @Getter private final ObservableList<String> categoryOptions = FXCollections.observableArrayList();
    @Getter private final ObservableList<String> selectedImagePaths = FXCollections.observableArrayList();

    @Getter private final ICommand saveCommand;
    @Getter private final ICommand addActorCommand;
    @Getter private final ICommand removeActorCommand;

    private Integer currentMovieId = null;
    private boolean imagesCleared = false;

    public MovieFormVM(MovieService movieService, ActorService actorService,
                       DirectorService directorService, ScreenwriterService screenwriterService,
                       ImageService imageService) {
        this.movieService = movieService;
        this.actorService = actorService;
        this.directorService = directorService;
        this.screenwriterService = screenwriterService;
        this.imageService = imageService;

        this.saveCommand = this::save;
        this.addActorCommand = this::addActor;
        this.removeActorCommand = this::removeActor;

        initOptions();
    }

    private void initOptions() {
        genreOptions.setAll(Arrays.stream(Genre.values()).map(Enum::name).toList());
        categoryOptions.setAll(Arrays.stream(Category.values()).map(Enum::name).toList());
        loadAvailableActors();
    }

    private void loadAvailableActors() {
        availableActors.setAll(actorService.getActors().stream()
                .map(a -> Map.of("id", (Object) a.getActorId(), "name", a.getName()))
                .toList());
    }

    private void save() {
        try {
            if (isValid()) {
                Movie movie = (currentMovieId == null)
                        ? new Movie()
                        : movieService.getMovieById(currentMovieId);
                updateMovieObject(movie);
                Movie saved = movieService.saveMovie(movie);
                handleImages(saved);

                message.set(new Event<>("Saving was completed successfully!"));
                shouldClose.set(true);
            }
        } catch (Exception ex) {
            message.set(new Event<>(ex.getMessage()));
        }
    }

    private void addActor() {
        Map<String, Object> actor = selectedAvailableActor.get();
        if (actor != null && !selectedActors.contains(actor)) {
            selectedActors.add(actor);
        }
    }

    private void removeActor() {
        if (selectedExistingActor.get() != null) {
            selectedActors.remove(selectedExistingActor.get());
        }
    }

    public void resetFields() {
        currentMovieId = null;
        imagesCleared = false;
        title.set("");
        year.set("");
        duration.set("");
        directorInput.set("");
        screenwriterInput.set("");
        selectedGenre.set("");
        selectedCategory.set("");
        selectedActors.clear();
        selectedImagePaths.clear();
        message.set(new Event<>(""));

        shouldClose.set(false);
    }

    public void loadMovieData(Integer id) {
        resetFields();
        Movie m = movieService.getMovieById(id);
        if (m != null) {
            currentMovieId = id;
            title.set(m.getName());
            year.set(String.valueOf(m.getReleaseYear()));
            duration.set(String.valueOf(m.getDuration()));
            selectedGenre.set(m.getGenre().name());
            selectedCategory.set(m.getCategory().name());

            if (m.getDirector() != null) {
                directorInput.set(m.getDirector().getName());
            }
            if (m.getScreenwriter() != null) {
                screenwriterInput.set(m.getScreenwriter().getName());
            }

            selectedActors.setAll(
                    m.getActors().stream()
                            .map(a -> Map.of("id", (Object) a.getActorId(), "name", a.getName()))
                            .toList()
            );
        }
    }

    private boolean isValid() {
        if (title.get() == null || title.get().isBlank()) {
            message.set(new Event<>("Title is required."));
            return false;
        }
        if (year.get() == null || year.get().isBlank()) {
            message.set(new Event<>("Year is required."));
            return false;
        }
        try {
            Integer.parseInt(year.get());
        } catch (NumberFormatException e) {
            message.set(new Event<>("Year must be a number."));
            return false;
        }
        if (duration.get() != null && !duration.get().isBlank()) {
            try {
                Integer.parseInt(duration.get());
            } catch (NumberFormatException e) {
                message.set(new Event<>("Duration must be a number."));
                return false;
            }
        }
        return true;
    }

    private void updateMovieObject(Movie movie) {
        movie.setName(title.get());
        movie.setReleaseYear(Integer.parseInt(year.get()));
        movie.setDuration(Integer.parseInt(duration.get()));
        movie.setGenre(Genre.valueOf(selectedGenre.get()));
        movie.setCategory(Category.valueOf(selectedCategory.get()));
        movie.setDirector(directorService.getDirectorByName(directorInput.get()));
        movie.setScreenwriter(screenwriterService.getScreenwriterByName(screenwriterInput.get()));
        movie.setActors(
                selectedActors.stream()
                        .map(map -> actorService.getActorById((Integer) map.get("id")))
                        .toList()
        );
    }

    private void handleImages(Movie movie) {
        if (imagesCleared) imageService.deleteImagesByMovieId(movie.getMovieId());
        selectedImagePaths.forEach(path -> {
            Image img = new Image();
            img.setImgUrl(path);
            img.setMovie(movie);
            imageService.saveImage(img);
        });
    }

    public void setImages(List<File> files) {
        selectedImagePaths.clear();
        files.stream().limit(3).forEach(f -> selectedImagePaths.add(f.getAbsolutePath()));
    }

    public void clearImages() {
        selectedImagePaths.clear();
        imagesCleared = true;
    }
}