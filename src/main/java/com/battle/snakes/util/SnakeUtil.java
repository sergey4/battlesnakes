package com.battle.snakes.util;


import com.battle.snakes.game.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SnakeUtil {

  private static final Random RANDOM = new Random();

  public static MoveType getRandomMove(List<MoveType> possibleMoves) {
    /*
     * Given all possible moves, picks a random move
     * */
    if (possibleMoves.isEmpty()) return getRandomMoveForEmptyParam();
    int randomIndex = RANDOM.nextInt (possibleMoves.size());
    return possibleMoves.get(randomIndex);
  }

  private static MoveType getRandomMoveForEmptyParam(){
      int randomIndex = RANDOM.nextInt(MoveType.values().length);
      return MoveType.values()[randomIndex];
  }

  public static boolean isInBounds(Board board, Coordinate coordinate) {
    /*
     * Given the game board, calculates if a coordinate is within the board
     * */
    if ((coordinate.getX()<0) || (coordinate.getX()>=board.getWidth()))
      return false;
    if ((coordinate.getY()<0) || (coordinate.getY()>=board.getHeight()))
      return false;
    return true;
  }

  public static Coordinate getNextMoveCoords(MoveType moveType, Coordinate start) {
    /*
     * Given the move type and the start coordinate, returns the coordinates of the next move
     * */
    int deltaX = 0;
    int deltaY = 0;
    switch (moveType) {
      case UP:
        deltaY = -1;
        break;
      case DOWN:
        deltaY = 1;
        break;
      case LEFT:
        deltaX = -1;
        break;
      case RIGHT:
        deltaX = 1;
        break;
    }
    return Coordinate.builder()
            .x(start.getX() + deltaX)
            .y(start.getY() + deltaY)
            .build();
  }

  public static List<MoveType> getAllowedMoves(MoveRequest request) {
    /*
     * Given the move request, returns a list of all the moves that do not end in the snake dieing
     * Hint: finding all the coordinates leading to the snakes death and
     * comparing it to the potential moves is a good starting point
     * */
    List<Coordinate> forbiddenCoordinates = getForbiddenCoordinateList(request);
    List<MoveType> allowedMoves = new ArrayList<>();
    Coordinate startPosition = request.getYou().getBody().get(0);

    for (MoveType move : MoveType.values()){
      Coordinate nextPosition = getNextMoveCoords(move, startPosition);
      boolean isMoveAllowed = isInBounds(request.getBoard(), nextPosition) &&
              !forbiddenCoordinates.contains(nextPosition);
      if (isMoveAllowed)
        allowedMoves.add(move);
    }
    return allowedMoves;
  }

  private static List<Coordinate> getForbiddenCoordinateList(MoveRequest moveRequest){
    ArrayList<Coordinate> coordinates = new ArrayList<>();
    for (Snake snake : moveRequest.getBoard().getSnakes()){
      coordinates.addAll(snake.getBody());
    }
    return coordinates;
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
