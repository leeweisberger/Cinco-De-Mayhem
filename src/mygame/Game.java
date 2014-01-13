package mygame;
import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;

import mygame.MyGame.Enemy;
import jgame.*;
import jgame.platform.*;
/** Minimal shooter illustrating Eclipse usage. */
public class Game extends StdGame {
	Player dino;
	public static void main(String[]args) {new Game(parseSizeArgs(args,0));}
	public Game() { initEngineApplet(); }
	public Game(JGPoint size) { initEngine(size.x,size.y); }
	
	public void initCanvas() { 
		setCanvasSettings(32,24,8,8,null,null,null); 
	
		}

	public void initGame() {
		
		defineMedia("example3.tbl");
		//setBGImage("mybackground");
		if (isMidlet()) {
			setFrameRate(20,1);
			setGameSpeed(2.0);
		} else {
			setFrameRate(45,1);
		}
		this.
		setHighscores(10,new Highscore(0,"nobody"),15);
		startgame_ingame=true;
		try { Thread.sleep(4000); }
		catch (InterruptedException e) {}
	}
	public void doFrameInGame() {
		// Move all objects.
		moveObjects();
		checkCollision(2,2);
		checkCollision(2,1);
		checkCollision(3,2);
		if(checkTime(0,(int)(8000),(int)((120-level/2))))
			new Zombie(dino);
		if(countObjects("zombie",0)==0 && gametime>=8000){
			levelDone();			
		}
	}
	
	public void initNewLife() {
		removeObjects(null,0);
		dino = new Player(pfWidth()/2,pfHeight()-32,5);
//		if (checkTime(0,(int)(800),(int)((12-level/2))))
//			new Zombie(dino);
		new Boss(dino);
		
	}
	public void defineLevel(){
		removeObjects(null,0);
		initNewLife();
	}
	public void incrementLevel() {
		score += 50;
		if (level<7) level++;
		stage++;
	}
	public void startGameOver() { removeObjects(null,0); }

	public class Player extends JGObject {
		int weapon = 1;
		boolean gun = true;
		boolean machine_gun=false;
		boolean cross_bow=false;
		boolean rocket_launcher=false;
		boolean sword = false;
		int xfacing=0;
		int yfacing=1;
		public Player(double x,double y,double speed) {
			super("player",true,x,y,1,"dino", 0,0,speed,speed,-1);
		}
		public void move() {
			setDir(0,0);

			if (getKey(key_left)  && x > xspeed){
				xdir=-1;
				setGraphic("dino");
				xfacing=-5; yfacing=0;
			}
			if (getKey(key_right) && x < pfWidth()-5-yspeed){
				xdir=1;
				setGraphic("dino");
				xfacing=5;yfacing=0;

			}
			if (getKey(key_down) && y<pfHeight()-10){
				ydir=1;
				setGraphic("dino");	
				yfacing=5;xfacing=0;
			}
			if (getKey(key_up) && y>0) 	{
				ydir=-1;
				setGraphic("dino");
				yfacing=-5;xfacing=0;
			}
			
			if (getKey(key_fire) && (weapon==1 || weapon==3) && countObjects("bullet",0) < 1 ) {
				new JGObject("bullet",true,x,y,3,"dino", xfacing,yfacing, -2);
				clearKey(key_fire);
			}
			if (getKey(key_fire) && weapon==2 && countObjects("mbullet",0)<3){
				new JGObject("mbullet",true,x,y,3,"dino", xfacing,yfacing, -2);
				clearKey(key_fire);
			}	
			
			if(getKey(key_changeright)){
				changeWeapon();
				clearKey(key_changeright);
			}
		}
		//player hits zombie
		public void hit(JGObject obj) {
			if (obj.colid==2 && colid==1){ 
				lifeLost();
				remove();
			}
		}
		public Player getPlayer(){
			return this;
		}
		public void changeWeapon(){
			if(weapon == 5)weapon = 1;
			else
				weapon++;
		}
	}
	public class Boss extends JGObject{
		public Player p;
		public Boss(Player dino){
			super("boss",true,Game.this.random(0,pfWidth()),Game.this.random(0,pfHeight()),2,"myanim_l",0,0,1,1,-1);
			xspeed=.1;yspeed=.1;
			p=dino;
		}
		public void move(){
			if(hitWalls()){ }
			if (checkTime(0,(int)(80000000),(int)((12-level/2))))
				new JGObject("bullet",true,x,y,4,"dino", random(-1,1),random(-1,1), -2);
			
		}
		public boolean hitWalls(){
			if ( (x >  pfWidth()-16 && xspeed>0)
					||   (x <            0  && xspeed<0) ) {
				xspeed = -xspeed;
				return true;
			}
			if ( (y > pfHeight()-16 && yspeed>0)
					||   (y <            0  && yspeed<0) ) {
				yspeed = -yspeed;
				return true;
			}
			return false;
		}
	}
	public class Zombie extends JGObject{
		public static final double SPEED = .1;
		public Player p;
		public int hitpoints;
		public Zombie(Player dino){
			super("zombie",true,Game.this.random(0,pfWidth()),Game.this.random(0,pfHeight()),2,"myanim_l",0,0,1,1,-1);
			
			xspeed = SPEED;
			yspeed = SPEED;
			p = dino;
			hitpoints=0;
		}

		public void move(){
			double r = random(0,1);
			if (xspeed < 0) setGraphic("myanim_l"); else setGraphic("myanim_r");

			if(hitWalls()){ }

			else if(hitZombiex() || hitZombiey()){}
			
			else{
				xspeed=SPEED;yspeed=SPEED;
				if(this.p.x>x ){
					xspeed = Math.abs(xspeed);
				}
				if(this.p.x<x  ){
					xspeed = -Math.abs(xspeed);
				}
				if(this.p.y>y  ){
					yspeed = Math.abs(yspeed); 
				}
				if(this.p.y<y  ){
					yspeed = -Math.abs(yspeed); 
				}
			}
		}
		public boolean hitWalls(){
			if ( (x >  pfWidth()-16 && xspeed>0)
					||   (x <            0  && xspeed<0) ) {
				xspeed = -xspeed;
				return true;
			}
			if ( (y > pfHeight()-16 && yspeed>0)
					||   (y <            0  && yspeed<0) ) {
				yspeed = -yspeed;
				return true;
			}
			return false;
		}

		public boolean hitZombiex(){
			if(checkCollision(2,4*SPEED,0)!=0){
				if(xspeed!=0){xspeed=0;return true;}
				if(checkCollision(2,-4*SPEED,0)==0){xspeed=-SPEED; return true;}
				return true;
			}
			if(checkCollision(2,-4*SPEED,0)!=0){
				if(xspeed!=0){xspeed=0;return true;}
				if(checkCollision(2,4*SPEED,0)==0){xspeed=SPEED; return true;}
				return true;
			}
			
			return false;
		}
		public boolean hitZombiey(){
			if(checkCollision(2,0,4*SPEED)!=0){
				if(yspeed!=0){yspeed=0;return true;}
				if(checkCollision(2,0,-4*SPEED)==0){yspeed=-SPEED; return true;}
				return true;
			}
			if(checkCollision(2,0,-4*SPEED)!=0 && y!=0){
				if(yspeed!=0){yspeed=0;return true;}
				if(checkCollision(2,0,4*.4)==0){yspeed=SPEED; return true;}
				return true;
			}
			
			return false;
		}
		public void hit(JGObject o) {
			if(o.colid==3 && hitpoints<3){
				hitpoints++;
				if(p.weapon!=3)o.remove();
			}
			if(o.colid==3 && hitpoints==3){
				if(p.weapon!=3)o.remove();
				remove();
				score += 5;
				
			}
			
		}
		
	}
	public void paintFrameTitle(){
		this.
		setFont(new JGFont("arial",0,15));
		drawString("Prehistoric Zombie Fighting Game thing", pfWidth()/2,pfHeight()/4,0);
		drawString("Press Space to Start",
				pfWidth()/2,pfHeight()/2,0);
	}
}



