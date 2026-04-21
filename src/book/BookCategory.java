package book;

public class BookCategory {
    private int bookId, categoryId;

    public BookCategory(int bookId, int categoryId) {
        this.bookId = bookId;
        this.categoryId = categoryId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
}
