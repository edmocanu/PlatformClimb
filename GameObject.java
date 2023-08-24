import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

abstract class GameObject {
  private int x;
  private int y;
  private int width;
  private int height;
  private BufferedImage picture;
  private Rectangle box;
  
  public GameObject(int x, int y, String picName) {
    this.x = x;
    this.y = y;
    try {
      picture = ImageIO.read(new File(picName));
    }catch (IOException ex) {
    }
    this.width = picture.getWidth();
    this.height = picture.getHeight();
    this.box = new Rectangle(this.x, this.y, this.width, this.height);
  }
  
  public int getX() {
    return this.x;
  }
  
  public int getY() {
    return this.y;
  }
  
  public int getWidth() {
    return this.width;
  }
  
  public int getHeight() {
    return this.height;
  }
  
  public void setX(int x) {
    this.x = x;
  }
  
  public void setY(int y) {
    this.y = y;
  }
  
  public Rectangle getBox() {
    return this.box;
  }
  
  public void setBox(){
    this.box.setLocation(this.x, this.y); 
  }    
  
  public BufferedImage getImage() {
    return this.picture;
  }
  
  public void setImage(String picName) {
    try {
      picture = ImageIO.read(new File(picName));
    }catch (IOException ex) {
    }
  }
  
  public void setImage(BufferedImage image) {
    this.picture = image;
  }
 
  public void draw(Graphics g) {
    g.drawImage(this.picture, this.x, this.y, null);
  }
}