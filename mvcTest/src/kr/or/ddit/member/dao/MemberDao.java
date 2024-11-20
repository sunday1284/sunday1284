package kr.or.ddit.member.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kr.or.ddit.member.vo.MemberVO;
import kr.or.ddit.util.DBUtil3;


/**
 * DAO객체 - 실제 DB와 연결해서 SQL문을 수행하여 결과를 작성해서 
 * 			Service에게 전달하는 역할을 수행하는 객체
 * 
 * 		- 이 클래스의 메서드 하나가 DB와 관련된 작업 1개를 수행하도록 작성한다.
 */
public class MemberDao implements IMemberDao { 
	
	//1.참조값
	private static MemberDao dao;
	
	//2.생성자 private
	private MemberDao() {}
	
	//3.getInstance생성
	public static MemberDao getInstance() {
		if (dao == null) dao = new MemberDao();
		return dao;
	}
	
	/**
	 * insert할 데이터가 저장된 MemberVO객체를 매개변수로 받아서
	 * 해당 자료를 DB에 insert하는 메서드
	 *  
	 * @param memVo DB에 insert할 자료가 저장된 MemberVO객체
	 * @return 작업 성공 : 1, 작업 실패 : 0
	 */
	public int insertMember(MemberVO memVo) {
		int cnt = 0;		// 반환값이 저장될 변수
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = DBUtil3.getConnection();
			
			String sql = "insert into mymember "
					+ " (mem_id, mem_pass, mem_name, mem_tel, mem_addr) "
					+ " values( ?, ?, ?, ?, ? ) ";
			pstmt = conn.prepareStatement(sql);
			
			// 매개변수로 받은 VO객체에서 자료를 꺼내와 물음표 자라에 셋팅한다.
			pstmt.setString(1, memVo.getMem_id());
			pstmt.setString(2, memVo.getMem_pass());
			pstmt.setString(3, memVo.getMem_name());
			pstmt.setString(4, memVo.getMem_tel());
			pstmt.setString(5, memVo.getMem_addr());
			
			cnt = pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(pstmt!=null) try { pstmt.close(); }catch(SQLException e) {}
			if(conn!=null) try { conn.close(); }catch(SQLException e) {}
		}
		
		return cnt;
	}
	
	/**
	 * 회원ID를 매개변수로 받아서 해당 회원 정보를 삭제하는 메서드
	 * 
	 * @param memId 삭제할 회원ID
	 * @return 작업 성공 : 1, 작업 실패 : 0
	 */
	public int deleteMember(String memId) {
		int cnt = 0;		// 반환값이 저장될 변수
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = DBUtil3.getConnection();
			
			String sql = "delete from mymember where mem_id = ? ";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, memId);
			
			cnt = pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(pstmt!=null) try { pstmt.close(); }catch(SQLException e) {}
			if(conn!=null) try { conn.close(); }catch(SQLException e) {}
		}
		
		return cnt;
	}
	
	/**
	 * 수정할 자료가 저장될 MemberVO객체를 매개변수로 받아서
	 * 해당 자료를 update하는 메서드
	 * 
	 * @param memVo 수정할 자료가 저장된 MemberVO객체
	 * @return 작업 성공 : 1, 작업 실패 : 0
	 */
	public int updateMember(MemberVO memVo) {
		int cnt = 0;		// 반환값이 저장될 변수
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = DBUtil3.getConnection();
			
			String sql = "update mymember set mem_pass = ?, mem_name = ?,"
					+ " mem_tel = ?, mem_addr = ? "
					+ " where mem_id = ? ";
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, memVo.getMem_pass());
			pstmt.setString(2, memVo.getMem_name());
			pstmt.setString(3, memVo.getMem_tel());
			pstmt.setString(4, memVo.getMem_addr());
			pstmt.setString(5, memVo.getMem_id());
			
			cnt = pstmt.executeUpdate();
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(pstmt!=null) try { pstmt.close(); }catch(SQLException e) {}
			if(conn!=null) try { conn.close(); }catch(SQLException e) {}
		}
		
		return cnt;		
	}
	
	
	/**
	 * DB의 전체 회원 정보를 가져와서 List에 담아서 반환하는 메서드
	 * 
	 * @return MemberVO객체가 저장된 List객체
	 */
	public List<MemberVO> getAllMember(){
		List<MemberVO> memList = null;		// 반환값이 저장될 변수
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = DBUtil3.getConnection();
			
			String sql = "select * from mymember";
			
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			
			// List객체 생성
			memList = new ArrayList<MemberVO>();
			
			while(rs.next()) {
				// 1개의 레코드가 저장될 VO객체 생성
				MemberVO memVo = new MemberVO();
				
				// VO객체에 DB에서 가져온 자료를 저장한다.
				memVo.setMem_id(rs.getString("mem_id"));
				memVo.setMem_pass(rs.getString("mem_pass"));
				memVo.setMem_name(rs.getString("mem_name"));
				memVo.setMem_tel(rs.getString("mem_tel"));
				memVo.setMem_addr(rs.getString("mem_addr"));
				
				// DB에서 가져온 데이터가 저장된 VO객체를 List에 추가한다.
				memList.add(memVo);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(rs!=null) try { rs.close(); }catch(SQLException e) {}
			if(pstmt!=null) try { pstmt.close(); }catch(SQLException e) {}
			if(conn!=null) try { conn.close(); }catch(SQLException e) {}
		}
		
		return memList;
	}
	
	/**
	 * 회원ID를 매개변수로 받아서 해당 회원ID의 개수를 반환하는 메서드
	 * 
	 * @param memId 검색할 회원ID
	 * @return 검색된 회원ID의 갯수
	 */
	public int getMemberCount(String memId) {
		int count = 0;   	// 반환값이 저장될 변수
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = DBUtil3.getConnection();
			String sql = "select count(*) cnt from mymember "
					+ " where mem_id = ? ";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, memId);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				count = rs.getInt("cnt");
			}
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(rs!=null) try { rs.close(); }catch(SQLException e) {}
			if(pstmt!=null) try { pstmt.close(); }catch(SQLException e) {}
			if(conn!=null) try { conn.close(); }catch(SQLException e) {}
		}
		return count;
	}
	
	/**
	 * 수정할 정보가 저장된 Map 데이터를 파라미터로 받아서 원하는 컬럼을 수정하는 메서드
	 *  key값 정보 ==> 회원ID(MEMID), 수정할컬럼명(FIELD), 새로운데이터(NEWDATA)
	 * 
	 * @param paramMap 회원ID, 수정할컬럼명, 새로운데이터가 저장된 Map 객체
	 * @return 작업 성공 : 1, 작업 실패 : 0
	 */
	public int updateMember2(Map<String, String> paramMap ) {
		int cnt = 0; 	// 반환값이 저장될 변수
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = DBUtil3.getConnection();
			
			String sql = "update mymember set " + paramMap.get("FIELD")	+ " = ? "
					+ " where mem_id = ? ";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, paramMap.get("NEWDATA"));
			pstmt.setString(2, paramMap.get("MEMID"));
			
			cnt = pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(pstmt!=null) try { pstmt.close(); }catch(SQLException e) {}
			if(conn!=null) try { conn.close(); }catch(SQLException e) {}
		}
		
		return cnt;
	}
	
	
}






