package kr.or.ddit.member.dao;

import java.util.List;
import java.util.Map;

import kr.or.ddit.member.vo.MemberVO;


public interface IMemberDao {
	/**
	 * insert할 데이터가 저장된 MemberVO객체를 매개변수로 받아서
	 * 해당 자료를 DB에 insert하는 메서드
	 *  
	 * @param memVo DB에 insert할 자료가 저장된 MemberVO객체
	 * @return 작업 성공 : 1, 작업 실패 : 0
	 */
	public int insertMember(MemberVO memVo);
	
	/**
	 * 회원ID를 매개변수로 받아서 해당 회원 정보를 삭제하는 메서드
	 * 
	 * @param memId 삭제할 회원ID
	 * @return 작업 성공 : 1, 작업 실패 : 0
	 */
	public int deleteMember(String memId);
	
	/**
	 * 수정할 자료가 저장될 MemberVO객체를 매개변수로 받아서
	 * 해당 자료를 update하는 메서드
	 * 
	 * @param memVo 수정할 자료가 저장된 MemberVO객체
	 * @return 작업 성공 : 1, 작업 실패 : 0
	 */
	public int updateMember(MemberVO memVo);
	
	
	/**
	 * DB의 전체 회원 정보를 가져와서 List에 담아서 반환하는 메서드
	 * 
	 * @return MemberVO객체가 저장된 List객체
	 */
	public List<MemberVO> getAllMember();
	
	/**
	 * 회원ID를 매개변수로 받아서 해당 회원ID의 개수를 반환하는 메서드
	 * 
	 * @param memId 검색할 회원ID
	 * @return 검색된 회원ID의 갯수
	 */
	public int getMemberCount(String memId);
	
	
	/**
	 * * 수정할 정보가 저장된 Map 데이터를 파라미터로 받아서 원하는 컬럼을 수정하는 메서드
	 *  key값 정보 ==> 회원ID(MEMID), 수정할컬럼명(FIELD), 새로운데이터(NEWDATA)
	 * 
	 * @param paramMap 회원ID, 수정할컬럼명, 새로운데이터가 저장된 Map 객체
	 * @return 작업 성공 : 1, 작업 실패 : 0
	 */
	public int updateMember2(Map<String, String> paramMap);
	
	
}
