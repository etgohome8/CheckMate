package observableList;


public class UserList {
	private int idx;
	private String id;
	private String nick;
	private String gender;
	private int age;
	private int win;
	private int lose;
	private double rate;
	private String joindate;
	private String isBlack;
	
	public UserList(int idx, String id, String nick, String gender, int age, int win, int lose, double rate, String joindate, String isBlack) {
		this.age = age;
		this.gender = gender;
		this.id = id;
		this.idx = idx;
		this.isBlack = isBlack;
		this.joindate = joindate;
		this.lose = lose;
		this.win = win;
		this.nick = nick;
		this.rate = rate;
	}
	
	public String ToString() {
		char[] datee = joindate.toCharArray();
		String date = new String(datee,0,10);
		return idx+"\t"+id+"\t"+nick+"\t"+gender+"\t"+age+"\t"+win+"\t"+lose+"\t"+rate+"\t"+date+"\t"+isBlack;
	}
	
	public String getIdx() {
		return Integer.toString(idx);
	}
	public void setIdx(int idx) {
		this.idx = idx;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getAge() {
		return Integer.toString(age);
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getWin() {
		return Integer.toString(win);
	}
	public void setWin(int win) {
		this.win = win;
	}
	public String getLose() {
		return Integer.toString(lose);
	}
	public void setLose(int lose) {
		this.lose = lose;
	}
	public String getRate() {
		return String.format("%.2f", rate);
	}
	public void setRate(double rate) {
		this.rate = rate;
	}
	public String getJoindate() {
		return joindate;
	}
	public void setJoindate(String joindate) {
		this.joindate = joindate;
	}
	public String getIsBlack() {
		if(isBlack.equals("f")) return "F";
		else{return "T";}
	}
	public void setIsBlack(String isBlack) {
		this.isBlack = isBlack;
	}
	
}
