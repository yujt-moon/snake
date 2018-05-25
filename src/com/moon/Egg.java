package com.moon;

import java.awt.*;
import java.util.Random;

/**
 * 蛋类
 * @author yujiangtao
 * @date 2018/5/8 17:50
 */
public class Egg {

    /**
     * 默认宽度
     */
    public static final int DEFAULT_WIDTH = 20;

    /**
     * 默认高度
     */
    public static final int DEFAULT_HEIGHT = 20;

    /**
     * 持有画笔的引用（需要环境传递）
     */
    Graphics graphics;

    /**
     * egg所在x轴位置
     */
    private int x;

    /**
     * egg所在y轴位置
     */
    private int y;

    /**
     * egg的宽度
     */
    private int width;

    /**
     * egg的高度
     */
    private int height;

    /**
     * 蛋的颜色
     */
    private Color color;

    public Egg(int x, int y) {
        this(x, y, null);
    }

    public Egg(int x, int y, Graphics g) {
        this(x, y, DEFAULT_WIDTH, DEFAULT_HEIGHT, g);
    }

    public Egg(int x, int y, int width, int height, Graphics g) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.graphics = g;
    }

    /**
     * 随机放下一个蛋
     * @return
     */
    public Egg layDownEgg(int panelWidth, int panelHeight) {
        Random rand = new Random();
        int xPos = rand.nextInt((panelWidth - 20 - width)/width) * width;
        int yPos = rand.nextInt((panelHeight - 40 - height)/height) * height;
        // System.out.println("x: " + xPos + " y: " + yPos);
        this.x = xPos;
        this.y = yPos;
        return this;
    }

    public static Egg generateEgg(int panelWidth, int panelHeight) {
        Random rand = new Random();
        int xPos = rand.nextInt((panelWidth - 20)/20) * 20;
        int yPos = rand.nextInt((panelHeight - 20)/20) * 20;
        return new Egg(xPos, yPos);
    }

    /**
     * 画出egg
     */
    public void drawEgg() {
        Color color = graphics.getColor();
        graphics.setColor(Color.ORANGE);
        graphics.fillOval(this.x, this.y, this.width, this.height);
        graphics.setColor(color);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "Egg{" +
                "x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                ", color=" + color +
                '}';
    }
}
