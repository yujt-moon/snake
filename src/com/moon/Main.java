package com.moon;

import com.moon.util.PropertiesUtil;

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
     * 有新面板中的菜单栏
     */
    private MenuBar menuBar = new MenuBar();

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
     * 面板线程的状态
     */
    private int status;

    /**
     * 运行状态
     */
    private final int RUNNING = 0;

    /**
     * 暂停状态
     */
    private final int SUSPEND = 1;

    /**
     * 停止状态
     */
    private final int STOP = -1;

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
        // 初始化游戏界面外观
        initFrameFacade();
        // 事件处理程序
        initEventListener();
        this.setVisible(true);
    }

    /**
     * 初始化游戏界面外观
     */
    private void initFrameFacade() {
        // 设置主窗口的属性
        this.setSize(PANEL_WIDTH + 7, PANEL_HEIGHT + 36);
        this.setLocation(500, 300);
        this.setBackground(Color.lightGray);
        this.setTitle("贪吃蛇");
        this.setResizable(false);
        // 添加贪吃蛇图标
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Image image = toolkit.getImage(this.getClass().getResource("/image/snake.jpg"));
        this.setIconImage(image);

        // 添加菜单栏
        Menu startMenu = new Menu("Start");
        MenuItem startItem = new MenuItem("Start Game");
        MenuItem introItem = new MenuItem("Introduction");
        startItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });
        startMenu.add(startItem);
        // 添加菜单的分隔线
        startMenu.addSeparator();
        startMenu.add(introItem);
        menuBar.add(startMenu);
        this.setMenuBar(menuBar);

        // 游戏玩法介绍对话框
        final Dialog msgDialog = new Dialog(this, "游戏介绍", false);
        msgDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                msgDialog.setVisible(false);
            }
        });
        // 游戏玩法介绍内容
        Label introLabel = new Label();
        String content = "通过上下左右控制贪吃蛇的方向，空格键用来暂停";
        introLabel.setText(content);
        msgDialog.add(introLabel, BorderLayout.CENTER);
        msgDialog.setBounds(Main.this.getX()+ 250, Main.this.getY() + 225, 300, 150);
        msgDialog.pack();
        introItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                msgDialog.setVisible(true);
            }
        });

        // 添加贪吃蛇所在的面板
        myPanel.setSize(PANEL_WIDTH, PANEL_HEIGHT);
        this.add(myPanel, BorderLayout.CENTER);
    }

    /**
     * 初始化主界面的事件处理程序
     */
    private void initEventListener() {
        // 窗口关闭事件
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        // 键盘事件监听
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                keyControl(e);
            }
        });
    }

    /**
     * 开始游戏
     */
    private void startGame() {
        // 新启动一个线程
        panelThread = new Thread(myPanel);
        if(panelThread != null) {
            panelThread.start();
            // 修改当前的状态
            status = RUNNING;
        }
    }

    /**
     * 重新开始游戏
     */
    private void restartGame() {
        startGame();
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
            case KeyEvent.VK_SPACE:
                // 如果面板线程正在运行
                if(status == RUNNING) {
                    // 修改状态为暂停
                    status = SUSPEND;
                } else if(status == SUSPEND) {
                    // 唤起线程
                    myPanel.resume();
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

        /**
         * 背景画笔
         */
        Graphics gOffScreen = null;

        public void init(Graphics g) {
            // 将背景修改成图片
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Image image = toolkit.getImage(this.getClass().getResource("/image/background.jpg"));
            g.drawImage(image, 0, 0, PANEL_WIDTH, PANEL_HEIGHT, null, null);
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
            String historyMaxScore = PropertiesUtil.getDefaultPropertiesValue("history.max.score");
            gOffScreen.drawString("最高得分为：" + historyMaxScore, 400, 40);

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
            // b1.addActionListener((e) -> System.exit(0));
            b1.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });
            /*b2.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dialog.setVisible(false);
                    restartGame();
                }
            });*/
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
        public synchronized void run() {
            while(true) {
                try {
                    // 如果当前线程是运行状态
                    if(status == RUNNING) {
                        // 判断蛋是否被吃
                        if(snake.eatEgg(egg)) {
                            // 越往后贪吃蛇的速度越快
                            intervalMills = (intervalMills - (snake.getScore()/5) * 50);
                            intervalMills = intervalMills > 300 ? intervalMills : 300;
                            egg = egg.layDownEgg(PANEL_WIDTH, PANEL_HEIGHT);
                        }
                        if(!snake.isAlive()) {
                            System.out.println("贪吃蛇死亡，游戏结束！");
                            // 记录最高成绩
                            // 获取当前的最高分
                            int curScore = Integer.valueOf(PropertiesUtil.getDefaultPropertiesValue("history.max.score"));
                            if(curScore < snake.getScore()) {
                                PropertiesUtil.setDefaultPropertiesValue(
                                        "history.max.score", String.valueOf(snake.getScore()));
                            }
                            showDialog();
                            // 停止线程
                            panelThread.join();
                        }
                        Thread.sleep(intervalMills);
                    } else if(status == SUSPEND) {
                        wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                repaint();
            }
        }

        /**
         * 唤起暂停的线程
         */
        public synchronized void resume() {
            if(status == SUSPEND) {
                status = RUNNING;
                notifyAll();
            }
        }
    }
}


