package piece;

import java.util.LinkedList;

import application.Game2Controller;



public class Piece {
	public int x;//0~7
	public int y;
	int scaleX,scaleY;
	char column;
	int row;
	public boolean isWhite;
	public String name;
	public LinkedList<Piece> ps;
	
	public Piece(int arrayX, int arrayY, boolean isWhite,String n, LinkedList<Piece> ps) {
		x = arrayX;
		y = arrayY;
		scaleX = x * 45;
		scaleY = y * 45;
        this.isWhite = isWhite;
        name=n;
        ps.add(this);
    }
	//오버로드
	public Piece(char column, int row, boolean isWhite,String n, LinkedList<Piece> ps) {//오버로드
		x = Column(column);
		y = Row(row);
		scaleX = x * 45;
		scaleY = y * 45;
        this.isWhite = isWhite;
        name=n;
        ps.add(this);
    }
	
	public void setPosition(int arrayX, int arrayY) {
		if(Game2Controller.getPiece(arrayX*64, arrayX*64) != null ) {
    		if(Game2Controller.getPiece(arrayX*64, arrayX*64).isWhite != isWhite) {
    			Game2Controller.getPiece(arrayX*64, arrayX*64).capture();
        	}else {
        		scaleX = x * 64;
        		scaleY = y * 64;
        		return;
        	}
        }
		x = arrayX;
		y = arrayY;
		scaleX = x * 45;
		scaleY = y * 45;
	}
	//오버로드
	public void setPosition(char column, int row) {//오버로드
		int arrayX = Column(column);
		int arrayY = Row(row);
		if(Game2Controller.getPiece(arrayX*64, arrayX*64) != null ) {
    		if(Game2Controller.getPiece(arrayX*64, arrayX*64).isWhite != isWhite) {
    			Game2Controller.getPiece(arrayX*64, arrayX*64).capture();
        	}else {
        		scaleX = x * 64;
        		scaleY = y * 64;
        		return;
        	}
        }
		x = Column(column);
		y = Row(row);
		scaleX = x * 45;
		scaleY = y * 45;
	}
	
	public static int Column(char column) {
		//A65 ~ H72 Z90   a97 ~ h104 z122
		if((column>=65&&column<=72) || (column>=97 && column<=104)) {
			if(column >= 97) {//소문자 a~h
				return column - 97;
			}else {//대문자 A~H
				return column - 65;
			}
		}else {// a~h/A~H가 아니면 invalid
			return -1 ;
		}
	}
	
	public static int Row(int row) {//인덱스는 0부터 시작잉께
		if(row >=1 && row<=8) {
			return row - 1;
		}else {
			return -1;	//-1일 경우 틀림 index out of bound 나오게 유도		
		}
	}
	
	public void capture() {
		ps.remove(this);
	}
	
}
