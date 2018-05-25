package com.moon;

import java.awt.*;
import java.awt.event.*;

/**
 * 贪吃蛇游戏主界面
 */
public class Main extends Frame {

    /**
     * 整个游戏面板的宽度
     */
    public static final int PANEL_WIDTH = 800;

    /**
     * 整个游戏面板的高度
     */
    public static final int PANEL_HEIGHT = 600;

    /**
     * 游戏具体的面板
     */
    final MyPanel myPanel = new MyPanel();

    /**
     * 持有对蛇的引用
     */
    private Snake snake = new Snake();

    /**
     * 持有对蛋的引用
     */
    private Egg egg = Egg.generateEgg(PANEL_WIDTH, PANEL_HEIGHT);

    /**
     * 面板的线程
     */
    private Thread panelThread = null;

    /**
     * 游戏结束时弹出的对话框
     */
    Dialog dialog = new Dialog(this, "游戏结束", true);

    /**
     * 程序主入口，启动贪吃蛇程序
     * @param args
     */
    public static void main(String[] args) {
        new Main().launchFrame();
    }

    /**
     * 生成贪吃蛇主界面
     */
    public void launchFrame() {
        this.setSize(PANEL_WIDTH + 7, PANEL_HEIGHT + 36);
        this.setLocation(500, 300);
        this.setBackground(Color.lightGray);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        myPanel.setSize(PANEL_WIDTH, PANEL_HEIGHT);
        this.add(myPanel, BorderLayout.CENTER);
        this.setTitle("贪吃蛇");
        this.setResizable(false);
        // 添加贪吃蛇图标
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Image image = toolkit.getImage(this.getClass().getResource("/image/snake.jpg"));
        this.setIconImage(image);
        this.setVisible(true);
        panelThread = new Thread(myPanel);
        panelThread.start();
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                keyControl(e);
            }
        });
    }

    /**
     * 键盘按键监控
     * @param e
     */
    private void keyControl(KeyEvent e) {
        // 获取snake的现在的方向
        Direction snakeDir = snake.getDir();
        // 不允许指向相反的方向
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                if(snakeDir != Direction.D) {
                    snake.setDir(Direction.U);
                }
                break;
            case KeyEvent.VK_DOWN:
                if(snakeDir != Direction.U) {
                    snake.setDir(Direction.D);
                }
                break;
            case KeyEvent.VK_LEFT:
                if(snakeDir != Direction.R) {
                    snake.setDir(Direction.L);
                }
                break;
            case KeyEvent.VK_RIGHT:
                if(snakeDir != Direction.L) {
                    snake.setDir(Direction.R);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 面板类
     */
    class MyPanel extends Panel implements Runnable {

        /**
         * 贪吃蛇移动间隔
         */
        int intervalMills = 500;

        /**
         * 背景画布，用来作为双缓冲的
         */
        Image offScreenImage = null;

        Graphics gOffScreen = null;

        public void init(Graphics g) {
            // 将背景修改成图片
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Image image = toolkit.getImage(this.getClass().getResource("/image/background.jpg"));
            gOffScreen.drawImage(image, 0, 0, PANEL_WIDTH, PANEL_HEIGHT, null, null);
        }

        @Override
        public void update(Graphics g) {
            // 添加此行才会重绘
            //super.update(g);
            paintAllImage(g);
        }

        /**
         * 画出所有物品
         * @param g
         */
        private void paintAllImage(Graphics g) {
            // 判断背景幕布是否存在
            if(offScreenImage == null) {
                offScreenImage = createImage(PANEL_WIDTH, PANEL_HEIGHT);
                gOffScreen = offScreenImage.getGraphics();
            }

            // 获取画笔的默认属性
            Color color = gOffScreen.getColor();
            Font font = gOffScreen.getFont();

            /*gOffScreen.setColor(getBackground());
            gOffScreen.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);*/
            // 将背景修改成图片
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Image image = toolkit.getImage(this.getClass().getResource("/image/background.jpg"));
            gOffScreen.drawImage(image, 0, 0, PANEL_WIDTH, PANEL_HEIGHT, null, null);

            gOffScreen.setColor(color);

            // 画出格子
            for(int i = 1; i <= PANEL_HEIGHT/20; i++) {
                gOffScreen.drawLine(0, i*20, PANEL_WIDTH, i*20);
            }
            for(int i = 1; i <= PANEL_WIDTH/20; i++) {
                gOffScreen.drawLine(i*20, 0, i*20, PANEL_HEIGHT);
            }
            // 画出得分
            gOffScreen.setColor(Color.RED);
            gOffScreen.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 30));
            gOffScreen.drawString("总得分为：" + snake.getScore(), 20, 40);

            // 还原画笔的默认属性
            gOffScreen.setColor(color);
            gOffScreen.setFont(font);

            // 传递画笔，绘制蛋
            egg.graphics = gOffScreen;
            egg.drawEgg();
            // 传递画笔绘制贪吃蛇
            snake.graphics = gOffScreen;
            snake.move();
            g.clearRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);
            g.drawImage(offScreenImage, 0, 0, this);
        }


        /**
         * 显示对话框
         */
        public void showDialog() {
            Button b1 = new Button("Game Over");
            Button b2 = new Button("Restart Game");
            b1.setSize(200, 50);
            b2.setSize(200, 50);
            // lambda表达式
            b1.addActionListener((e) -> System.exit(0));
            // b2.addActionListener((e) -> System.exit(0));
            dialog.add(b1);
            dialog.add(b2, BorderLayout.EAST);
            dialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });
            dialog.setLayout(new FlowLayout());
            dialog.setBounds(Main.this.getX()+ 250, Main.this.getY() + 225, 300, 150);
            //dialog.pack();
            // 显示对话框
            dialog.setVisible(true);
            this.add(dialog);
        }

        @Override
        public void run() {
            while(true) {
                try {
                    // 判断蛋是否被吃
                    if(snake.eatEgg(egg)) {
                        // 越往后贪吃蛇的速度越快
                        intervalMills = (intervalMills - (snake.getScore()/5) * 50);
                        intervalMills = intervalMills > 300 ? intervalMills : 300;
                        egg = egg.layDownEgg(PANEL_WIDTH, PANEL_HEIGHT);
                    }
                    if(!snake.isAlive()) {
                        System.out.println("贪吃蛇死亡，游戏结束！");
                        showDialog();
                        // 停止线程
                        panelThread.join();
                    }
                    Thread.sleep(intervalMills);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                repaint();
            }
        }
    }
}


