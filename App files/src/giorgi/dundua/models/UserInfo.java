package giorgi.dundua.models;

import java.util.ArrayList;

public class UserInfo {
	ArrayList<Pub> pubs;
	int score;
	public ArrayList<Pub> getPubs() {
		return pubs;
	}
	public void setPubs(ArrayList<Pub> pubs) {
		this.pubs = pubs;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
		
}
