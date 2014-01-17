package mygame;
import jgame.Highscore;
import jgame.JGColor;
import jgame.JGFont;
import jgame.JGObject;
import jgame.JGPoint;
import jgame.platform.StdGame;
/** Minimal shooter illustrating Eclipse usage. */
public class Game extends StdGame {
	boolean cheat = false;
	Player dino;
	public static void main(String[]args) {new Game(new JGPoint(1280,960));}
	public Game() { initEngineApplet(); }
	public Game(JGPoint size) { initEngine(size.x,size.y); }

	public void initCanvas() { setCanvasSettings(48,32,8,8,JGColor.red,null,null); } 

	public void initGame() {
		defineMedia("example3.tbl");
		setBGImage("lot");
		if (isMidlet()) {
			setFrameRate(20,1);
			setGameSpeed(2.0);
		} else {
			setFrameRate(45,1);
		}
		setHighscores(10,new Highscore(0,"nobody"),15);

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
		if(level==0){
			String[] e = {"zombie"};
			doLevel(e);
		}
		if(level==1){
			String[] e = {"zombie","shooter"};
			doLevel(e);
		}

		if(level==2){
			String[] e = {"zombie","shooter","angry"};
			doLevel(e);
		}
		if(level>2){
			String[] e = {"zombie","shooter","blood","angry"};
			doLevel(e);
		}

	}
	public void doLevel(String[] enemies){
		for(String enemy:enemies){
			if(checkTime(0,(int)(800+level*15),(int)((30+level*10))))
				chooseEnemy(enemy);
		}
		if(level>3){
			for(String enemy:enemies){
				if(checkTime(0,(int)(800+level*15),(int)((60-level*5))))
					chooseEnemy(enemy);
			}
		}

		checkIfLevelDone(enemies);

	}
	private void checkIfLevelDone(String[] enemies) {
		boolean enemiesLeft=false;
		for(String enemy:enemies){
			if(countObjects(enemy,0)!=0)enemiesLeft=true;
			if(enemiesLeft)break;
		}
		if(!enemiesLeft && gametime>200)levelDone();
	}
	public void chooseEnemy(String enemy){
		if(enemy.equals("zombie"))
			new Zombie();
		else if(enemy.equals("angry"))
			new AngryZombie();
		else if(enemy.equals("blood"))
			new BloodExploder();
		else if(enemy.equals("shooter"))
			new ShootingZombie();
	}

	public void initNewLife() {
		removeObjects(null,0);
		dino = new Player(pfWidth()/2,pfHeight()-32,5);
		gametime=0;
	}
	public double[] getSpawn(){
		double x = random(0,pfWidth());
		double y = random(0,pfHeight());

		while(x<(dino.x+viewWidth()/2) && x>(dino.x-viewWidth()/2)){
			x = random(0,pfWidth());

		}
		while(y<(dino.y+viewHeight()/2) && y>(dino.y-viewHeight()/2)){
			y = random(0,pfHeight());
		}
		double[] range = new double[2];
		range[0]=x;range[1]=y;
		return range;
	}
	public void defineLevel(){

		removeObjects(null,0);
		initNewLife();
	}

	public void startGameOver() { removeObjects(null,0); }
	public void incrementLevel() {
		score += 50;
		level++;
		stage++;



	}

	public class Player extends JGObject {
		int weapon = 1;
		int xfacing=0;
		int yfacing=1;

		String weapon_dir="r";

		public Player(double x,double y,double speed) {
			super("player",true,x,y,1,"mymex_l4", 0,0,speed,speed,-1);
		}
		public void move() {
			playerGraphic();
			setDir(0,0);
			playerMove();
			weaponDirection();
			if(getKey(key_fire))fireWeapon();
			if(getKey(key_cycleweapon)){
				changeWeapon();clearKey(key_cycleweapon);
			}
		}
		private void weaponDirection() {

			if(xfacing==1)weapon_dir="r";
			if(xfacing==-1)weapon_dir="l";
			if(yfacing==1)weapon_dir="u";
			if(yfacing==-1)weapon_dir="d";	
		}
		private void fireWeapon() {
			makeWeapon("gun",1,1);
			makeWeapon("gun",2,3);
			makeWeapon("laser",3,3);
			makeWeapon("arrow",4,1);
		}
		public void makeWeapon(String name, int weaponNum, int howMany ){
			if (weapon==weaponNum && countObjects(name,0) < howMany )
				new JGObject(name,true,x,y,3,name+weapon_dir, xfacing*6,yfacing*6,-3);
			if(weapon!=3)clearKey(key_fire);
		}
		private void playerMove() {
			if (getKey(key_left)  && x > xspeed){
				xdir=-1; xfacing=-1; yfacing=0;
			}
			if (getKey(key_right) && x < pfWidth()-5-yspeed){
				xdir=1;xfacing=1;yfacing=0;
			}
			if (getKey(key_down) && y<pfHeight()-10){
				ydir=1;yfacing=1;xfacing=0;
			}
			if (getKey(key_up) && y>0) 	{
				ydir=-1;yfacing=-1;xfacing=0;
			}
			if (getKey(key_action))cheat=true;
		}
		private void playerGraphic() {
			if (xdir < 0) setGraphic("mymex_l"); 
			if (xdir>0) setGraphic("mymex_r");
			if(xdir==0 && ydir==0)setGraphic("mymex_l4");
			if(ydir<0)setGraphic("mymex_d");
			if(ydir>0)setGraphic("mymex_u");
		}
		public void hit(JGObject obj) {
			if(obj.colid==2 && colid==1 && !cheat){ 
				lifeLost();
				remove();
			}
			else if(obj.colid==4 && colid==1 && !cheat){
				lifeLost();
				remove();
			}
		}
		public void changeWeapon(){
			if(weapon==level+1)weapon=1;

			else
				weapon++;
		}
	}
	public class BloodExploder extends Enemy{
		public BloodExploder(){
			super("boss",.4,getSpawn()[0],getSpawn()[1]);
			this.setAnimation("myshooter_l", "myshooter_r", "myshooter_l4");
		}
		public void move(){
			if(hitWalls()){ }
			if (checkTime(0,(int)(80000000),(int)200))
				for(int i=0;i<8;i++)
					new JGObject("bullet",true,x,y,4,"blood", random(-3,3),random(-3,3), -2);
		}
	}

	public class Zombie extends Enemy{
		public Zombie(){
			super("zombie",.4,getSpawn()[0],getSpawn()[1]);
			this.setAnimation("myalien_l", "myalien_r", "myalienr4");

		}
	}
	public class AngryZombie extends Enemy{
		public AngryZombie(){
			super("angryzombie",.4,getSpawn()[0],getSpawn()[1]);
			this.setAnimation("myalien_l", "myalien_r", "myalienr4");
			this.angry=true;
			this.hitpoints=-4;
		}
	}
	public class ShootingZombie extends Enemy{
		public ShootingZombie(){
			super("shooter",.4,getSpawn()[0],getSpawn()[1]);
			this.shooter=true;
			this.setAnimation("myexploder_l", "myexploder_r", "myexploder4");
		}

	}
	abstract class Enemy extends JGObject{
		private double SPEED = .4;
		public int hitpoints;
		boolean angry;
		boolean shooter;
		public Enemy(String name,double speed,double xspawn,double yspawn){
			super(name,true,xspawn,yspawn,2,"dino",0,0,1,1,-1);	
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
				if(angry)angry();
				if(dino.weapon!=4)o.remove();
			}
			if(o.colid==3 && hitpoints==3){
				if(dino.weapon!=4)o.remove();
				remove();
				score += 5;
			}
		}
		public void angry(){
			this.SPEED=.8;
			setAnimation("myangry_l", "myangry_r", "myangry_l4");

		}
		public void setAnimation(String left, String right, String still){
			if (xspeed < 0) setGraphic(left); 
			if (xspeed > 0) setGraphic(right);  
			if(xspeed==0)setGraphic(still);
		}
		public void move(){

			if(hitWalls()){ }

			else if(contactZombieX() || contactZombieY()){}

			else{
				followPlayer();

			}
			if (shooter && checkTime(0,80000000,70))
				new JGObject("blood",true,x,y,4,"blood", random(-3,3),random(-3,3), -2);

		}
		private void followPlayer() {
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
		public boolean contactZombieX(){
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
		public boolean contactZombieY(){
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

	public void paintFrameStartLevel(){

		drawString("Level " + (stage+1),viewWidth()/2,viewHeight()/4,0);
		drawString("START",viewWidth()/2,viewHeight()/4+50,0);

		if(level==1)
			levelPaint("machine gun", "Blood Spitter");
		else if(level==2)
			levelPaint("laser", "Angry Zombie");
		else if(level==3)
			levelPaint("arrow", "Blood Exploder");	
	}
	public void levelPaint(String weapon, String enemy){
		drawString("New Weapon: "+weapon,viewWidth()/2,viewHeight()/2,0);
		drawString("New Enemy: "+enemy,viewWidth()/2,viewHeight()/1.5,0);

	}
	public void paintFrameTitle(){
		//setColorsFont(JGColor.green, JGColor.black,new JGFont("arial",1,10) );
		//setFont(new JGFont("arial",1,10));
		setTextOutline(2, JGColor.red);

		drawString("Will You Survive Cinco de Mayo?", viewWidth()/2,viewHeight()/4,0);
		drawString("Press Space to Start",
				viewWidth()/2,viewHeight()/2,0);
		drawString("Press Z to Shoot, X to Change Weapons, and / to Cheat",viewWidth()/2,viewHeight()/1.5,0);
	}
}



