package book;

public class BookAuthor {
    private int bookId, authorId;

    public BookAuthor (int bookId, int authorId){
        this.bookId=bookId;
        this.authorId=authorId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }
}
