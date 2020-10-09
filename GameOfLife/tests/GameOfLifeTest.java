package game;

import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.List;
import java.util.Map;

import static game.GameOfLife.CellState.ALIVE;
import static game.GameOfLife.CellState.DEAD;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.*;

class GameOfLifeTest {
  @Test
  void Canary() {
    assertTrue(true);
  }

  @Test
  void aDeadCellWith_LiveNeighborsStaysDead() {
    assertAll(
      () -> assertEquals(DEAD, GameOfLife.computeNextState(DEAD, 0)),
      () -> assertEquals(DEAD, GameOfLife.computeNextState(DEAD, 1)),
      () -> assertEquals(DEAD, GameOfLife.computeNextState(DEAD, 2)),
      () -> assertEquals(DEAD, GameOfLife.computeNextState(DEAD, 4)),
      () -> assertEquals(DEAD, GameOfLife.computeNextState(DEAD, 8))
    );
  }

  @Test
  void aDeadCellWithThreeLiveNeighborsComesToLife() {
    assertEquals(ALIVE, GameOfLife.computeNextState(DEAD, 3));
  }

  @Test void aLiveCellWith_LiveNeighborsDies() {
    assertAll(
      () -> assertEquals(DEAD, GameOfLife.computeNextState(ALIVE, 1)),
      () -> assertEquals(DEAD, GameOfLife.computeNextState(ALIVE, 4)),
      () -> assertEquals(DEAD, GameOfLife.computeNextState(ALIVE, 8))
    );
  }

  @Test
  void aLiveCellWith_LiveNeighborsLives() {
    assertAll(
      () -> assertEquals(ALIVE, GameOfLife.computeNextState(ALIVE, 2)),
      () -> assertEquals(ALIVE, GameOfLife.computeNextState(ALIVE, 3))
    );
  }

  @Test
  void aLiveCellAtPosition3_3ShouldGenerateEightSignals() {
    List<Point> positions = List.of(new Point(2, 2), new Point(2, 3),
      new Point(2, 4), new Point(3, 2), new Point(3, 4),
      new Point(4, 2), new Point(4, 3), new Point(4, 4));

    assertEquals(positions, GameOfLife.generateSignalsForPosition(new Point(3, 3)));
  }

  @Test
  void aLiveCellAtPosition2_4ShouldGenerateEightSignals() {
    List<Point> positions = List.of(new Point(1, 3), new Point(1, 4),
      new Point(1, 5), new Point(2, 3), new Point(2, 5),
      new Point(3, 3), new Point(3, 4), new Point(3, 5));

    assertEquals(positions, GameOfLife.generateSignalsForPosition(new Point(2, 4)));
  }

  @Test
  void aLiveCellAtPosition0_0ShouldGenerateEightSignals() {
    List<Point> positions = List.of(new Point(-1, -1), new Point(-1, 0),
      new Point(-1, 1), new Point(0, -1), new Point(0, 1),
      new Point(1, -1), new Point(1, 0), new Point(1, 1));

    assertEquals(positions, GameOfLife.generateSignalsForPosition(new Point(0, 0)));
  }

  @Test
  void zeroCellsShouldReturn0Positions() {
    assertEquals(List.of(), GameOfLife.generateSignalsForPositions(List.of()));
  }

  @Test
  void oneCellShouldReturn8Positions() {
    assertEquals(8, GameOfLife.generateSignalsForPositions(List.of(new Point(0, 0))).size());
  }

  @Test
  void twoCellsShouldReturn16Positions() {
    assertEquals(16,
      GameOfLife.generateSignalsForPositions(List.of(new Point(0, 0), new Point(1, 1))).size());
  }

  @Test
  void threeCellsShouldReturn24Positions() {
    assertEquals(24,
      GameOfLife.generateSignalsForPositions(
        List.of(new Point(0, 0), new Point(1, 1), new Point(2, 2))).size());
  }

  @Test
  void zeroPositionsReturnsEmptyMap() {
    assertEquals(Map.of(), GameOfLife.countSignals(List.of()));
  }

  @Test
  void onePositionsReturnsMapWithPositionCountOne() {
    assertEquals(Map.of(new Point(0, 0), 1L),
      GameOfLife.countSignals(List.of(new Point(0, 0))));
  }

  @Test
  void twoPositionsReturnsMapWithPositionCountTwo() {
    assertEquals(Map.of(new Point(0, 0), 2L),
      GameOfLife.countSignals(List.of(new Point(0, 0), new Point(0, 0))));
  }

  @Test
  void threePositionsReturnsMapWithPositionCountsOneAndTwo() {
    assertEquals(Map.of(new Point(0, 0), 2L, new Point(1, 1), 1L),
      GameOfLife.countSignals(List.of(new Point(0, 0), new Point(1, 1), new Point(0, 0))));
  }

  @Test
  void noPositionsReturnsAnEmptyList() {
    assertEquals(List.of(), GameOfLife.nextGeneration(List.of()));
  }
  
  @Test
  void onePositionsReturnsAnEmptyList() {
    assertEquals(List.of(), GameOfLife.nextGeneration(List.of(new Point(2, 2))));
  }

  @Test
  void twoPositionsReturnsAnEmptyList() {
    assertEquals(List.of(), GameOfLife.nextGeneration(List.of(new Point(2, 3), new Point(2, 4))));
  }

  @Test
  void positions11_12_30_ReturnsListWith21() {
    assertEquals(List.of(new Point(2, 1)),
      GameOfLife.nextGeneration(List.of(new Point(1, 1), new Point(1, 2), new Point(3, 0))));
  }

  List<Point> sort(List<Point> points) {
    return points.stream()
      .sorted(comparing(Point::getX).thenComparing(Point::getY))
      .collect(toList());
  }

  @Test
  void positions11_12_22_ReturnsListWith11_12_21_22() {
    var newGeneration = GameOfLife.nextGeneration(List.of(
      new Point(1, 1), new Point(1, 2), new Point(2, 2)));

    assertEquals(List.of(new Point(1, 1), new Point(1, 2), new Point(2, 1), new Point(2, 2)),
      sort(newGeneration));
  }

  @Test
  void aBlockReturnTheSameBlock() {
    var newGeneration = GameOfLife.nextGeneration(List.of(new Point(1, 1), new Point(1, 2),
      new Point(2, 1), new Point(2, 2)));

    assertEquals(List.of(new Point(1, 1), new Point(1, 2), new Point(2, 1), new Point(2, 2)),
      sort(newGeneration));
  }

  @Test
  void aBeehiveReturnTheSameBeehive() {
    var newGeneration = GameOfLife.nextGeneration(List.of(new Point(-1, 1), new Point(-1, 2),
      new Point(0, 0), new Point(0, 3), new Point(1, 1), new Point(1, 2)));

    assertEquals(List.of(new Point(-1, 1), new Point(-1, 2), new Point(0, 0), new Point(0, 3),
      new Point(1, 1), new Point(1, 2)), sort(newGeneration));
  }

  @Test
  void horizontalBlinkerReturnVerticalBlinker() {
    var newGeneration = GameOfLife.nextGeneration(List.of(new Point(1, 1), new Point(1, 2), new Point(1, 3)));

    assertEquals(List.of(new Point(0, 2), new Point(1, 2), new Point(2, 2)), sort(newGeneration));
  }

  @Test
  void verticalBlinkerReturnHorizontalBlinker() {
    var newGeneration = GameOfLife.nextGeneration(List.of(new Point(0, 2), new Point(1, 2), new Point(2, 2)));

    assertEquals(List.of(new Point(1, 1), new Point(1, 2), new Point(1, 3)), sort(newGeneration));
  }

  @Test
  void gliderIteratesOneCorrectStep() {
    var newGeneration = GameOfLife.nextGeneration(List.of(new Point(-1, 1), new Point(-1, 2),
      new Point(0, 0), new Point(0, 2), new Point(1, 2)));

    assertEquals(List.of(new Point(-1, 1), new Point(-1, 2), new Point(0, 2),
      new Point(0, 3), new Point(1, 1)), sort(newGeneration));
  }

  @Test
  void anEmptyListReturnsAnEmptyList() {
    assertEquals(List.of(), GameOfLife.getBounds(List.of()));
  }

  @Test
  void onePointsReturnsTwoPointsThatAreTheSame() {
    assertEquals(List.of(new Point(0, 0), new Point(0, 0)), GameOfLife.getBounds(List.of(new Point(0, 0))));
  }

  @Test
  void twoPointsReturnsTopLeftFirstBottomRightSecond() {
    assertEquals(List.of(new Point(0, 0), new Point(2, 2)),
      GameOfLife.getBounds(List.of(new Point(2, 2), new Point(0, 0))));
  }

  @Test
  void threePointsReturnsTopLeftFirstBottomRightSecond() {
    assertEquals(List.of(new Point(1, 1), new Point(4, 5)),
      GameOfLife.getBounds(List.of(new Point(1, 1), new Point(4, 5), new Point(3, 2))));
  }

  @Test
  void fourPointsReturnsTopLeftFirstBottomRightSecond() {
    assertEquals(List.of(new Point(1, 1), new Point(4, 4)),
      GameOfLife.getBounds(List.of(new Point(1, 1), new Point(2, 3), new Point(3, 3), new Point(4, 4))));
  }
  
  @Test
  void checkBoundsAreCorrect() {
    assertEquals(List.of(new Point(1, 1), new Point(5, 8)),
      GameOfLife.getBounds(
        List.of(new Point(3, 1), new Point(1, 3), new Point(5, 3), new Point(4, 8))));
  }
}
