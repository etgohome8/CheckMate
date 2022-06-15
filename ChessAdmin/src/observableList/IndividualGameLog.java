package observableList;

public class IndividualGameLog {
	private String player;
	private String destination;
	private String startt;

	public IndividualGameLog(String player, String startt, String destination) {
		this.player = player;
		this.startt = startt;
		this.destination = destination;
	}

	public String getPlayer() {
		return player;
	}

	public void setPlayer(String player) {
		this.player = player;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getStartt() {
		return startt;
	}

	public void setStartt(String startt) {
		this.startt = startt;
	}
}
