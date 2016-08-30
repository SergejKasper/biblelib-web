package me.sergejkasper.bibelbibliothek.web.websocket.dto;

import me.sergejkasper.bibelbibliothek.domain.Borrower;

/**
 * DTO for storing a user's activity.
 */
public class InteractionDTO {

    private String isbn;
    private String cover;
    private Action action;
    private Borrower borrower;

    public InteractionDTO(String isbn, Action action, String cover){
        this.isbn = isbn;
        this.action = action;
        this.cover = cover;
    }

    public InteractionDTO(String isbn, Action action, Borrower borrower){
        this.isbn = isbn;
        this.action = action;
        this.borrower = borrower;
    }

    public InteractionDTO(){

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

    public Borrower getBorrower() {
        return borrower;
    }

    public void setBorrower(Borrower borrower) {
        this.borrower = borrower;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    @Override
    public String toString() {
        return "InteractionDTO{" +
            "isbn='" + isbn + '\'' +
            ", action='" + action + '\'' +
            ", borrower='" + borrower + '\'' +
            ", cover='" + cover + '\'' +
            '}';
    }

    public enum Action {
        NEW,BORROW,RETURN
    }

}
