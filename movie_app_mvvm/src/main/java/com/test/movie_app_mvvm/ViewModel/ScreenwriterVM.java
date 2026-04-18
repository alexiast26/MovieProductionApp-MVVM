package com.test.movie_app_mvvm.ViewModel;

import com.test.movie_app_mvvm.Model.Screenwriter;
import com.test.movie_app_mvvm.Model.Service.ScreenwriterService;
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
public class ScreenwriterVM {
    private final ScreenwriterService screenwriterService;

    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty age = new SimpleStringProperty("");
    private final StringProperty gender = new SimpleStringProperty("");

    private final ObjectProperty<Event<String>> message = new SimpleObjectProperty<>(new Event<>(""));

    @Getter
    private final ObservableList<Map<String, Object>> screenwritersList = FXCollections.observableArrayList();
    private final ObjectProperty<Map<String, Object>> selectedScreenwriter = new SimpleObjectProperty<>();

    @Getter private final ICommand saveCommand;
    @Getter private final ICommand deleteCommand;
    @Getter private final ICommand updateUIFieldsCommand;

    private Integer currentScreenwriterId = null;


    public ScreenwriterVM(ScreenwriterService screenwriterService) {
        this.screenwriterService = screenwriterService;

        this.saveCommand = this::executeSave;
        this.deleteCommand = this::executeDelete;
        this.updateUIFieldsCommand = this::executeUpdateUIFields;

        selectedScreenwriter.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                this.currentScreenwriterId = (Integer) newValue.get("id");
            }
        });

        refreshTable();
    }

    public void executeSave() {
        try {
            Screenwriter screenwriter = (currentScreenwriterId == null)
                    ? new Screenwriter()
                    : screenwriterService.getScreenwriterById(currentScreenwriterId);

            if (screenwriter != null) {
                screenwriter.setName(name.get());
                screenwriter.setAge(Integer.parseInt(age.get()));
                screenwriter.setGender(gender.get());

                if (currentScreenwriterId == null) {
                    screenwriterService.saveScreenwriter(screenwriter);
                    message.set(new Event<>("Screenwriter saved successfully"));
                } else {
                    screenwriterService.updateScreenwriter(screenwriter);
                    message.set(new Event<>("Screenwriter updated successfully"));
                }

                resetState();
                refreshTable();
            }
        } catch (Exception e) {
            message.set(new Event<>("Error saving screenwriter: " + e.getMessage()));
        }
    }

    public void executeDelete() {
        try {
            Map<String, Object> selected = selectedScreenwriter.get();
            if (selected == null) {
                message.set(new Event<>("Please select a screenwriter"));
                return;
            }
            Screenwriter screenwriter = screenwriterService.getScreenwriterById((Integer) selected.get("id"));
            screenwriterService.deleteScreenwriter(screenwriter);
            message.set(new Event<>("Screenwriter deleted successfully"));
            // FIX 4: resetState() was missing after delete, leaving stale UI field values.
            // C# PersonVM clears CurrentPerson and SearchedID after every mutating operation.
            resetState();
            refreshTable();
        } catch (Exception e) {
            message.set(new Event<>("Error deleting screenwriter: " + e.getMessage()));
        }
    }

    public void executeUpdateUIFields() {
        Map<String, Object> selected = selectedScreenwriter.get();
        if (selected != null) {
            Screenwriter screenwriter = screenwriterService.getScreenwriterById((Integer) selected.get("id"));
            if (screenwriter != null) {
                name.set(screenwriter.getName());
                age.set(String.valueOf(screenwriter.getAge()));
                gender.set(screenwriter.getGender());
            }
        } else {
            message.set(new Event<>("Please select a screenwriter to update"));
        }
    }

    private void resetState() {
        currentScreenwriterId = null;
        name.set("");
        age.set("");
        gender.set("");
    }

    private void refreshTable() {
        List<Map<String, Object>> mapped = screenwriterService.getScreenwriters().stream().map(s -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", s.getScreenwriterId());
            map.put("name", s.getName());
            map.put("age", s.getAge());
            map.put("gender", s.getGender());
            return map;
        }).toList();
        screenwritersList.setAll(mapped);
    }

    public StringProperty nameProperty() { return name; }
    public StringProperty ageProperty() { return age; }
    public StringProperty genderProperty() { return gender; }
    public ObjectProperty<Event<String>> messageProperty() { return message; }
    public ObjectProperty<Map<String, Object>> selectedScreenwriterProperty() { return selectedScreenwriter; }
}