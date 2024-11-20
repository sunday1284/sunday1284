package kr.or.ddit.pratice.service;

import java.util.List;
import java.util.Map;

import kr.or.ddit.pratice.dao.MemberDaoImpl;
import kr.or.ddit.pratice.vo.MemberVO;

public class MemberServiceImpl implements IMemberService{
	private static MemberDaoImpl dao;
	
	public MemberServiceImpl() {
		dao = MemberDaoImpl.getInstance();
	}
	
	
	@Override
	public int insertMember(MemberVO memVo) {
		
		return dao.insertMember(memVo);
	}

	@Override
	public int deleteMember(String memId) {
		return dao.deleteMember(memId);
	}

	@Override
	public int updateMember(MemberVO memVo) {
		return dao.updateMember(memVo);
	}

	@Override
	public List<MemberVO> getAllMember() {
		return dao.getAllMember();
	}

	@Override
	public int getMemberCount(String memId) {
		return dao.getMemberCount(memId);
	}

	@Override
	public int updateMember2(Map<String, String> paramMap) {
		return dao.updateMember2(paramMap);
	}

}
