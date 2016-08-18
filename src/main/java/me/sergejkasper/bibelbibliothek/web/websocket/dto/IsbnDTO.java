package me.sergejkasper.bibelbibliothek.web.websocket.dto;

/**
 * DTO for storing a user's activity.
 */
public class IsbnDTO {

    private String isbn;
    private Action action;

    public IsbnDTO(String isbn, Action action){
        this.isbn = isbn;
        this.action = action;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }


    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }


    public String getIsbn() {
        return isbn;
    }

    @Override
    public String toString() {
        return "ActivityDTO{" +
            "isbn='" + isbn + '\'' +
            ", action='" + action + '\'' +
            '}';
    }

    public enum Action {
        NEW,BORROW,RETURN
    }

}
