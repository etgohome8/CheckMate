package observableList;

public class Gamelog {
	private int seq;
	private String white;
	private String black;
	private String win;
	private String date;
	
	public Gamelog(int seq, String white, String black, String win, String date) {
		this.seq = seq;
		this.white = white;
		this.black = black;
		this.win = win;
		this.date = date;
	}

	public String getSeq() {
		return Integer.toString(seq);
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public String getWhite() {
		return white;
	}

	public void setWhite(String white) {
		this.white = white;
	}

	public String getBlack() {
		return black;
	}

	public void setBlack(String black) {
		this.black = black;
	}

	public String getWin() {
		if (win.equals("w")) {
			return "W";
		}else if (win.equals("b")) {
			return "B";
		}else if (win.equals("p")) {
			return "P";
		}else {
			return "D";
		}
	}

	public void setWin(String win) {
		this.win = win;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}


}
