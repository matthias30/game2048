package spw4.game2048;

import java.util.Arrays;
import java.util.Random;
import java.util.StringJoiner;

public class Game {

  public static final double PROBABILITY_OF_TWO = 0.9;
  public static final int WINNING_VALUE = 2048;
  private int[][] board;
  private Random random;
  private int score = 0;
  private int countMoves = 0;

  public Game() {
    board = new int[4][4];
    random = new Random();
  }

  public Game(Random random) {
    board = new int[4][4];
    this.random = random;
  }

  public int getScore() {
    return score;
  }

  public boolean isOver() {
    if (canMove(Direction.up)) return false;
    if (canMove(Direction.down)) return false;
    if (canMove(Direction.left)) return false;
    if (canMove(Direction.right)) return false;
    return true;
  }

  private boolean canMove(Direction direction) {
    int[][] tmpBoard = copyBoard();
    move(direction);
    if (!boardsEqual(tmpBoard)) {
      this.board = tmpBoard;
      return true;
    }
    return false;
  }

  public boolean isWon() {
    for(int row = 0; row < board.length; row++){
      for(int col = 0; col < board.length; col++){
        if(board[row][col] >= WINNING_VALUE){
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public String toString() {
    StringJoiner joiner = new StringJoiner(String.format("%n"), "", "");
    for (int i = 0; i < board.length; i++) {
      StringJoiner row = new StringJoiner("", "", "");
      for (int j = 0; j < board.length; j++) {
        row.add(String.format("%5d", board[i][j]));
      }
      joiner.add(row.toString());
    }
    return joiner.toString();
  }

  public void initialize() {
    clearBoard();
    generateTile();
    generateTile();
  }

  public void clearBoard() {
    for (int row = 0; row < board.length; row++) {
      for (int col = 0; col < board.length; col++) {
        board[row][col] = 0;
      }
    }
  }

  private void generateTile() {
    if (allTilesSet()) return;
    int randValue = random.nextDouble() <= PROBABILITY_OF_TWO ? 2 : 4;
    int randRow = Math.abs(random.nextInt()) % board.length;
    int randCol = Math.abs(random.nextInt()) % board.length;

    while (positionInUse(randRow, randCol)) {
      randRow++;
      randRow %= board.length;
      if (randRow == 0) {
        randCol++;
        randCol %= board.length;
      }
    }
    board[randRow][randCol] = randValue;
  }

  private boolean allTilesSet() {
    for (int row = 0; row < board.length; row++) {
      for (int col = 0; col < board.length; col++) {
        if (board[row][col] == 0) {
          return false;
        }
      }
    }
    return true;
  }

  private boolean positionInUse(int row, int col) {
    return board[row][col] != 0;
  }

  private int[][] copyBoard(){
    int[][] tmpBoard = new int[board.length][board.length];
    for(int row = 0; row < board.length; row++){
      for(int col = 0; col < board.length; col++){
        tmpBoard[row][col] = this.board[row][col];
      }
    }
    return tmpBoard;
  }

  private boolean boardsEqual(int[][] otherBoard){
    for(int row = 0; row < board.length; row++){
      for(int col = 0; col < board.length; col++){
        if(board[row][col] != otherBoard[row][col]){
          return false;
        }
      }
    }
    return true;
  }

  public void move(Direction direction) {
    int[][] tmpBoard = copyBoard();

    switch (direction) {
      case up:
        moveUp();
        mergeUp();
        moveUp();
        break;
      case down:
        moveDown();
        mergeDown();
        moveDown();
        break;
      case left:
        moveLeft();
        mergeLeft();
        moveLeft();
        break;
      case right:
        moveRight();
        mergeRight();
        moveRight();
        break;
    }
    if(!boardsEqual(tmpBoard)){
      generateTile();
      countMoves++;
    }
  }

  private void mergeUp(){
    for(int col = 0; col < board.length; col++){
      for(int row = 0; row < board.length - 1; row++){
        if(board[row][col] == board[row + 1][col]){
          board[row][col] *= 2;
          score += board[row][col];
          board[row + 1][col] = 0;
        }
      }
    }
  }

  private void mergeDown(){
    for(int col = 0; col < board.length; col++){
      for(int row = board.length - 1; row >= 1; row--){
        if(board[row][col] == board[row - 1][col]){
          board[row][col] *= 2;
          score += board[row][col];
          board[row - 1][col] = 0;
        }
      }
    }
  }

  private void mergeLeft(){
    for(int row = 0; row < board.length; row++){
      for(int col = 0; col < board.length - 1; col++){
        if(board[row][col] == board[row][col + 1]){
          board[row][col] *= 2;
          score += board[row][col];
          board[row][col + 1] = 0;
        }
      }
    }
  }

  private void mergeRight(){
    for(int row = 0; row < board.length; row++){
      for(int col = board.length - 1; col >= 1; col--){
        if(board[row][col] == board[row][col - 1]){
          board[row][col] *= 2;
          score += board[row][col];
          board[row][col - 1] = 0;
        }
      }
    }
  }

  private void moveRight() {
    for (int col = board.length - 1; col >= 0; col--) {
      for (int row = 0; row < board.length; row++) {
        if (positionInUse(row, col)) {
          int newCol = board.length - 1;
          while (newCol > 0 && positionInUse(row, newCol)) {
            newCol--;
          }
          if (newCol > col) {
            board[row][newCol] = board[row][col];
            board[row][col] = 0;
          }
        }
      }
    }
  }

  private void moveLeft() {
    for (int col = 0; col < board.length; col++) {
      for (int row = 0; row < board.length; row++) {
        if (positionInUse(row, col)) {
          int newCol = 0;
          while (newCol < board.length && positionInUse(row, newCol)) {
            newCol++;
          }
          if (newCol < col) {
            board[row][newCol] = board[row][col];
            board[row][col] = 0;
          }
        }
      }
    }
  }

  private void moveDown() {
    for (int row = board.length - 1; row >= 0; row--) {
      for (int col = 0; col < board.length; col++) {
        if (positionInUse(row, col)) {
          int newRow = board.length - 1;
          while (newRow > 0 && positionInUse(newRow, col)) {
            newRow--;
          }
          if(newRow > row){
            board[newRow][col] = board[row][col];
            board[row][col] = 0;
          }
        }
      }
    }
  }

  private void moveUp() {
    for (int row = 0; row < board.length; row++) {
      for (int col = 0; col < board.length; col++) {
        if (positionInUse(row, col)) {
          int newRow = 0;
          while (newRow < board.length && positionInUse(newRow, col)) {
            newRow++;
          }
          if(newRow < row){
            board[newRow][col] = board[row][col];
            board[row][col] = 0;
          }
        }
      }
    }
  }

  public int getValueAt(int row, int col) {
    if(row < 0 || row > board.length - 1 || col < 0 || col > board.length - 1) throw new IllegalArgumentException();
    return board[row][col];
  }

  public int getMoves() {
    return countMoves;
  }
}