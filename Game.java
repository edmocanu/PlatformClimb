import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineEvent;

public class Game {
  JFrame gameWindow;
  JPanel leftPanel;
  GamePanel gamePanel;
  GameKeyListener keyListener;
  JButton shuffleButton;
  JButton restartButton;
  AudioInputStream audioStream;
  Clip jumpSound;
  Clip collisionSound;
  Clip coinSound;
  Clip music;
  Background background;
  Ground ground;
  boolean leftHeld;
  boolean rightHeld;
  boolean upHeld;
  boolean downHeld;
  
  Player player;
  Platform[] platforms;
  Bullet[] bullets;
  Coin[] coins;
  Item trampoline;
  Item[] portals;
  
  Game() {
    gameWindow = new JFrame("Platform Climb");
    gamePanel = new GamePanel();
    gamePanel.setLayout(null);
    keyListener = new GameKeyListener();
    shuffleButton = new JButton("Shuffle platforms");
    shuffleButton.setBounds(825,10,150,30);
    restartButton = new JButton("Restart game");
    restartButton.setBounds(825,50,150,30);

    String bgPic = "Images/background.jpeg";
    background = new Background(bgPic);
    
    ground = new Ground(-250, 540, "Images/ground.png");
    
    int startX = Const.WIDTH/10;
    int startY = Const.GROUND;
    player = new Player(startX, startY, "Images/stillPlayer.png");
    player.setLevel(1);
    player.setPoints(0);
   
    platforms = new Platform[10];
    for (int i=0; i<platforms.length; i++) {
      int platformY = 80 * i + 100;
      platforms[i] = new Platform((int) (Math.random() * (Const.WIDTH-100)), platformY, "Images/platform.png");
    }
    
    bullets = new Bullet[12];
    for (int i=0; i<bullets.length; i++) {
      bullets[i] = new Bullet(Const.WIDTH/4 * (i/4 + 1), -(i-4*(i/4))*(Const.HEIGHT/4), "Images/downBullet.png");
      bullets[i].setSpeed(Const.BULLET_SPEED);
    }
    
    coins = new Coin[2];
    for (int i=0; i<coins.length; i++) {
      coins[i] = new Coin((i+1) * Const.WIDTH * 1/3, 0, "Images/coin.png");
    }
    
    trampoline = new Item(platforms[5].getX() + 25, platforms[5].getY() - 50, "Images/trampoline.png");
    
    portals = new Item[2];
    for (int i=0; i<portals.length; i++) {
      portals[i] = new Item(platforms[i*5+2].getX() + 25, platforms[i*5+2].getY() - 50, "Images/portal.png");
    }
  }
  
  public class GamePanel extends JPanel {
    public GamePanel() {
      setFocusable(true);
      requestFocusInWindow();
    }
    public void paintComponent(Graphics g) { 
      super.paintComponent(g);
      background.draw(g);
      for (int i=0; i<platforms.length; i++) {
        platforms[i].draw(g);
      }
      for (int i=0; i<coins.length; i++) {
        coins[i].draw(g);
      }
      for (int i=0; i<portals.length; i++) {
        portals[i].draw(g);
      }
      trampoline.draw(g);
      player.draw(g);
      for (int i=0; i<bullets.length; i++) {
        bullets[i].draw(g);
      }
      ground.draw(g);
      g.setColor(Color.blue);
      g.setFont(new Font("Arial", Font.BOLD, 25));
      g.drawString("Level " + String.valueOf(player.getLevel()) + "/3", 20, 30);
      
      if (player.getLevel() == 3 && player.getPoints() >= 5) {
        g.setColor(Color.green);
      }else {
        g.setColor(Color.blue);
      }
      g.drawString(String.valueOf(player.getPoints()) + "/5 points", 20, 70);
      
      if (player.getLevel() == 3 && player.getPoints() == 5) {
        g.setFont(new Font("Arial", Font.BOLD, 25));
        g.setColor(Color.red);
        g.drawString("You beat all 3 levels! You may continue to play Level 3 or restart from Level 1.", 30, 927);
      }
    }
  }
  
  public void shufflePlatforms() {
    for (int i=0; i<platforms.length; i++) {
      player.respawn();
      
      platforms[i].setX((int) (Math.random() * (Const.WIDTH-100)));
      platforms[i].setBox();
      
      trampoline.setX(platforms[5].getX() + 25);
      trampoline.setY(platforms[5].getY() - 50);
      trampoline.setBox();
      
      for (int j=0; j<portals.length; j++) {
        portals[j].setX(platforms[j*5+2].getX() + 25);
        portals[j].setY(platforms[j*5+2].getY() - 50);
        portals[j].setBox();
      }
    }
  }
  
  public void playJumpSound() {
    if (jumpSound.isRunning()){
      jumpSound.stop();
      jumpSound.flush();
      jumpSound.setFramePosition(0);
    }
    jumpSound.start();
  }
  
  public void setUp() {
    gameWindow.setSize(Const.WIDTH, Const.HEIGHT);
    gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    gameWindow.setResizable(false);
    gameWindow.add(gamePanel);
    gamePanel.addKeyListener(keyListener);
    gamePanel.add(shuffleButton);
    shuffleButton.addActionListener(new ShuffleButtonListener());
    shuffleButton.setFocusable(false);
    gamePanel.add(restartButton);
    restartButton.addActionListener(new RestartButtonListener());
    restartButton.setFocusable(false);
    
    try {
      File audioFile = new File("Audio/jumping.wav");
      audioStream = AudioSystem.getAudioInputStream(audioFile);
      jumpSound = AudioSystem.getClip();
      jumpSound.open(audioStream);
      jumpSound.addLineListener(new JumpSoundListener());
    } catch (Exception e) {
    }
    
    try {
      File audioFile = new File("Audio/collision.wav");
      audioStream = AudioSystem.getAudioInputStream(audioFile);
      collisionSound = AudioSystem.getClip();
      collisionSound.open(audioStream);
      collisionSound.addLineListener(new CollisionSoundListener());
    } catch (Exception e) {
    }
    
    try {
      File audioFile = new File("Audio/coinSound.wav");
      audioStream = AudioSystem.getAudioInputStream(audioFile);
      coinSound = AudioSystem.getClip();
      coinSound.open(audioStream);
      coinSound.addLineListener(new CoinSoundListener());
    } catch (Exception e) {
    }
    
    try {
      File audioFile = new File("Audio/gameMusic.wav");
      audioStream = AudioSystem.getAudioInputStream(audioFile);
      music = AudioSystem.getClip();
      music.open(audioStream);
    } catch (Exception e) {
    }
    music.start();
    music.loop(Clip.LOOP_CONTINUOUSLY);
    
    gameWindow.setVisible(true);
  }
  
  public void runGameLoop() {
    while (true) {
      gameWindow.repaint();
      try {Thread.sleep(Const.FRAME_PERIOD);} catch (Exception e) {};
      background.scroll();
      player.accelerate();
      player.moveY(Const.GROUND+50);
      
      for (int i=0; i<bullets.length; i++) {
        if (player.collides(bullets[i])) {
          player.respawn();
          if (collisionSound.isRunning()){
            collisionSound.stop();
            collisionSound.flush();
            collisionSound.setFramePosition(0);
          }
          collisionSound.start();
        }
      }
      
      for (int i=0; i<platforms.length; i++) {
        if (player.getYSpeed() > 0 && player.collides(platforms[i])) {
          player.setY(platforms[i].getY() - player.getHeight());
          player.setYSpeed(0);
        }else if (player.getYSpeed() < 0 && player.collides(platforms[i])) {
          player.setY(platforms[i].getY() + platforms[i].getHeight());
          player.setYSpeed(0);
        }
      }
      if (player.getY() <= 0) {
        player.setY(0);
        player.setYSpeed(0);
      }
      for (int i=0; i<coins.length; i++) {
        if (player.collides(coins[i])) {
          shufflePlatforms();
          player.addPoint();
          if (coinSound.isRunning()){
            coinSound.stop();
            coinSound.flush();
            coinSound.setFramePosition(0);
          }
          coinSound.start();
        }
      }
      if (player.getLevel() == 1) {
        for (int i=0; i<bullets.length; i++) {
          bullets[i].moveDown();
        }
      }
      if (player.getPoints() == 5 && player.getLevel() == 1) {
        player.nextLevel();
        for (int i=0; i<bullets.length; i++) {
          bullets[i].setX(-(i-4*(i/4))*(Const.WIDTH/4));
          bullets[i].setY(125+300*(i/4));
          bullets[i].setImage("Images/rightBullet.png");
        }
      }
      if (player.getLevel() == 2) {
        for (int i=0; i<bullets.length; i++) {
          bullets[i].moveRight();
        }
      }
      if (player.getPoints() == 5 && player.getLevel() == 2) {
        player.nextLevel();
        for (int i=0; i<3; i++) {
          bullets[i].setX(-i*(Const.WIDTH/3 + 1));
          bullets[i].setY(-i*(Const.HEIGHT/3 + 1));
          bullets[i].setImage("Images/rightDiagBullet.png");
        }
        for (int i=3; i<6; i++) {
          bullets[i].setX(1000+(i-3)*(Const.WIDTH/3 + 1));
          bullets[i].setY(-(i-3)*(Const.WIDTH/3 + 1));
          bullets[i].setImage("Images/leftDiagBullet.png");
        }
        for (int i=6; i<9; i++) {
          bullets[i].setX(Const.WIDTH/2);
          bullets[i].setY(-(i-6)*((Const.HEIGHT/3 + 1)));
          bullets[i].setImage("Images/downBullet.png");
        }
        for (int i=9; i<12; i++) {
          bullets[i].setX(-(i-9)*((Const.WIDTH/3 + 1)));
          bullets[i].setY(425);
          bullets[i].setImage("Images/rightBullet.png");
        }
      }
      if (player.getLevel() == 3) {
        for (int i=0; i<3; i++) {
          bullets[i].moveDown();
          bullets[i].moveRight();
        }
        for (int i=3; i<6; i++) {
          bullets[i].moveDown();
          bullets[i].moveLeft();
        }
        for (int i=6; i<9; i++) {
          bullets[i].moveDown();
        }
        for (int i=9; i<12; i++) {
          bullets[i].moveRight();
        }
      }
      
      if (player.collides(trampoline)) {
        player.setYSpeed(Const.JUMP_SPEED);
        playJumpSound();
      }
      
      if (player.collides(portals[0])) {
        player.setY(portals[1].getY());
        player.setYSpeed(0);
        if (player.getX() <= portals[0].getX()) {
          player.setX(portals[1].getX() + 50);
          if (player.getX() > Const.WIDTH - 50) {
            player.setX(portals[1].getX() - 50);
          }
        }
        else if (player.getX() > portals[0].getX()) {
          player.setX(portals[1].getX() - 50);
          if (player.getX() < 0) {
            player.setX(portals[1].getX() + 50);
          }
        }
      }
      if (player.collides(portals[1])) {
        player.setY(portals[0].getY());
        player.setYSpeed(0);
        if (player.getX() <= portals[1].getX()) {
          player.setX(portals[0].getX() + 50);
          if (player.getX() > Const.WIDTH - 50) {
            player.setX(portals[0].getX() - 50);
          }
        }
        else if (player.getX() > portals[1].getX()) {
          player.setX(portals[0].getX() - 50);
          if (player.getX() < 0) {
            player.setX(portals[0].getX() + 50);
          }
        }
      }
      
      if (leftHeld && player.getX() > 0) {
        player.setXSpeed(-Const.PLAYER_SPEED);
        player.moveX();
      }else if (rightHeld && player.getX() < (Const.WIDTH - player.getWidth())) {
        player.setXSpeed(Const.PLAYER_SPEED);
        player.moveX();
      }else if (upHeld && player.getYSpeed() == 0) {
        for (int i=0; i<platforms.length; i++) {
          if (player.onPlatform(platforms[i])) {
            player.setYSpeed(Const.JUMP_SPEED);
            playJumpSound();
          }
        }
        if (player.onGround()) {
          player.setYSpeed(Const.JUMP_SPEED);
          playJumpSound();
        }
      }else if (downHeld) {
        player.setYSpeed(25);
      }
      
      if (player.getYSpeed() < 0) {
        player.setImage("Images/upPlayer.png");
      }else if (player.getYSpeed() > 0) {
        player.setImage("Images/downPlayer.png");
      }else if (player.getXSpeed() < 0) {
        player.setImage("Images/leftPlayer.png");
      }else if (player.getXSpeed() > 0) {
        player.setImage("Images/rightPlayer.png");
      }else if (player.getXSpeed() == 0 && player.getYSpeed() == 0) {
        player.setImage("Images/stillPlayer.png");
      }
    }
  }
  
  public class ShuffleButtonListener implements ActionListener{
    public void actionPerformed(ActionEvent event){
      shufflePlatforms();
      player.respawn();
      
      trampoline.setX(platforms[5].getX() + 25);
      trampoline.setY(platforms[5].getY() - 50);
          
      for (int j=0; j<portals.length; j++) {
        portals[j].setX(platforms[j*5+2].getX() + 25);
        portals[j].setY(platforms[j*5+2].getY() - 50);
      }
    }
  }
  
  public class RestartButtonListener implements ActionListener{
    public void actionPerformed(ActionEvent event){
      player.respawn();
      player.setLevel(1);
      player.setPoints(0);
      shufflePlatforms();
      
      for (int i=0; i<bullets.length; i++) {
        bullets[i].setX(Const.WIDTH/4 * (i/4 + 1));
        bullets[i].setY(-(i-4*(i/4))*(Const.HEIGHT/4));
        bullets[i].setImage("Images/downBullet.png");
      }
    }
  }
  
  public class GameKeyListener implements KeyListener {
    public void keyPressed(KeyEvent e) {
      int key = e.getKeyCode();
      if (key == KeyEvent.VK_LEFT) {
        leftHeld = true;
      }else if (key == KeyEvent.VK_RIGHT) {
        rightHeld = true;
      }else if (key == KeyEvent.VK_UP) {
        upHeld = true;
      }else if (key == KeyEvent.VK_DOWN) {
        downHeld = true;
      }
    }
    public void keyReleased(KeyEvent e){ 
      int key = e.getKeyCode();
      if (key == KeyEvent.VK_LEFT) {
        leftHeld = false;
        player.setXSpeed(0);
      }else if (key == KeyEvent.VK_RIGHT) {
        rightHeld = false;
        player.setXSpeed(0);
      }else if (key == KeyEvent.VK_UP) {
        upHeld = false;
      }else if (key == KeyEvent.VK_DOWN) {
        downHeld = false;
      }
    }   

    public void keyTyped(KeyEvent e){
    } 
  }
  
  public class JumpSoundListener implements LineListener {
    public void update(LineEvent event) {
      if (event.getType() == LineEvent.Type.STOP) {
        jumpSound.flush();
        jumpSound.setFramePosition(0);
      }
    }
  }  
  
  public class CollisionSoundListener implements LineListener {
    public void update(LineEvent event) {
      if (event.getType() == LineEvent.Type.STOP) {
        collisionSound.flush();
        collisionSound.setFramePosition(0);
      }
    }
  }
  
  public class CoinSoundListener implements LineListener {
    public void update (LineEvent event) {
      if (event.getType() == LineEvent.Type.STOP) {
        coinSound.flush();
        coinSound.setFramePosition(0);
      }
    }
  }
}