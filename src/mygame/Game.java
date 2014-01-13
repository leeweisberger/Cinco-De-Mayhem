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
	public static void main(String[]args) {new Game(new JGPoint(640,480));}
	public Game() { initEngineApplet(); }
	public Game(JGPoint size) { initEngine(size.x,size.y); }

	public void initCanvas() { setCanvasSettings(32,24,8,8,null,null,null); } 

	public void initGame() {

		defineMedia("example3.tbl");
		setBGImage("grass");
		if (isMidlet()) {
			setFrameRate(20,1);
			setGameSpeed(2.0);
		} else {
			setFrameRate(45,1);
		}
		setHighscores(10,new Highscore(0,"nobody"),15);
		startgame_ingame=true;
		setPFSize(64,48);
		try { Thread.sleep(2000); }
		catch (InterruptedException e) {}
	}
	public void doFrameInGame() {
		// Move all objects.
		moveObjects();
		checkCollision(2,2);
		checkCollision(2,1);
		checkCollision(3,2);
		checkCollision(4,1);
		int xofs=(int)dino.x;
		int yofs=(int)dino.y;
		setViewOffset(xofs,yofs,true);

		//level1
		if(checkTime(0,(int)(800),(int)((50-level/2))))
			new Zombie();
		if(countObjects("zombie",0)==0 && gametime>80){
			levelDone();
		}

		//level3+
		/*if(level>1){
			if(countObjects("boss",0)==0 && gametime>80)
				levelDone();
			if(checkTime(0,(int)(800),(int)((100-level/2))))
				new Boss();
		}*/

	}

	public void initNewLife() {
		removeObjects(null,0);
		dino = new Player(pfWidth()/2,pfHeight()-32,5);
		gametime=0;
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
		int xfacing=0;
		int yfacing=1;
		String weapon_dir="r";
		public Player(double x,double y,double speed) {
			super("player",true,x,y,1,"dude", 0,0,speed,speed,-1);
		}
		public void move() {
			setDir(0,0);

			if (getKey(key_left)  && x > xspeed){
				xdir=-1; xfacing=-1; yfacing=0;
				setGraphic("dude");
			}
			if (getKey(key_right) && x < pfWidth()-5-yspeed){
				xdir=1;xfacing=1;yfacing=0;
				setGraphic("dude");
				

			}
			if (getKey(key_down) && y<pfHeight()-10){
				ydir=1;yfacing=1;xfacing=0;
				setGraphic("dude");	
				
			}
			if (getKey(key_up) && y>0) 	{
				ydir=-1;yfacing=-1;xfacing=0;
				setGraphic("dude");
				
			}
			//which direction weapon should be used
			if(xfacing==1)weapon_dir="r";
			if(xfacing==-1)weapon_dir="l";
			if(yfacing==1)weapon_dir="u";
			if(yfacing==-1)weapon_dir="d";
			if (getKey(key_fire) && weapon==1 && countObjects("bullet",0) < 1 ) {
				new JGObject("bullet",true,x,y,3,"gun"+weapon_dir, xfacing*6,yfacing*6, -2);
				clearKey(key_fire);
			}
			if (getKey(key_fire) && weapon==2 && countObjects("mbullet",0)<3){
				new JGObject("mbullet",true,x,y,3,"gun"+weapon_dir, xfacing*6,yfacing*6, -2);
				clearKey(key_fire);
			}	
			if(getKey(key_fire) && weapon==3 && countObjects("arrow",0)<1){
				new JGObject("arrow",true,x,y,3,"arrow"+weapon_dir, xfacing*6,yfacing*6, -2);
			}
			if (getKey(key_fire) && weapon==4){
				new JGObject("laser",true,x,y,3,"laser"+weapon_dir, xfacing*6,yfacing*6, -2);
			}

			if(getKey(key_changeright)){
				changeWeapon();
				clearKey(key_changeright);
			}
		}
		//player hits zombie or projectile
		public void hit(JGObject obj) {
			if (obj.colid==2 && colid==1){ 
				lifeLost();
				remove();
			}
			if(obj.colid==4 && colid==1){
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
	public class Boss extends Enemy{
		private static final double SPEED = .4;
		public Boss(){
			super("boss","dino",SPEED);
		}
		public void move(){
			if(hitWalls()){ }
			if (checkTime(0,(int)(80000000),(int)20))
				new JGObject("bullet",true,x,y,4,"blood", random(-3,3),random(-3,3), -2);
		}
	}
	public class Zombie extends Enemy{
		private static final double SPEED = .4;
		public Zombie(){
			super("zombie","dino",SPEED);
		}
		public void move(){
			double r = random(0,1);
			if (xspeed < 0) setGraphic("myanim_l"); else setGraphic("myanim_r");

			if(hitWalls()){ }

			else if(hitZombiex() || hitZombiey()){}

			else{
				xspeed=SPEED;yspeed=SPEED;
				if(dino.x>x ){
					xspeed = Math.abs(xspeed);
				}
				if(dino.x<x  ){
					xspeed = -Math.abs(xspeed);
				}
				if(dino.y>y  ){
					yspeed = Math.abs(yspeed); 
				}
				if(dino.y<y  ){
					yspeed = -Math.abs(yspeed); 
				}
			}
			if (level>0 && checkTime(0,80000000,30))
				new JGObject("bullet",true,x,y,4,"blood", random(-3,3),random(-3,3), -2);
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
				if(checkCollision(2,0,4*SPEED)==0){yspeed=SPEED; return true;}
				return true;
			}

			return false;
		}


	}
	public void paintFrameTitle(){
		this.
		setFont(new JGFont("arial",0,10));
		drawString("Prehistoric Zombie Fighting Game thing", viewWidth()/2,viewHeight()/4,0);
		drawString("Press Space to Start",
				viewWidth()/2,viewHeight()/2,0);
	}
	abstract class Enemy extends JGObject{

		public int hitpoints;
		public Enemy(String name,String image,double speed){
			super(name,true,Game.this.random(0,pfWidth()),Game.this.random(0,pfHeight()),2,image,0,0,1,1,-1);	
			xspeed = speed;
			yspeed = speed;

			hitpoints=0;
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
		public void hit(JGObject o) {
			if(o.colid==3 && hitpoints<3){
				hitpoints++;
				if(dino.weapon!=3)o.remove();
			}
			if(o.colid==3 && hitpoints==3){
				if(dino.weapon!=3)o.remove();
				remove();
				score += 5;
			}

		}
	}
}



