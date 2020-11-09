package com.battle.snakes.util;


import com.battle.snakes.game.Board;
import com.battle.snakes.game.Coordinate;
import com.battle.snakes.game.MoveRequest;
import com.battle.snakes.game.MoveType;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SnakeUtil {

  private static final Random RANDOM = new Random();

  public static MoveType getRandomMove(List<MoveType> possibleMoves) {
    /* TODO
     * Given all possible moves, picks a random move
     * */
    return MoveType.LEFT;
  }

  public static boolean isInBounds(Board board, Coordinate coordinate) {
    /* TODO
     * Given the game board, calculates if a coordinate is within the board
     * */
    return true;
  }

  public static Coordinate getNextMoveCoords(MoveType moveType, Coordinate start) {
    /* TODO
     * Given the move type and the start coordinate, returns the coordinates of the next move
     * */
    return Coordinate.builder()
      .build();
  }

  public static List<MoveType> getAllowedMoves(MoveRequest request) {
    /* TODO
     * Given the move request, returns a list of all the moves that do not end in the snake dieing
     * Hint: finding all the coordinates leading to the snakes death and
     * comparing it to the potential moves is a good starting point
     * */
    return Collections.emptyList();
  }

  public static double getDistance(Coordinate first, Coordinate second) {
    /* TODO
     * Given two coordinates on a 2D grid, calculates the distance between them
     * */
    return 0;
  }

  public static MoveType getNearestMoveToTarget(Coordinate target, Coordinate current, List<MoveType> moves) {
    /* TODO
     * Given the target coordinate, the current coordinate and a list of moves, returns
     * the nearest move to the target, selected from the moves list
     * */
    return MoveType.LEFT;
  }

  public static Coordinate getNearestCoordinateToTarget(Coordinate target, List<Coordinate> coords) {
    /* TODO
     * Given the target coordinate and a list of coordinates, finds the nearest coordinate to the target
     * */
    return Coordinate.builder()
      .build();
  }
}
