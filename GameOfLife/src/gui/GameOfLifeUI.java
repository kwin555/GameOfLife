package gui;

import game.GameOfLife;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class GameOfLifeUI extends JFrame {

  private static List<Point> liveCells = new ArrayList<>();

  private static void display(JFrame frame, int count, List<Point> liveCells) {
    JPanel gridPanel = new JPanel(new GridLayout(count, count, 1, 1));
    gridPanel.setBackground(Color.gray);
    gridPanel.setMinimumSize(new Dimension(100, 100));
    frame.setContentPane(gridPanel);

    for (int col = 0; col < count; col++) {
      for (int row = 0; row < count; row++) {
        JButton button = new JButton();
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(count, count));
        gridPanel.add(button);

        button.setBackground(liveCells.contains(new Point(row, col)) ? Color.pink : Color.white);
      }
    }

    frame.pack();
  }

  public static void main(String[] args) {
    String[] parts = Stream.of(args)
      .flatMap(a -> Stream.of(a.split(" ")))
      .toArray(String[]::new);

    String[] aPoint;
    for (String part : parts) {
      aPoint = part.split(",");
      liveCells.add(new Point(Integer.parseInt(aPoint[0]), Integer.parseInt(aPoint[1])));
    }

    JFrame frame = new JFrame("Game of Life");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setPreferredSize(new Dimension(500, 500));
    frame.validate();
    frame.setVisible(true);

    Timer blinkTimer = new Timer(250, new ActionListener() {

      int cellCount = 50;

      public void actionPerformed(ActionEvent e) {

        int negativeCell = 0;
        List<Point> boundaryCells = GameOfLife.getBounds(liveCells);

        for (Point cell : boundaryCells) {
          if (Math.min(cell.x, cell.y) < negativeCell) {
            negativeCell = Math.min(cell.x, cell.y);
            cellCount -= negativeCell;

            for (Point live : liveCells) {
              live.x -= negativeCell;
              live.y -= negativeCell;
            }
          }
        }

        for (Point cell : boundaryCells) if (Math.max(cell.x, cell.y) > cellCount) cellCount = Math.max(cell.x, cell.y);

        display(frame, cellCount + 1, liveCells);
        liveCells = GameOfLife.nextGeneration(liveCells);

      }
    });

    blinkTimer.start();
  }

}
