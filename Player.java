public class Player extends GameObject {
  private int xSpeed;
  private int ySpeed;
  private int points;
  private int level;
  
  public Player(int x, int y, String picName) {
    super(x, y, picName);
  }
  
  public void respawn() {
    this.setX(Const.WIDTH/10);
    this.setY(Const.GROUND);
    this.ySpeed = 0;
  }
  
  public void addPoint() {
    this.points += 1;
  }
  
  public void setLevel(int level) {
    this.level = level;
  }
  
  public int getLevel() {
    return this.level;
  }
  
  public void nextLevel() {
    this.level += 1;
    this.points = 0;
  }
  
  public int getPoints() {
    return this.points;
  }
  
  public void setPoints(int points) {
    this.points = points;
  }
  
  public int getXSpeed() {
    return this.xSpeed;
  }
  
  public void setXSpeed(int xSpeed) {
    this.xSpeed = xSpeed;
  }
  
  public void setYSpeed(int ySpeed) {
    this.ySpeed = ySpeed;
  }
  
  public int getYSpeed() {
    return this.ySpeed;
  }
  
  public void moveX() {
    this.setX(this.getX() + this.xSpeed);
    this.setBox();
  }
  
  public void accelerate() {
    this.ySpeed += Const.GRAVITY;
  }
  
  public void moveY(int bottomLimit) {
    this.setY(this.getY() + this.ySpeed);
    if (this.getY() + this.getHeight() >= bottomLimit) {
      this.setY(bottomLimit - this.getHeight());
      this.ySpeed = 0;
    }
    this.setBox();
  }
    
  public boolean onGround() {
    return this.getY() == Const.GROUND;
  }
  
  public boolean onPlatform(Platform platform) {
    boolean y = this.getY() == platform.getY() - this.getHeight();
    boolean leftX = this.getX() >= platform.getX() - this.getWidth();
    boolean rightX = this.getX() <= platform.getX() + platform.getWidth();
    return y && leftX && rightX;
  }
  
  public boolean collides(GameObject object) {
    return this.getBox().intersects(object.getBox());
  }
}