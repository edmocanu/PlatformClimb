public class Bullet extends GameObject {
  private int speed;
  
  public Bullet(int x, int y, String picName) {
    super(x, y, picName);
  }
  
  public void setSpeed(int speed) {
    this.speed = speed;
  }
  
  public void moveDown() {
    this.setY(this.getY() + this.speed);
    if (this.getY() == Const.HEIGHT) {
      this.setY(0);
    }
    this.setBox();
  }
  
  public void moveRight() {
    this.setX(this.getX() + this.speed);
    if (this.getX() == Const.WIDTH) {
      this.setX(0);
    }
    this.setBox();
  }
  
  public void moveLeft() {
    this.setX(this.getX() - this.speed);
    if (this.getX() == 0) {
      this.setX(Const.WIDTH);
    }
    this.setBox();
  }
}