package kr.or.ddit.book.service;

import java.util.List;

import kr.or.ddit.book.dao.BookDaoImpl;
import kr.or.ddit.book.dao.IBookDao;
import kr.or.ddit.book.vo.BookVO;

public class BookServiceImpl implements IBookService {
	private IBookDao dao;
	
	public BookServiceImpl() {
		dao = BookDaoImpl.getInstance(); //DAO객체 생성
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
