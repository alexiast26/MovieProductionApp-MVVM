package com.test.movie_app_mvvm.ViewModel;

public class Event<T> {
    private T content;
    private boolean hasBeenHandled = false;

    public Event(T content) {
        this.content = content;
    }

    public T getContentIfNotHandled() {
        if (hasBeenHandled || content == null || content.toString().isEmpty()) {
            return null;
        } else {
            hasBeenHandled = true;
            return content;
        }
    }
}
