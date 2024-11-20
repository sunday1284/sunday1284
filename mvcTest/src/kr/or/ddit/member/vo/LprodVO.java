package kr.or.ddit.member.vo;

public class LprodVO {
	public int lprod_id;
	public String lprod_gu;
	public String lprod_nm;
	
	public int getLprod_id() {
		return lprod_id;
	}
	public void setLprod_id(int lprod_id) {
		this.lprod_id = lprod_id;
	}
	public String getLprod_gu() {
		return lprod_gu;
	}
	public void setLprod_gu(String lprod_gu) {
		this.lprod_gu = lprod_gu;
	}
	public String getLprod_nm() {
		return lprod_nm;
	}
	public void setLprod_nm(String lprod_nm) {
		this.lprod_nm = lprod_nm;
	}
	
	@Override
	public String toString() {
		return "LprodVO [lprod_id=" + lprod_id + ", lprod_gu=" + lprod_gu + ", lprod_nm=" + lprod_nm + "]";
	}
	
	
}
