package kr.or.ddit.book.controller;

import java.util.List;
import java.util.Scanner;

import kr.or.ddit.book.service.BookServiceImpl;
import kr.or.ddit.book.service.IBookService;
import kr.or.ddit.book.vo.BookVO;

public class BookController {
	private IBookService service; // Service 객체 변수 선언

	private Scanner scan;

	public BookController() {
//		service = new BookServiceImpl(); // Service 객체 생성
		service = BookServiceImpl.getInstance();
		scan = new Scanner(System.in);
	}

	public static void main(String[] args) {
		new BookController().bookStart();
	}

	// 시작 메서드
	public void bookStart() {
		while (true) {
			int choice = displayMenu();
			switch (choice) {
			case 1: // 등록
				bookInsert();
				break;
			case 2: // 수정
				bookUpdate();
				break;
			case 3: // 삭제
				bookDelete();
				break;
			case 4: // 검색
				bookSearch();
				break;
			case 5: // 전체 출력
				bookDisplayAll();
				break;
			case 0: // 종료
				System.out.println();
				System.out.println("도서관리 프로그램을 종료합니다..");
				return;
			default:
				System.out.println();
				System.out.println("작업 번호를 잘못 입력하였습니다!!");
				System.out.println("다시 입력하세요!!");
			}
		}
	}

	private void bookDisplayAll() {
		// 전체 자료 가져오기
		List<BookVO> bookList = service.bookDisplayAll();

		System.out.println();
		System.out.println("================================================");
		System.out.println(" 도서번호     도서제목    지은이     대출가능여부   등록날짜");
		System.out.println("================================================");
		
		// 컨트롤러 약한 부분**
		if (bookList == null || bookList.size() == 0) {
			System.out.println("\t\t 도서 정보가 하나도 없습니다...");
		} else {
			for (BookVO bookVo : bookList) {
				String bookId = bookVo.getBook_id();
				String bookTitle = bookVo.getBook_title();
				String bookAuthor = bookVo.getBook_author();
				String bookLoan = bookVo.getBook_loan();
				String bookDate = bookVo.getBook_date();

				System.out.println(bookId + "  " + bookTitle + "     " + "     " + bookAuthor + "    " + bookLoan
						+ "     " + bookDate);
			}
		}
		System.out.println("================================================");
	}

	// 검색 메서드
	private void bookSearch() {
		scan.nextLine(); // 입력 버퍼 비우기

		System.out.println();
		System.out.println("검색할 도서 정보를 입력하세요...");
		System.out.print("도서제목 입력 >> ");
		String bookTitle = scan.nextLine();

		// 검색 결과를 받아옴
		List<BookVO> bookList = service.bookSearch(bookTitle);

		System.out.println("    	=== 검 색 결 과 ===");
		System.out.println("---------------------------------------------");
		// 컨트롤러 약한 부분**
		if (bookList == null || bookList.size() == 0) {
			System.out.println("\t\t검색된 자료가 하나도 없습니다...");
			System.out.println("---------------------------------------------");
		} else { // 검색된 데이터가 있을 때...
			for (BookVO bookVo : bookList) {
				System.out.println("도서 번호 : " + bookVo.getBook_id());
				System.out.println("제    목 : " + bookVo.getBook_title());
				System.out.println("지 은 이 : " + bookVo.getBook_author());
				System.out.println("대출가능여부 : " + bookVo.getBook_loan());
				System.out.println("등록 날짜 : " + bookVo.getBook_date());
				System.out.println("---------------------------------------------");

			}
		}

	}

	// 삭제 메서드
	private void bookDelete() {
		System.out.println();
		System.out.println("삭제할 도서 정보를 입력하세요...");
		System.out.print("도서번호 입력 >> ");
		String bookId = scan.next();

		int count = service.getBookIdCount(bookId);

		if (count == 0) {
			System.out.println("도서번호가 " + bookId + "인 도서정보는 없습니다...");
			System.out.println("삭제 작업을 마칩니다...");
			return;
		}

		// 위에서 선언했으므로 따로 VO 객체를 안만들어도 된다.
		int cnt = service.bookDelete(bookId);
		if (cnt > 0) {
			System.out.println("삭제 작업 성공!!!");
		} else {
			System.out.println("삭제 작업 실패~~~");
		}
	}

	// 수정
	private void bookUpdate() {
		System.out.println();
		System.out.println("수정할 도서정보를 입력하세요...");
		System.out.print("도서번호 입력 >> ");
		String bookId = scan.next();

		int count = service.getBookIdCount(bookId);

		if (count == 0) {
			System.out.println("도서번호가 " + bookId + "인 도서정보는 없습니다...");
			System.out.println("수정 작업을 마칩니다...");
			return;
		}

		scan.nextLine(); // 버퍼 비우기는 한번만 해준다..**

		System.out.print("새로운 도서 제목 >> ");
		String newTitle = scan.nextLine();

		System.out.print("새로운 지은이 >> ");
		String newAuthor = scan.nextLine();

		System.out.print("새로운 대출가능 여부(Y/N) >> ");
		String newLoan = scan.next();
		newLoan = newLoan.toUpperCase(); // 대문자로 변환

		// 입력 받은 데이터를 bookVo객체에 저장
		BookVO bookVo = new BookVO();
		bookVo.setBook_title(newTitle);
		bookVo.setBook_author(newAuthor);
		bookVo.setBook_loan(newLoan);
		bookVo.setBook_id(bookId);

		// Service로 VO객체를 넘겨주고 Update를 실행한다.
		int cnt = service.bookUpdate(bookVo);
		if (cnt > 0) {
			System.out.println("도서 정보 수정 성공!!!");
		} else {
			System.out.println("도서 정보 수정 실패!!!");
		}

	}

	// 등록
	private void bookInsert() {
		System.out.println();
		System.out.println("추가할 도서 정보를 입력하세요..");

		int count = 0;
		String bookId = null; // 도서 번호가 저장될 변수

		do {
			System.out.print("도서 번호 입력 >> ");
			bookId = scan.next();

			// 서비스에 있는 Count메서드를 가져온다.
			count = service.getBookIdCount(bookId);

			if (count > 0) {
				System.out.println("도서번호 " + bookId + "는(은) 이미 등록된 번호입니다.");
				System.out.println("다른 도서번호를 입력하세요...");
				System.out.println();
			}
		} while (count > 0);

		scan.nextLine(); // 입력 버퍼 비우기

		System.out.print("도서 제목 입력 >> ");
		String title = scan.nextLine(); // 띄워쓰기도 써야되서 Line

		System.out.print("지은이 입력 >> ");
		String author = scan.nextLine();

		// 입력 받은 자료들을 VO 객체에 저장한다.
		BookVO bookVo = new BookVO();
		bookVo.setBook_id(bookId);
		bookVo.setBook_title(title);
		bookVo.setBook_author(author);

		// Service 객체의 insert를 처리하는 메서드를 호출한다.
		// 이 때 입력한 자료가 저장된 VO객체를 매개변수로 전달한다.
		int cnt = service.bookInsert(bookVo);

		if (cnt > 0) {
			System.out.println("도서 정보 등록 성공!!!");
		} else {
			System.out.println("도서 정보 등록 실패~~~");
		}
	}

	// 메뉴를 출력하고 작업번호를 입력받아 반환하는 메서드
	private int displayMenu() {
		System.out.println();
		System.out.println("-------------------------------");
		System.out.println("	 1.도서 정보 등록");
		System.out.println("	 2.도서 정보 수정");
		System.out.println("	 3.도서 정보 삭제");
		System.out.println("	 4.도서 정보 검색");
		System.out.println("	 5.전체 도서 정보 출력");
		System.out.println("	 0.프로그램 종료");
		System.out.println("-------------------------------");
		System.out.print("원하는 작업 선택 >> ");
		return scan.nextInt();
	}
}
