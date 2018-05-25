package com.moon;

import java.awt.*;

/**
 * 贪吃蛇
 * @author yujiangtao
 * @date 2018/5/8 11:32
 */
public class Snake {

    /**
     * 贪吃蛇默认颜色
     */
    public static final Color DEFAULT_COLOR = Color.GREEN;

    /**
     * 持有画笔（需要绘制环境传递）
     */
    Graphics graphics;

    /**
     * 贪吃蛇的头部
     */
    private Node head;

    /**
     * 贪吃蛇的尾部
     */
    private Node tail;

    /**
     * 贪吃蛇的颜色
     */
    private Color color;

    /**
     * 蛇头的方向
     */
    private Direction dir;

    /**
     * 蛇是否活着（初始化为true）
     */
    private boolean alive = true;

    /**
     * 贪吃蛇游戏得分
     */
    private int score = 0;

    /**
     * 蛇眼睛的大小
     */
    private int eyeSize = 3;

    public Snake() {
        Node node = new Node(400, 400);
        this.dir = Direction.U;
        this.head = node;
        this.tail = node;
    }

    public Snake(Graphics g) {
        this.graphics = g;
        Node node = new Node(400, 400);
        this.head = node;
        this.tail = node;
    }

    public Snake(Graphics g, Node initHead) {
        this.graphics = g;
        this.head = initHead;
    }

    /**
     * 画出贪吃蛇
     */
    public void draw() {
        Color color = graphics.getColor();
        graphics.setColor(DEFAULT_COLOR);
        // 遍历贪吃蛇的所有节点
        for(Node node = head; node != null; node = node.next) {
            // 如果是蛇头，画圆形
            if(node == head) {
                graphics.fillOval(node.x, node.y, node.width, node.height);
                // 绘制蛇的眼睛
                graphics.setColor(color);
                switch (this.dir) {
                    case U:
                        graphics.fillOval(node.x + 5, node.y + 5, eyeSize, eyeSize);
                        graphics.fillOval(node.x + 13, node.y + 5, eyeSize, eyeSize);
                        break;
                    case L:
                        graphics.fillOval(node.x + 5, node.y + 5, eyeSize, eyeSize);
                        graphics.fillOval(node.x + 5, node.y + 13, eyeSize, eyeSize);
                        break;
                    case D:
                        graphics.fillOval(node.x + 5, node.y + 13, eyeSize, eyeSize);
                        graphics.fillOval(node.x + 13, node.y + 13, eyeSize, eyeSize);
                        break;
                    case R:
                        graphics.fillOval(node.x + 13, node.y + 5, eyeSize, eyeSize);
                        graphics.fillOval(node.x + 13, node.y + 13, eyeSize, eyeSize);
                        break;
                    default:
                }
            } else {
                graphics.setColor(DEFAULT_COLOR);
                graphics.fillOval(node.x, node.y, node.width, node.height);
            }
        }
        // printSnake();
        graphics.setColor(color);
    }

    /**
     * 吞吃蛋
     * @param egg
     */
    public boolean eatEgg(Egg egg) {
        // 首先校验是否咬到自己
        checkEatSelf();
        Rectangle eggRect = new Rectangle(egg.getX(), egg.getY(), egg.getWidth(), egg.getHeight());
        int x = head.x;
        int y = head.y;
        // 获取贪吃蛇的方向
        Direction dir = this.dir;
        if(dir == Direction.U) {
            y--;
        } else if(dir == Direction.L) {
            x--;
        } else if(dir == Direction.D) {
            y++;
        } else if(dir == Direction.R) {
            x++;
        }
        Rectangle headRect = new Rectangle(x, y, head.width, head.height);
        // 蛇头与蛋的碰撞检测
        if(headRect.intersects(eggRect)) {
            System.out.println("发生了碰撞！！！");
            addToHead();
            // 得分加一
            score ++;
            return true;
        }
        return false;
    }

    /**
     * 判断蛇是否咬到自己
     * @return
     */
    public void checkEatSelf() {
        boolean isEat = false;
        Rectangle headRect = new Rectangle(head.x, head.y, head.width, head.height);
        for(Node node = head; node != null; node = node.next) {
            if(node != head) {
                Rectangle nodeRect = new Rectangle(node.x, node.y, node.width, node.height);
                isEat = headRect.intersects(nodeRect);
                if(isEat) {
                    alive = false;
                }
            }
        }
    }

    /**
     * 贪吃蛇的移动
     */
    public boolean move() {
        checkBorder();
        if(alive) {
            addToHead();
            removeFromTail();
            draw();
            return true;
        }
        return false;
    }

    /**
     * 校验蛇是否触碰到边界
     * @return
     */
    private boolean checkBorder() {
        if(head.x <= Main.PANEL_WIDTH - 20 && head.x >= 0 &&
                head.y <= Main.PANEL_HEIGHT - 20 && head.y >=0) {
            return true;
        }
        // 将蛇设置为死亡
        this.setAlive(false);
        System.out.println("蛇撞墙了。。。");
        return false;
    }

    /**
     * 根据方向将节点添加到头部
     */
    private void addToHead() {
        Node newHead = null;
        switch (dir) {
            case D:
                newHead = new Node(head.x, head.y + head.height);
                break;
            case L:
                newHead = new Node(head.x - head.width, head.y);
                break;
            case R:
                newHead = new Node(head.x + head.width, head.y);
                break;
            case U:
                newHead = new Node(head.x, head.y - head.height);
                break;
            default:
                break;
        }
        newHead.next = head;
        head = newHead;
    }

    /**
     * 移除尾部的一截
     */
    private void removeFromTail() {
        //printSnake();
        for(Node node = head; node != null; node = node.next) {
            if(node.next == tail) {
                node.next = null;
                tail = node;
            }
        }
        //printSnake();
    }

    /**
     * 打印贪吃蛇的各个节点，从头开始
     * @return
     */
    private void printSnake() {
        String str = "Snake";
        for(Node node = head; node != null; node = node.next) {
            str += " [" + head.x + ", " + head.y + "]" + "->";
        }
        System.out.println(str);
    }

    public Graphics getGraphics() {
        return graphics;
    }

    public void setGraphics(Graphics graphics) {
        this.graphics = graphics;
    }

    public Node getHead() {
        return head;
    }

    public void setHead(Node head) {
        this.head = head;
    }

    public Node getTail() {
        return tail;
    }

    public void setTail(Node tail) {
        this.tail = tail;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Direction getDir() {
        return dir;
    }

    public void setDir(Direction dir) {
        this.dir = dir;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    /**
     * 代表贪吃蛇的各个节点
     */
    private static class Node {

        /**
         * 默认宽度
         */
        public static final int DEFAULT_WIDTH = 20;

        /**
         * 默认高度
         */
        public static final int DEFAULT_HEIGHT = 20;

        /**
         * 节点的x轴位置
         */
        private int x;

        /**
         * 节点y轴位置
         */
        private int y;

        /**
         * 节点的宽度
         */
        private int width;

        /**
         * 节点的高度
         */
        private int height;

        /**
         * 指向下一个节点
         */
        private Node next;

        public Node(int x, int y) {
            this(x, y, null);
        }

        public Node(int x, int y, Node next) {
            this(x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, next);
        }

        public Node(int x, int y, int width, int height) {
            this(x, y, width, height, null);
        }

        public Node(int x, int y, int width, int height, Node next) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.next = next;
        }
    }

    @Override
    public String toString() {
        return "Snake{" +
                "graphics=" + graphics +
                ", head=" + head +
                ", tail=" + tail +
                ", color=" + color +
                ", dir=" + dir +
                ", alive=" + alive +
                '}';
    }
}
