package game;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.*;

public interface GameOfLife {
  public enum CellState { DEAD, ALIVE, NONE };

  public static CellState computeNextState(CellState currentState, long numberOfLiveNeighbors) {
    return currentState == CellState.ALIVE && numberOfLiveNeighbors == 2 ||
      numberOfLiveNeighbors == 3 ? CellState.ALIVE : CellState.DEAD;
  }

  public static List<Point> generateSignalsForPosition(Point cell) {
    int x = cell.x;
    int y = cell.y;
    List<Point> positions = new ArrayList<>();

    for (int i = x - 1; i <= x + 1; i++) {
      for (int j = y - 1; j <= y + 1; j++) {
        if (i == x && j == y) continue;

        positions.add(new Point(i, j));
      }
    }

    return positions;
  }

  public static List<Point> generateSignalsForPositions(List<Point> cells) {
    return cells.stream()
      .flatMap(cell -> generateSignalsForPosition(cell).stream())
      .collect(toList());
  }

  public static Map<Point, Long> countSignals(List<Point> cells) {
    return cells.stream()
      .collect(groupingBy(Function.identity(), counting()));
  }

  public static List<Point> nextGeneration(List<Point> cells) {
    Map<Point, Long> signalsCount = countSignals(generateSignalsForPositions(cells));

    return signalsCount.keySet()
      .stream()
      .filter(cell -> computeNextState(cells.contains(cell) ? CellState.ALIVE : CellState.DEAD, signalsCount.get(cell)) == CellState.ALIVE)
      .collect(toList());
  }

  public static List<Point> getBounds(List<Point> cells) {
    if (cells.isEmpty()) return List.of();

    Point leftBound = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
    Point rightBound = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);

    for(Point cell : cells) {
      if(cell.x < leftBound.x) leftBound.x = cell.x;
      if(cell.y < leftBound.y) leftBound.y = cell.y;
      if(cell.x > rightBound.x) rightBound.x = cell.x;
      if(cell.y > rightBound.y) rightBound.y = cell.y;
    }

    return List.of(leftBound, rightBound);
  }
}
