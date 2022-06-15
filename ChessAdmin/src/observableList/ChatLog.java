package observableList;

public class ChatLog {
	private int seq;
	private String time;
	private String id;
	private String text;
	private String loc;
	
	public ChatLog(int seq, String time, String id, String text,String loc) {
		this.seq = seq;
		this.time = time;
		this.id = id;
		this.text = text;
		this.loc = loc;
	}
	
	public String getLoc() {
		return loc;
	}

	public void setLoc(String loc) {
		this.loc = loc;
	}

	public String ToString() {
		return Integer.toString(seq) +"\t"+ this.time+"\t\t"+this.id+"\t"+this.text;
	}

	public String getSeq() {
		return Integer.toString(seq);
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
