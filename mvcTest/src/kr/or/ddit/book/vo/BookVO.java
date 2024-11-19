package kr.or.ddit.book.vo;

public class BookVO {
	public String book_id;
	public String book_title;
	public String book_author;
	public String book_loan;
	public String book_date;
	
	public String getBook_id() {
		return book_id;
	}
	public void setBook_id(String book_id) {
		this.book_id = book_id;
	}
	public String getBook_title() {
		return book_title;
	}
	public void setBook_title(String book_title) {
		this.book_title = book_title;
	}
	public String getBook_author() {
		return book_author;
	}
	public void setBook_author(String book_author) {
		this.book_author = book_author;
	}
	public String getBook_loan() {
		return book_loan;
	}
	public void setBook_loan(String book_loan) {
		this.book_loan = book_loan;
	}
	public String getBook_date() {
		return book_date;
	}
	public void setBook_date(String book_date) {
		this.book_date = book_date;
	}
	@Override
	public String toString() {
		return "BookVO [book_id=" + book_id + ", book_title=" + book_title + ", book_author=" + book_author
				+ ", book_loan=" + book_loan + ", book_date=" + book_date + "]";
	}
	
	
}
