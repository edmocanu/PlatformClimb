import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class Background {
  private int x1;
  private int y1;
  private int x2;
  private int y2;
  private BufferedImage bgPic;
  
  Background(String picName1) {
    this.x1 = 0;
    this.y1 = 0;
    try {
      this.bgPic = ImageIO.read(new File(picName1));
    } catch (IOException e) {}
    this.y2 = 0;
    this.x2 -= Const.WIDTH;
  }
  
  public void scroll(){
    this.x1 += Const.STEP;
    this.x2 += Const.STEP;
    if (this.x1 > Const.WIDTH){
      this.x1 = -Const.WIDTH;
      this.x2 = 0;
    }else if (this.x2 > Const.WIDTH){
        this.x2 = -Const.WIDTH;
        this.x1 = 0;
    }    
  }
  
  public void draw(Graphics g) {
    g.drawImage(this.bgPic, this.x1, this.y1, null);
    g.drawImage(this.bgPic, this.x2, this.y2, null);
  }
} 