package spw4.game2048;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class GameTest {

  Game game;

  @BeforeEach
  void setUp() {
    game = new Game();
  }

  @Nested
  class InitializerTests {
    @Test
    void getScore() {
      assertEquals(0, game.getScore());
    }

    @Test
    void getTileWithValidIndex() {
      assertDoesNotThrow(() -> {
        game.getValueAt(0, 0);
      });
    }

    @Test
    void getTileWithInvalidIndex() {
      assertAll(
              () -> assertThrows(IllegalArgumentException.class, () -> {
                game.getValueAt(-1, 2);
              }),
              () -> assertThrows(IllegalArgumentException.class, () -> {
                game.getValueAt(2, 4);
              })
              );
    }

    @Test
    void InitializedBoardTwoTilesAreSet() {
      game.initialize();

      int countTiles = 0;

      for (int row = 0; row < 4; row++) {
        for (int col = 0; col < 4; col++) {
          if (game.getValueAt(row, col) != 0) {
            countTiles++;
          }
        }
      }
      assertEquals(2, countTiles);
    }

    @Test
    void InitializedBoardOnlyContainsTwoOrFour() {
      game.initialize();

      for (int row = 0; row < 4; row++) {
        for (int col = 0; col < 4; col++) {
          if (game.getValueAt(row, col) == 0) {
            continue;
          }
          assertTrue(game.getValueAt(row, col) == 0
                  || game.getValueAt(row, col) == 2
                  || game.getValueAt(row, col) == 4);
        }
      }
    }

    @Test
    void InitializedBoardChanceOfTwoAroundNintetyPersent() {
      int twos = 0;
      int fours = 0;

      for (int i = 0; i < 1000; i++) {
        game.initialize();
        for (int row = 0; row < 4; row++) {
          for (int col = 0; col < 4; col++) {
            if (game.getValueAt(row, col) == 2) {
              twos++;
            } else if (game.getValueAt(row, col) == 4) {
              fours++;
            }
          }
        }
      }

      double twoChance = twos / 2000.0;
      double fourChance = fours / 2000.0;
      assertAll(
              () -> assertEquals(1.9, twoChance, 0.05),
              () -> assertEquals(0.1, fourChance, 0.05)
      );
    }

    @Test
    void InitializedBoardValuesOnDifferentPositions() {
      int[][] hits = new int[4][4];

      for (int i = 0; i < 1000; i++) {
        game.initialize();
        for (int row = 0; row < 4; row++) {
          for (int col = 0; col < 4; col++) {
            if (game.getValueAt(row, col) != 0) {
              hits[row][col]++;
            }
          }
        }
      }

      int countHittedPositions = 0;
      for (int row = 0; row < 4; row++) {
        for (int col = 0; col < 4; col++) {
          if (hits[row][col] != 0) {
            countHittedPositions++;
          }
        }
      }

      assertTrue(countHittedPositions > 2);
    }

    @Test
    void BoardOnlyContainsPowerOfTwo() {
      game.initialize();

      for (int row = 0; row < 4; row++) {
        for (int col = 0; col < 4; col++) {
          if (game.getValueAt(row, col) == 0) {
            continue;
          }
          assertEquals(0, (game.getValueAt(row, col) & game.getValueAt(row, col) - 1));
        }
      }
    }
  }

  @ExtendWith(MockitoExtension.class)
  @Nested
  class MoveTestsDifferentRowAndCol {

    @Mock
    private Random random;

    @BeforeEach
      // 0  0  0  0
      // 0  2  0  0
      // 0  0  4  0
      // 0  0  0  0
    void setUpFixBoard() {
      game = new Game(random);
      when(random.nextDouble()).thenReturn(0.8).thenReturn(0.95);
      when(random.nextInt()).thenReturn(1).thenReturn(1).thenReturn(2).thenReturn(2);
      game.initialize();
    }

    @Test
    void moveUp() {
      game.move(Direction.up);
      assertEquals(2, game.getValueAt(0, 1));
      assertEquals(4, game.getValueAt(0, 2));
      assertEquals(0, game.getValueAt(1, 1));
      assertEquals(4, game.getValueAt(2, 2));
    }

    @Test
    void moveDown() {
      game.move(Direction.down);
      assertEquals(2, game.getValueAt(3, 1));
      assertEquals(4, game.getValueAt(3, 2));
      assertEquals(0, game.getValueAt(1, 1));
      assertEquals(4, game.getValueAt(2, 2));
    }

    @Test
    void moveLeft() {
      game.move(Direction.left);
      assertEquals(2, game.getValueAt(1, 0));
      assertEquals(4, game.getValueAt(2, 0));
      assertEquals(0, game.getValueAt(1, 1));
      assertEquals(4, game.getValueAt(2, 2));
    }

    @Test
    void moveRight() {
      game.move(Direction.right);
      assertEquals(2, game.getValueAt(1, 3));
      assertEquals(4, game.getValueAt(2, 3));
      assertEquals(0, game.getValueAt(1, 1));
      assertEquals(4, game.getValueAt(2, 2));
    }
  }

  @ExtendWith(MockitoExtension.class)
  @Nested
  class MoveTestsSameRowAndDifferentCol {
    @Mock
    private Random random;

    @BeforeEach
      // 0  0  0  0
      // 0  2  4  0
      // 0  0  0  0
      // 0  0  0  0
    void setUpFixBoard() {
      game = new Game(random);
      when(random.nextDouble()).thenReturn(0.8).thenReturn(0.95);
      when(random.nextInt()).thenReturn(1).thenReturn(1).thenReturn(1).thenReturn(2);
      game.initialize();
    }

    @Test
    void moveUp() {
      game.move(Direction.up);
      assertEquals(2, game.getValueAt(0, 1));
      assertEquals(4, game.getValueAt(0, 2));
      assertEquals(0, game.getValueAt(1, 1));
      assertEquals(4, game.getValueAt(2, 2));
    }

    @Test
    void moveDown() {
      game.move(Direction.down);
      assertEquals(2, game.getValueAt(3, 1));
      assertEquals(4, game.getValueAt(3, 2));
      assertEquals(0, game.getValueAt(1, 1));
      assertEquals(4, game.getValueAt(2, 2));
    }

    @Test
    void moveLeft() {
      game.move(Direction.left);
      assertEquals(2, game.getValueAt(1, 0));
      assertEquals(4, game.getValueAt(1, 1));
      assertEquals(0, game.getValueAt(1, 2));
    }

    @Test
    void moveRight() {
      game.move(Direction.right);
      assertEquals(2, game.getValueAt(1, 2));
      assertEquals(4, game.getValueAt(1, 3));
      assertEquals(0, game.getValueAt(1, 1));
    }
  }

  @ExtendWith(MockitoExtension.class)
  @Nested
  class MoveTestsDifferentRowAndSameCol {
    @Mock
    private Random random;

    @BeforeEach
      // 0  0  0  0
      // 0  2  0  0
      // 0  4  0  0
      // 0  0  0  0
    void setUpFixBoard() {
      game = new Game(random);
      when(random.nextDouble()).thenReturn(0.8).thenReturn(0.95);
      when(random.nextInt()).thenReturn(1).thenReturn(1).thenReturn(2).thenReturn(1);
      game.initialize();
    }

    @Test
    void moveUp() {
      game.move(Direction.up);
      assertEquals(2, game.getValueAt(0, 1));
      assertEquals(4, game.getValueAt(1, 1));
      assertEquals(4, game.getValueAt(2, 1));
    }

    @Test
    void moveDown() {
      game.move(Direction.down);
      assertEquals(2, game.getValueAt(2, 1));
      assertEquals(4, game.getValueAt(3, 1));
      assertEquals(4, game.getValueAt(1, 1));
    }

    @Test
    void moveLeft() {
      game.move(Direction.left);
      assertEquals(2, game.getValueAt(1, 0));
      assertEquals(4, game.getValueAt(2, 0));
      assertEquals(4, game.getValueAt(1, 1));
      assertEquals(0, game.getValueAt(2, 2));
    }

    @Test
    void moveRight() {
      game.move(Direction.right);
      assertEquals(2, game.getValueAt(1, 3));
      assertEquals(4, game.getValueAt(2, 3));
      assertEquals(4, game.getValueAt(1, 1));
      assertEquals(0, game.getValueAt(2, 2));
    }
  }

  @ExtendWith(MockitoExtension.class)
  @Nested
  class MoveTestsFullRow {
    @Mock
    private Random random;

    @BeforeEach
      // 0  0  0  0
      // 2  4  2  4
      // 0  0  0  0
      // 0  0  0  0
    void setUpFixBoard() {
      game = new Game(random);
      when(random.nextDouble()).thenReturn(0.8).thenReturn(0.95).thenReturn(0.8).thenReturn(0.95);
    }

    @Test
    void moveUp() {
      when(random.nextInt()).thenReturn(1).thenReturn(0).thenReturn(1).thenReturn(1)
              .thenReturn(1).thenReturn(2).thenReturn(0).thenReturn(3);
      game.initialize();

      game.move(Direction.up);
      game.move(Direction.up);

      assertEquals(2, game.getValueAt(0, 0));
      assertEquals(4, game.getValueAt(0, 1));
      assertEquals(2, game.getValueAt(0, 2));

      assertEquals(0, game.getValueAt(1, 0));
      assertEquals(0, game.getValueAt(1, 1));
      assertEquals(0, game.getValueAt(1, 2));
      assertEquals(0, game.getValueAt(1, 3));
    }

    @Test
    void moveDown() {
      when(random.nextInt()).thenReturn(1).thenReturn(0).thenReturn(1).thenReturn(1)
              .thenReturn(1).thenReturn(2).thenReturn(3).thenReturn(3);
      game.initialize();

      game.move(Direction.down);
      game.move(Direction.down);

      assertEquals(2, game.getValueAt(3, 0));
      assertEquals(4, game.getValueAt(3, 1));
      assertEquals(2, game.getValueAt(3, 2));

      assertEquals(0, game.getValueAt(1, 0));
      assertEquals(0, game.getValueAt(1, 1));
      assertEquals(0, game.getValueAt(1, 2));
      assertEquals(0, game.getValueAt(1, 3));
    }

    @Test
    void moveLeft() {
      when(random.nextInt()).thenReturn(1).thenReturn(2).thenReturn(1).thenReturn(3)
              .thenReturn(1).thenReturn(2).thenReturn(1).thenReturn(0);
      game.initialize();

      game.move(Direction.left);
      game.move(Direction.right);
      game.move(Direction.left);

      assertEquals(4, game.getValueAt(1, 0));
      assertEquals(2, game.getValueAt(1, 1));
      assertEquals(4, game.getValueAt(1, 2));
      assertEquals(2, game.getValueAt(1, 3));

      game.move(Direction.left);
      assertEquals(4, game.getValueAt(1, 0));
      assertEquals(2, game.getValueAt(1, 1));
      assertEquals(4, game.getValueAt(1, 2));
      assertEquals(2, game.getValueAt(1, 3));
    }

    @Test
    void moveRight() {
      when(random.nextInt()).thenReturn(1).thenReturn(1).thenReturn(1).thenReturn(0)
              .thenReturn(1).thenReturn(0).thenReturn(1).thenReturn(0);
      game.initialize();

      game.move(Direction.right);
      game.move(Direction.right);

      assertEquals(4, game.getValueAt(1, 0));
      assertEquals(2, game.getValueAt(1, 1));
      assertEquals(4, game.getValueAt(1, 2));
      assertEquals(2, game.getValueAt(1, 3));

      game.move(Direction.right);
      assertEquals(4, game.getValueAt(1, 0));
      assertEquals(2, game.getValueAt(1, 1));
      assertEquals(4, game.getValueAt(1, 2));
      assertEquals(2, game.getValueAt(1, 3));
    }
  }

  @ExtendWith(MockitoExtension.class)
  @Nested
  class MoveTestsFullCol {
    @Mock
    private Random random;

    @BeforeEach
      // 0  2  0  0
      // 0  4  0  0
      // 0  2  0  0
      // 0  4  0  0
    void setUpFixBoard() {
      game = new Game(random);
      when(random.nextDouble()).thenReturn(0.8).thenReturn(0.95).thenReturn(0.8).thenReturn(0.95);
    }

    @Test
    void moveUp() {
      when(random.nextInt()).thenReturn(2).thenReturn(1).thenReturn(3).thenReturn(1)
              .thenReturn(3).thenReturn(1).thenReturn(3).thenReturn(1);
      game.initialize();

      game.move(Direction.up);
      game.move(Direction.up);

      assertEquals(2, game.getValueAt(0, 1));
      assertEquals(4, game.getValueAt(1, 1));
      assertEquals(2, game.getValueAt(2, 1));
      assertEquals(4, game.getValueAt(3, 1));
    }

    @Test
    void moveDown() {
      when(random.nextInt()).thenReturn(1).thenReturn(1).thenReturn(0).thenReturn(1)
              .thenReturn(0).thenReturn(1).thenReturn(0).thenReturn(1);
      game.initialize();

      game.move(Direction.down);
      game.move(Direction.down);

      assertEquals(4, game.getValueAt(0, 1));
      assertEquals(2, game.getValueAt(1, 1));
      assertEquals(4, game.getValueAt(2, 1));
      assertEquals(2, game.getValueAt(3, 1));
    }

    @Test
    void moveLeft() {
      when(random.nextInt()).thenReturn(0).thenReturn(2).thenReturn(1).thenReturn(2)
              .thenReturn(2).thenReturn(2).thenReturn(3).thenReturn(0);
      game.initialize();

      game.move(Direction.left);
      game.move(Direction.left);

      assertEquals(2, game.getValueAt(0, 0));
      assertEquals(4, game.getValueAt(1, 0));
      assertEquals(2, game.getValueAt(2, 0));
      assertEquals(4, game.getValueAt(3, 0));
    }

    @Test
    void moveRight() {
      when(random.nextInt()).thenReturn(0).thenReturn(1).thenReturn(1).thenReturn(1)
              .thenReturn(2).thenReturn(1).thenReturn(3).thenReturn(3);
      game.initialize();

      game.move(Direction.right);
      game.move(Direction.right);

      assertEquals(2, game.getValueAt(0, 3));
      assertEquals(4, game.getValueAt(1, 3));
      assertEquals(2, game.getValueAt(2, 3));
      assertEquals(4, game.getValueAt(3, 3));
    }
  }

  @ExtendWith(MockitoExtension.class)
  @Nested
  class MoveTestsWithMergeSameRowDifferentCol {
    @Mock
    private Random random;

    @BeforeEach
    void setUpFixBoard() {
      game = new Game(random);
      when(random.nextDouble()).thenReturn(0.8).thenReturn(0.8).thenReturn(0.95);
    }

    @Test
    void MoveUpMerge() {
      // 0  2  0  0
      // 0  0  0  0
      // 0  2  0  0
      // 0  0  0  0
      when(random.nextInt()).thenReturn(0).thenReturn(1).thenReturn(2).thenReturn(1).thenReturn(3).thenReturn(3);
      game.initialize();

      game.move(Direction.up);

      assertEquals(4, game.getValueAt(0, 1));
      assertEquals(0, game.getValueAt(2, 1));
    }

    @Test
    void MoveDownMerge() {
      // 0  2  0  0
      // 0  0  0  0
      // 0  2  0  0
      // 0  0  0  0
      when(random.nextInt()).thenReturn(0).thenReturn(1).thenReturn(2).thenReturn(1).thenReturn(3).thenReturn(3);
      game.initialize();

      game.move(Direction.down);

      assertEquals(4, game.getValueAt(3, 1));
      assertEquals(0, game.getValueAt(0, 1));
      assertEquals(0, game.getValueAt(2, 1));
    }

    @Test
      // 0  0  0  0
      // 0  0  0  0
      // 2  2  0  0
      // 0  0  0  0
    void MoveLeftMerge() {
      when(random.nextInt()).thenReturn(2).thenReturn(0).thenReturn(2).thenReturn(1).thenReturn(3).thenReturn(3);
      game.initialize();

      game.move(Direction.left);

      assertEquals(4, game.getValueAt(2, 0));
      assertEquals(0, game.getValueAt(2, 1));
    }

    @Test
      // 0  0  0  0
      // 0  0  0  0
      // 2  2  0  0
      // 0  0  0  0
    void MoveRightMerge() {
      when(random.nextInt()).thenReturn(2).thenReturn(0).thenReturn(2).thenReturn(1).thenReturn(3).thenReturn(3);
      game.initialize();

      game.move(Direction.right);

      assertEquals(4, game.getValueAt(2, 3));
      assertEquals(0, game.getValueAt(2, 0));
      assertEquals(0, game.getValueAt(2, 1));
    }
  }

  @ExtendWith(MockitoExtension.class)
  @Nested
  class ScoreTests {
    @Mock
    private Random random;

    @BeforeEach
    void setUpFixBoard() {
      game = new Game(random);
    }

    @Test
      // 0  0  0  0
      // 0  0  0  0
      // 2  2  0  0
      // 0  0  0  0
    void ScoreAfterMergeWithTwoTwos() {
      when(random.nextDouble()).thenReturn(0.8).thenReturn(0.8).thenReturn(0.95);
      when(random.nextInt()).thenReturn(2).thenReturn(0).thenReturn(2).thenReturn(1).thenReturn(3).thenReturn(3);
      game.initialize();

      game.move(Direction.right);

      assertEquals(4, game.getScore());
    }

    @Test
      // 0  0  0  0
      // 0  0  0  0
      // 2  2  0  0
      // 0  0  0  0
    void ScoreAfterSimpleGame() {
      when(random.nextDouble()).thenReturn(0.8);
      when(random.nextInt()).thenReturn(2).thenReturn(0).thenReturn(3).thenReturn(3)
              .thenReturn(0).thenReturn(0)
              .thenReturn(2).thenReturn(0)
              .thenReturn(2).thenReturn(2)
              .thenReturn(3).thenReturn(2)
              .thenReturn(2).thenReturn(2)
              .thenReturn(3).thenReturn(0);
      game.initialize();

      game.move(Direction.right);
      game.move(Direction.down);
      game.move(Direction.down);
      game.move(Direction.right);
      game.move(Direction.up);
      game.move(Direction.left);

      assertEquals(20, game.getScore());
      assertEquals(4, game.getValueAt(0, 0));
      assertEquals(8, game.getValueAt(1, 0));
      assertEquals(2, game.getValueAt(2, 0));
      assertEquals(2, game.getValueAt(3, 0));
    }
  }

  @Test
  void gameOverTest() {
    Random random = Mockito.mock(Random.class);
    game = new Game(random);

    when(random.nextDouble()).thenReturn(0.95);
    when(random.nextInt()).thenReturn(1).thenReturn(1).thenReturn(1).thenReturn(2).thenReturn(0);

    game.initialize();

    for (int i = 0; i < 17; i++) {
      for (int j = 0; j < 50; j++) {
        game.move(Direction.right);
      }
      game.move(Direction.down);
      game.move(Direction.down);
      game.move(Direction.down);
    }
    assertTrue(game.isOver());
  }

  @Test
  void wonTest() {
    Random random = Mockito.mock(Random.class);
    game = new Game(random);

    when(random.nextDouble()).thenReturn(0.95);
    when(random.nextInt()).thenReturn(1).thenReturn(1).thenReturn(1).thenReturn(2).thenReturn(0);

    game.initialize();

    for (int i = 0; i < 100; i++) {
      if (game.isOver()) {
        System.out.println("Gameover at: " + i);
      }
      game.move(Direction.up);
      game.move(Direction.left);
      game.move(Direction.right);
    }

    game.move(Direction.left);
    game.move(Direction.left);
    game.move(Direction.down);
    game.move(Direction.right);
    game.move(Direction.up);
    game.move(Direction.up);
    game.move(Direction.down);
    game.move(Direction.down);
    game.move(Direction.down);
    game.move(Direction.down);
    game.move(Direction.down);
    game.move(Direction.up);
    game.move(Direction.right);
    game.move(Direction.up);
    game.move(Direction.right);
    game.move(Direction.right);
    game.move(Direction.up);
    game.move(Direction.right);
    game.move(Direction.left);
    game.move(Direction.up);
    game.move(Direction.right);
    game.move(Direction.up);
    game.move(Direction.up);
    game.move(Direction.right);

    for (int i = 0; i < 90; i++) {
      if (game.isOver()) {
        System.out.println("Gameover at: " + i);
      }
      game.move(Direction.up);
      game.move(Direction.left);
      game.move(Direction.right);
    }
    game.move(Direction.down);
    game.move(Direction.right);
    game.move(Direction.down);
    game.move(Direction.right);
    game.move(Direction.up);
    game.move(Direction.left);
    game.move(Direction.up);
    game.move(Direction.right);
    game.move(Direction.right);

    assertTrue(game.isWon());
  }
}