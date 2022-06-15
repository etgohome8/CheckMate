package observableList;

public class AdminList {
	private int no;
	private String id;
	private String join;
	private String permit;
	
	public AdminList(int no, String id, String join, String permit) {
		this.no = no;
		this.id = id;
		this.join = join;
		this.permit = permit;
	}
	
	public String getNo() {
		return Integer.toString(no);
	}
	public void setNo(int no) {
		this.no = no;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getJoin() {
		return join;
	}
	public void setJoin(String join) {
		this.join = join;
	}
	public String getPermit() {
		if(permit.equals("y")) {
			return "Y";
		}else {return "N";}
	}
	public void setPermit(String permit) {
		this.permit = permit;
	}
}
