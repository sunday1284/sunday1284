package kr.or.ddit.member.service;

import java.util.List;
import java.util.Map;

import kr.or.ddit.member.vo.MemberVO;


public interface IMemberService {
	
	/**
	 * 매개변수로 받은 MemberVO 자료를 DB에 inset하는 메서드
	 * @param memVo insert할 자료가 저장된 MemberVO객체
	 * @return 작업 성공 : 1, 작업 실패 : 0
	 */
	public int insertMember(MemberVO memVo);
	
	/**
	 * 맴버코드를 받아와서 삭제하는 메서드
	 * @param memVo delete할 memId 변수
	 * @return 작업 성공 : 1, 작업 실패 : 0
	 */
	public int deleteMember(String memId);
	
	/**
	 * 매개변수 MemberVO 자료 -> 수정하는 메서드
	 * @param memVo update할 자료가 저장된 MemberVO객체
	 * @return 작업 성공 : 1, 작업  실패 : 0
	 */
	public int updateMember(MemberVO memVo);
	
	
	/**
	 * 리스트로 받아온 MemberVO에 있는 데이터를 전체 출력
	 * @return MemberVO객체가 저장된 List객체
	 */
	public List<MemberVO> getAllMember();
	/**
	 * 회원ID를 매개변수로 받아서 해당 회원ID의 개수를 반환하는 메서드
	 * @return  작업할 회원 id로 반환
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
