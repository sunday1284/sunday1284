package kr.or.ddit.pratice.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kr.or.ddit.pratice.vo.MemberVO;
import kr.or.ddit.util.DBUtil3;

public class MemberDaoImpl implements IMemberDao {
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;
	private static MemberDaoImpl dao;

	private MemberDaoImpl() {
	}

	public static MemberDaoImpl getInstance() {
		if (dao == null)
			dao = new MemberDaoImpl();
		return dao;
	}

	@Override
	public int insertMember(MemberVO memVo) {
		int cnt = 0;
		try {
			conn = DBUtil3.getConnection();
			String sql = "insert into mymember " + " (mem_id, mem_pass, mem_name, mem_tel, mem_addr) "
					+ " values( ?, ?, ?, ?, ? ) ";
			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, memVo.getMem_id());
			pstmt.setString(2, memVo.getMem_pass());
			pstmt.setString(3, memVo.getMem_name());
			pstmt.setString(4, memVo.getMem_tel());
			pstmt.setString(5, memVo.getMem_addr());

			cnt = pstmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			disConnect();
		}
		return cnt;
	}

	@Override
	public int deleteMember(String memId) {
		int cnt = 0;
		try {
			conn = DBUtil3.getConnection();
			String sql = "delete from mymember where mem_id = ? ";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, memId);

			cnt = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			disConnect();
		}

		return cnt;
	}

	@Override
	public int updateMember(MemberVO memVo) {
		int cnt = 0;
		try {
			conn = DBUtil3.getConnection();

			String sql = "update mymember set mem_pass = ?, mem_name = ?," + " mem_tel = ?, mem_addr = ? "
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
			disConnect();
		}
		return cnt;
	}

	@Override
	public List<MemberVO> getAllMember() {
		List<MemberVO> memberlist = null;
		
		try {
			conn = DBUtil3.getConnection();
			String sql = "select * from mymember";
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			//리스트 객체 생성
			memberlist = new ArrayList<MemberVO>();
 			while(rs.next()) {
				if(memberlist == null) memberlist = new ArrayList<MemberVO>();
				
				//vo객체 생성
				MemberVO memVo = new MemberVO();
				memVo.setMem_id(rs.getString("mem_id"));
				memVo.setMem_pass(rs.getString("mem_pass"));
				memVo.setMem_name(rs.getString("mem_name"));
				memVo.setMem_tel(rs.getString("mem_tel"));
				memVo.setMem_addr(rs.getString("mem_addr"));
				
				//리스트에 추가함
				memberlist.add(memVo);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			disConnect();
		}
		
		return memberlist;
	}

	@Override
	public int getMemberCount(String memId) {
		int cnt = 0;
		
		try {
			conn = DBUtil3.getConnection();
			String sql = "select count(*) cnt from mymember "
					+ " where mem_id = ? ";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, memId);
			
			rs = pstmt.executeQuery();
			//대상이 단일이면 if를 씀
			if(rs.next()) {
				cnt = rs.getInt("cnt");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			disConnect();
		}
		return cnt;
	}

	@Override
	public int updateMember2(Map<String, String> paramMap) {
		int cnt = 0;
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
			disConnect();
		}
		return cnt;
	}

	// 사용했던 자원을 반환하는 메서드
	private void disConnect() {
		if (rs != null)
			try {
				rs.close();
			} catch (SQLException e) {
			}
		if (pstmt != null)
			try {
				pstmt.close();
			} catch (SQLException e) {
			}
		if (conn != null)
			try {
				conn.close();
			} catch (SQLException e) {
			}
	}

}
