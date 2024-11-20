package kr.or.ddit.pratice.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import kr.or.ddit.pratice.service.IMemberService;
import kr.or.ddit.pratice.service.MemberServiceImpl;
import kr.or.ddit.pratice.vo.MemberVO;


public class MemberController {
	private IMemberService service;
	private Scanner scan;
	
	public MemberController() {
		service = new MemberServiceImpl();
		scan = new Scanner(System.in);
	}
	public static void main(String[] args) {
		new MemberController().memberStart();
	}

	// 시작 메서드
	public void memberStart() {
		System.out.println("+++++++++++++++++++++++++++++++++");
		System.out.println("       회 원 관 리 프 로 그 램");
		System.out.println("+++++++++++++++++++++++++++++++++");
		System.out.println();

		while (true) {
			int choice = displayMenu();
			switch (choice) {
			case 1: // 추가
				insertMember();
				break;
			case 2: // 삭제
				deleteMember();
				break;
			case 3: // 수정 ==> 전체 항목 수정
				updateMember();
				break;
			case 4: // 전체 출력
				displayAllMember();
				break;
			case 5: // 수정 ==> 선택 항목 수정
				updateMember2();
				break;
			case 0:
				System.out.println("회원 관리 프로그램을 마칩니다.");
				return;
			default:
				System.out.println("작업 번호를 잘못 입력했습니다.");
				System.out.println("다시 입력하세요...");

			}
		}
	}

	private void updateMember2() {
		System.out.println();
		System.out.println("수정할 회원 정보를 입력하세요.");
		System.out.print("수정할 회원ID >> ");
		scan.nextLine();
		String memId = scan.nextLine();
		
		int count = service.getMemberCount(memId);
		if(count==0) {   // 없는 회원이면...
			System.out.println(memId + "는(은) 없는 회원ID 입니다.");
			System.out.println("수정 작업을 마칩니다...");
			return;
		}
		int num;	
		String updateField = null;		// 컬럼명이 저장될 변수
		String updateTitle = null;		// 새로운 값을 입력 받을 때
		
		do {
			System.out.println();
			System.out.println("수정할 항목을 선택하세요...");
			System.out.println("1.비밀번호  2.회원이름  3.전화번호  4.회원주소");
			System.out.println("--------------------------------------");
			System.out.print("수정할 항목 선택 >> ");
			num = scan.nextInt();
			
			switch(num) {
				case 1 : updateField = "mem_pass"; updateTitle = "비밀번호";
					break;
				case 2 : updateField = "mem_name"; updateTitle = "회원이름";
					break;
				case 3 : updateField = "mem_tel"; updateTitle = "전화번호";
					break;
				case 4 : updateField = "mem_addr"; updateTitle = "회원주소";
					break;
				default :
					System.out.println("수정 항목을 잘못 선택했습니다. 다시 선택하세요...");
			
			}
			
		}while (num<1 || num>4);
		
		System.out.print("새로운 " + updateTitle + " >> ");
		scan.nextLine();
		String newData = scan.nextLine();
		
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("MEMID", memId);
		map.put("FIELD", updateField);
		map.put("NEWDATA", newData);
		
		int cnt = service.updateMember2(map);
		
		
		if(cnt>0) {
			System.out.println("수정 작업 성공!!!");
		}else {
			System.out.println("수정 작업 실패~~~");
		}
		
	}

	private void displayAllMember() {
		List<MemberVO> memberlist  = service.getAllMember();
		System.out.println();
		System.out.println("-------------------------------------------------");
		System.out.println(" ID    비밀번호    이름    전화번호    주소");
		System.out.println("-------------------------------------------------");
		
		if(memberlist == null || memberlist.size() == 0) {
			System.out.println("회원 정보가 하나도 없습니다...");
		}else {
			for(MemberVO memVo : memberlist) {
				String memId = memVo.getMem_id();
				String memPass = memVo.getMem_pass();
				String memName = memVo.getMem_name();
				String memTel  = memVo.getMem_tel();
				String memAddr = memVo.getMem_addr();
				System.out.println(memId + "\t" + memPass + "\t" + memName
						+ "\t" + memTel + "\t" + memAddr);
			}
		}System.out.println("------------------------------------------------------");
	}

	private void updateMember() {
		System.out.println();
		System.out.println("수정할 회원 정보를 입력하세요.");
		scan.nextLine();
		System.out.print("수정할 회원ID >> ");
		String memId = scan.nextLine();
		
		int count = service.getMemberCount(memId);
		if(count==0) {   // 없는 회원이면...
			System.out.println(memId + "는(은) 없는 회원ID 입니다.");
			System.out.println("수정 작업을 마칩니다...");
			return;
		}
		
		System.out.println();
		System.out.println("수정할 내용을 입력하세요.");
		
		System.out.print("새로운 비밀번호 >> ");
		String newMemPass = scan.nextLine();
		System.out.print("새로운 회원이름 >> ");
		String newMemName = scan.nextLine();
		System.out.print("새로운 전화번호 >> ");
		String newMemTel = scan.nextLine();
		System.out.print("새로운 회원주소 >> ");
		String newMemAddr = scan.nextLine();
		
		MemberVO memVo = new MemberVO();
		memVo.setMem_id(memId);
		memVo.setMem_pass(newMemPass);
		memVo.setMem_name(newMemName);
		memVo.setMem_tel(newMemTel);
		memVo.setMem_addr(newMemAddr);
		
		int cnt = service.updateMember(memVo);
		
		if(cnt>0) {
			System.out.println(memId + " 회원 정보 수정 완료!!!");
		}else {
			System.out.println(memId + " 회원 정보 수정 실패~~~");
		}
		
	}

	private void deleteMember() {
		System.out.println();
		System.out.println("삭제할 회원 정보를 입력하세요.");
		System.out.print("삭제할 회원ID >> ");
		scan.nextLine();
		String memId = scan.nextLine();
		
		int count = service.getMemberCount(memId);
		
		if(count==0) {
			System.out.println(memId + "는(은) 없는 회원ID 입니다.");
			System.out.println("삭제 작업을 마칩니다...");
			return;
		}
		int cnt = service.deleteMember(memId);
		if(cnt>0) {
			System.out.println(memId + " 회원 정보 삭제 완료!!!");
		}else {
			System.out.println(memId + " 회원 정보 삭제 실패~~~");
		}

	}

	private void insertMember() {
		System.out.println();
		System.out.println("추가할 회원 정보를 입력하세요.");
		int count = 0;			// 회원ID의 개수가 저장될 변수
		String memId = null;	// 회원ID가 저장될 변수
		do {
			scan.nextLine(); //입력 버퍼 초기화
			System.out.print("회원ID >> ");
			memId = scan.nextLine();
			count = service.getMemberCount(memId);
			
			if(count>0) {
				System.out.println(memId + "는(은) 이미 등록된 회원ID 입니다.");
				System.out.println("다른 회원ID를 입력하세요...");
			}
			
		}while(count>0);
		
		System.out.print("비밀번호 >> ");
		String memPass = scan.nextLine();
		System.out.print("회원이름 >> ");
		String memName =  scan.nextLine();
		System.out.print("전화번호 >> ");
		String memTel = scan.nextLine();
		System.out.print("회원주소 >> ");
		String memAddr = scan.nextLine();
		
		//입력 받은 자료를 MemberVO객체에 저장
		MemberVO memVo = new MemberVO();
		memVo.setMem_id(memId);
		memVo.setMem_pass(memPass);
		memVo.setMem_name(memName);
		memVo.setMem_tel(memTel);
		memVo.setMem_addr(memAddr);
		
		int cnt = service.insertMember(memVo);
		if(cnt>0) {
			System.out.println("insert 작업 성공!!!");
		}else {
			System.out.println("insert 작업 실패~~~");
		}
	}

	private int displayMenu() {
			System.out.println();
			System.out.println("-------------------");
			System.out.println("  1. 자료 추가");
			System.out.println("  2. 자료 삭제");
			System.out.println("  3. 자료 전체 항목 수정");
			System.out.println("  4. 전체 자료 출력");
			System.out.println("  5. 자료 선택 항목 수정");
			System.out.println("  0. 작업 끝");
			System.out.println("-------------------");
			System.out.print("작업 번호 선택 >> ");
			return scan.nextInt();
	}
}
