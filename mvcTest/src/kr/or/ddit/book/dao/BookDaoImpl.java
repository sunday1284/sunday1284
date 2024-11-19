package kr.or.ddit.book.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import kr.or.ddit.book.vo.BookVO;
import kr.or.ddit.util.DBUtil3;

public class BookDaoImpl implements IBookDao {
	
	// 1번 
	private static BookDaoImpl dao;
	
	//2번 생성자의 접근 제한자를 private로 함
	private BookDaoImpl() {}
	
	// 3번 
	public static BookDaoImpl getInstance() {	
		if(dao==null) dao = new BookDaoImpl();
		
		return dao;
		
	}
	
	@Override
	public int bookInsert(BookVO bookVo) {
		int cnt = 0; 		//반환값이 저장될 변수
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = DBUtil3.getConnection();
			
			String sql = "INSERT INTO TB_BOOK "
					+ " (BOOK_ID, BOOK_TITLE, BOOK_AUTHOR, BOOK_LOAN, BOOK_DATE) "
					+ " VALUES(?, ?, ?, 'Y', SYSDATE) ";
			pstmt = conn.prepareStatement(sql);
			//?에 들어갈 값들
			pstmt.setString(1, bookVo.getBook_id());
			pstmt.setString(2, bookVo.getBook_title());
			pstmt.setString(3, bookVo.getBook_author());
			cnt = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			if(pstmt!=null)try {pstmt.close();}catch(SQLException e) {}
			if(conn!=null)try {conn.close();}catch(SQLException e) {}
		}
		
		return cnt;
	}

	@Override
	public int bookUpdate(BookVO bookVo) {
		int cnt = 0;
		
		//INSERT UPDATE DELETE문은 두개만 있으면 된다.
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = DBUtil3.getConnection();
			
			String sql = "UPDATE TB_BOOK SET BOOK_TITLE= ?, BOOK_AUTHOR = ?, "
					+ " BOOK_LOAN = ?, BOOK_DATE = SYSDATE "
					+ " WHERE BOOK_ID = ? ";
			
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, bookVo.getBook_title());
			pstmt.setString(2, bookVo.getBook_author());
			pstmt.setString(3, bookVo.getBook_loan());
			pstmt.setString(4, bookVo.getBook_id());
			cnt = pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			if(pstmt!=null)try {pstmt.close();}catch(SQLException e) {}
			if(conn!=null)try {conn.close();}catch(SQLException e) {}
		}
		
		return cnt;
	}

	@Override
	public int bookDelete(String bookId) {
		int cnt = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = DBUtil3.getConnection();
			
			String sql = "DELETE FROM TB_BOOK WHERE BOOK_ID = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, bookId);
			
			cnt = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			if(pstmt!=null)try {pstmt.close();}catch(SQLException e) {}
			if(conn!=null)try {conn.close();}catch(SQLException e) {}
		}
		
		return cnt;
	}

	@Override
	public List<BookVO> bookSearch(String bookTitle) {
		List<BookVO> bookList = null; //반환값이 저장될 변수
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = DBUtil3.getConnection();
			
			String sql = "SELECT * FROM TB_BOOK "
					+ " WHERE BOOK_TITLE LIKE '%' || ? || '%' ";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, bookTitle);
			
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				// List객체 생성
				if(bookList == null) bookList = new ArrayList<BookVO>();
				
				//VO객체 생성
				BookVO bookVo = new BookVO();
				
				// ResultSet객체에서 데이터를 꺼내와서 VO객체에 저장한다.
				bookVo.setBook_id(rs.getString("BOOK_ID"));
				bookVo.setBook_title(rs.getString("BOOK_TITLE"));
				bookVo.setBook_author(rs.getString("BOOK_AUTHOR"));
				bookVo.setBook_loan(rs.getString("BOOK_LOAN"));
				bookVo.setBook_date(rs.getString("BOOK_DATE"));
				
				// 데이터가 저장된 VO객체를 List에 추가
				bookList.add(bookVo);
				
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			if(pstmt!=null)try {pstmt.close();}catch(SQLException e) {}
			if(conn!=null)try {conn.close();}catch(SQLException e) {}
			if(rs!=null)try {rs.close();}catch(SQLException e) {}
		}
		return bookList;
		
	}

	@Override
	public List<BookVO> bookDisplayAll() {
		List<BookVO> bookList = null; //반환값이 저장될 변수
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = DBUtil3.getConnection();
			
			String sql = "SELECT * FROM TB_BOOK  ORDER BY BOOK_ID ASC ";
					
			pstmt = conn.prepareStatement(sql);
			
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				// List객체 생성
				if(bookList == null) bookList = new ArrayList<BookVO>();
				
				//VO객체 생성
				BookVO bookVo = new BookVO();
				
				// ResultSet객체에서 데이터를 꺼내와서 VO객체에 저장한다.
				bookVo.setBook_id(rs.getString("BOOK_ID"));
				bookVo.setBook_title(rs.getString("BOOK_TITLE"));
				bookVo.setBook_author(rs.getString("BOOK_AUTHOR"));
				bookVo.setBook_loan(rs.getString("BOOK_LOAN"));
				bookVo.setBook_date(rs.getString("BOOK_DATE"));
				
				// 데이터가 저장된 VO객체를 List에 추가
				bookList.add(bookVo);
				
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			if(pstmt!=null)try {pstmt.close();}catch(SQLException e) {}
			if(conn!=null)try {conn.close();}catch(SQLException e) {}
			if(rs!=null)try {rs.close();}catch(SQLException e) {}
		}
		return bookList;	
		
	}

	@Override
	public int getBookIdCount(String bookId) {
		int cnt = 0; 			//반환값이 저장될 변수 
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = DBUtil3.getConnection();
			
			String sql = "SELECT COUNT(*) CNT FROM TB_BOOK WHERE BOOK_ID = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, bookId);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				cnt = rs.getInt("CNT");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(rs!=null)try {rs.close();}catch(SQLException e) {}
			if(pstmt!=null)try {pstmt.close();}catch(SQLException e) {}
			if(conn!=null)try {conn.close();}catch(SQLException e) {}
		}
		
		return cnt;
	}
	
	
	
}
