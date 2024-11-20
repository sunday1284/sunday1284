package kr.or.ddit.book.service;

import java.util.List;

import kr.or.ddit.book.dao.BookDaoImpl;
import kr.or.ddit.book.dao.IBookDao;
import kr.or.ddit.book.vo.BookVO;

public class BookServiceImpl implements IBookService {
	private IBookDao dao;
	
	//1번
	private static BookServiceImpl service;
	
	//2번
	private BookServiceImpl() {
		dao = BookDaoImpl.getInstance(); //DAO객체 생성
	}
	//3번
	public static BookServiceImpl getInstance() {
		if(service == null) service = new BookServiceImpl();
		return service;
	}
	
	
	@Override
	public int bookInsert(BookVO bookVo) {
		return dao.bookInsert(bookVo);
	}

	@Override
	public int bookUpdate(BookVO bookVo) {
		return dao.bookUpdate(bookVo);
	}

	@Override
	public int bookDelete(String bookId) {
		return dao.bookDelete(bookId);
	}

	@Override
	public List<BookVO> bookSearch(String bookTitle) {
		return dao.bookSearch(bookTitle);
	}

	@Override
	public List<BookVO> bookDisplayAll() {
		return dao.bookDisplayAll();
	}

	@Override
	public int getBookIdCount(String bookId) {
		return dao.getBookIdCount(bookId);
	}

}
