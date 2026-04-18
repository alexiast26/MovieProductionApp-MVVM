package com.test.movie_app_mvvm.ViewModel;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import com.test.movie_app_mvvm.Model.Director;
import com.test.movie_app_mvvm.Model.Service.DirectorService;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DirectorVM {
    private final DirectorService directorService;

    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty age = new SimpleStringProperty("");
    private final StringProperty gender = new SimpleStringProperty("");

    private final ObjectProperty<Event<String>> message = new SimpleObjectProperty<>(new Event<>(""));

    @Getter private final ObservableList<Map<String, Object>> directorsList = FXCollections.observableArrayList();
    private final ObjectProperty<Map<String, Object>> selectedDirector = new SimpleObjectProperty<>();

    @Getter private final ICommand saveCommand;
    @Getter private final ICommand deleteCommand;
    @Getter private final ICommand updateUIFieldsCommand;

    private Integer currentDirectorId = null;

    public DirectorVM(DirectorService directorService) {
        this.directorService = directorService;

        this.saveCommand = this::executeSave;
        this.deleteCommand = this::executeDelete;
        this.updateUIFieldsCommand = this::executeUpdateUIFields;

        selectedDirector.addListener((obs, old, newValue) -> {
            if (newValue != null) {
                this.currentDirectorId = (Integer) newValue.get("id");
            }
        });

        refreshTable();
    }

    private void executeSave() {
        try {
            Director director = (currentDirectorId == null)
                    ? new Director()
                    : directorService.getDirectorById(currentDirectorId);

            if (director != null) {
                director.setName(name.get());
                director.setAge(Integer.parseInt(age.get()));
                director.setGender(gender.get());

                if (currentDirectorId == null) {
                    directorService.saveDirector(director);
                    message.set(new Event<>("Director saved successfully!"));
                } else {
                    directorService.updateDirector(director);
                    message.set(new Event<>("Director updated successfully!"));
                }

                resetState();
                refreshTable();
            }
        } catch (Exception e) {
            message.set(new Event<>("Error: " + e.getMessage()));
        }
    }

    private void executeDelete() {
        try {
            Map<String, Object> selected = selectedDirector.get();
            if (selected == null) {
                message.set(new Event<>("Please select a director first!"));
                return;
            }
            Director director = directorService.getDirectorById((Integer) selected.get("id"));
            directorService.deleteDirector(director);
            message.set(new Event<>("Director deleted!"));
            // FIX 2: resetState() was missing after delete, leaving stale ID and field
            // values in the UI. The C# PersonVM always resets CurrentPerson and SearchedID
            // after every successful mutating operation (add, update, delete).
            resetState();
            refreshTable();
        } catch (DataIntegrityViolationException e) {
            message.set(new Event<>("Cannot delete: existing references."));
        }
    }

    private void executeUpdateUIFields() {
        Map<String, Object> selected = selectedDirector.get();
        if (selected == null) {
            message.set(new Event<>("Select a director to modify."));
            return;
        }
        name.set(String.valueOf(selected.get("name")));
        age.set(String.valueOf(selected.get("age")));
        gender.set(String.valueOf(selected.get("gender")));
    }

    public void refreshTable() {
        List<Map<String, Object>> mapped = directorService.getDirectors().stream().map(d -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", d.getDirectorId());
            map.put("name", d.getName());
            map.put("age", d.getAge());
            map.put("gender", d.getGender());
            return map;
        }).toList();
        directorsList.setAll(mapped);
    }

    private void resetState() {
        currentDirectorId = null;
        name.set("");
        age.set("");
        gender.set("");
    }

    public StringProperty nameProperty() { return name; }
    public StringProperty ageProperty() { return age; }
    public StringProperty genderProperty() { return gender; }
    public ObjectProperty<Event<String>> messageProperty() { return message; }
    public ObjectProperty<Map<String, Object>> selectedDirectorProperty() { return selectedDirector; }
}