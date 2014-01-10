package mygame;
import mygame.MyGame.Enemy;
import mygame.MyGame.Player;
import jgame.*;
import jgame.platform.*;
/** Minimal shooter illustrating Eclipse usage. */
public class Game extends StdGame {
	public static void main(String[]args) {new Game(parseSizeArgs(args,0));}
	public Game() { initEngineApplet(); }
	public Game(JGPoint size) { initEngine(size.x,size.y); }
	public void initCanvas() { setCanvasSettings(32,24,8,8,null,null,null); }
	
	public void initGame() {
		defineMedia("mygame.tbl");
		if (isMidlet()) {
			setFrameRate(20,1);
			setGameSpeed(2.0);
		} else {
			setFrameRate(45,1);
		}
		this.
		setHighscores(10,new Highscore(0,"nobody"),15);
		startgame_ingame=true;
	}
	public void initNewLife() {
		removeObjects(null,0);
		new Player(pfWidth()/2,pfHeight()-32,5);
	}
	public void startGameOver() { removeObjects(null,0); }
	
	public void doFrameInGame() {
		moveObjects();
		
	}
	
	public class Player extends JGObject {
		int xfacing=0;
		int yfacing=1;
		public Player(double x,double y,double speed) {
			super("player",true,x,y,1,"block1", 0,0,speed,speed,-1);
		}
		public void move() {
			setDir(0,0);
			if (getKey(key_left)  && x > xspeed){
				xdir=-1;
				setGraphic("tankl");
				xfacing=-5; yfacing=0;
			}
			if (getKey(key_right) && x < pfWidth()-5-yspeed){
				xdir=1;
				setGraphic("tankr");
				xfacing=5;yfacing=0;
				
			}
			if (getKey(key_down) && y<pfHeight()-10){
				ydir=1;
				setGraphic("tankd");	
				yfacing=5;xfacing=0;
			}
			if (getKey(key_up) && y>0) 	{
				ydir=-1;
				setGraphic("tanku");
				yfacing=-5;xfacing=0;
			}
			if (getKey(key_fire) && countObjects("block1",0) < 2) {
				new JGObject("bullet",true,x,y,4,"bary", xfacing,yfacing, -2);
			
				clearKey(key_fire);
			}
		}
		
		public void hit(JGObject obj) {
			if (and(obj.colid,2)) lifeLost();
			else {
				score += 5;
				obj.remove();
			}
		}
	}
	public class Enemy extends JGObject {
		double timer=0;
		public Enemy() {
			super("enemy",true,random(32,pfWidth()-40),random(-8,pfHeight()+40),
					2, "block1",
					1, 1, -2 );
		}
		public void move() {
			timer += gamespeed;
			x += .1;
			y +=.1;
			if (y>pfHeight()) y = -8;
		}
		public void hit(JGObject o) {
			remove();
			o.remove();
			score += 5;
		}
	}
	
}
