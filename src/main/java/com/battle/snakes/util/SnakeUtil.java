package com.battle.snakes.util;


import com.battle.snakes.game.*;

import java.util.*;

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
    return getForbiddenCoordinateList(moveRequest.getBoard());
  }

  private static List<Coordinate> getForbiddenCoordinateList(Board board){
    ArrayList<Coordinate> coordinates = new ArrayList<>();
    for (Snake snake : board.getSnakes()){
      coordinates.addAll(snake.getBody());
    }
    return coordinates;
  }

  public static int getAStarDistanceToTarget(Board board, Coordinate start, Coordinate target){
    if (start.equals(target)) return 0;
    List<Coordinate> forbiddenCoordinates = getForbiddenCoordinateList(board);
    List<Coordinate> openSet = new ArrayList<>();
    openSet.add(start);

    Map<Coordinate, Coordinate> cameFrom = new HashMap<>();
    Map<Coordinate, Integer> gScore = new HashMap<>();
    gScore.put(start, 0);

    Map<Coordinate, Double> fScore = new HashMap<>();
    fScore.put(start, getDistance(start, target));

    while (!openSet.isEmpty()){
      Coordinate current = getNodeWithLowestFscore(openSet, fScore);
      if (current.equals(target)){
        return getPathDistance(cameFrom, current);
      }
      openSet.remove(current);

      for (Coordinate neighbor : getNeighbors(board, forbiddenCoordinates, current)){
        Integer tentativeGScore = gScore.get(current) + getWeight(current, neighbor);
        if (tentativeGScore < gScore.getOrDefault(neighbor, Integer.MAX_VALUE)){
          cameFrom.put(neighbor, current);
          gScore.put(neighbor, tentativeGScore);
          fScore.put(neighbor, tentativeGScore + getDistance(neighbor, target));
          if (!openSet.contains(neighbor)){
            openSet.add(neighbor);
          }
        }
      }
    }

    // target is unreachable
    return -1;
  }

  private static int getWeight(Coordinate current, Coordinate neighbour){
    return Math.abs(current.getX() - neighbour.getX()) + Math.abs(current.getY() - neighbour.getY());
  }

  private static int getPathDistance(Map<Coordinate, Coordinate> cameFrom, Coordinate current){
    int totalDistance = 0;
    while (cameFrom.containsKey(current)){
      current=cameFrom.get(current);
      totalDistance++;
    }
    return totalDistance;
  }

  private static Coordinate getNodeWithLowestFscore(List<Coordinate> openSet, Map<Coordinate, Double> fScore){
    Double lowestScore = Double.MAX_VALUE;
    Coordinate lowestScoreNode = Coordinate.builder().build();
    for (Coordinate current : openSet){
      if (fScore.get(current) < lowestScore){
        lowestScore = fScore.get(current);
        lowestScoreNode = current;
      }
    }
    return lowestScoreNode;
  }

// TODO: naming consistency?
  private static List<Coordinate> getNeighbors(Board board, List<Coordinate> forbiddenCoordinates, Coordinate current){
    List<Coordinate> neighbors = new ArrayList<>();

    for (MoveType move : MoveType.values()){
      Coordinate nextPosition = getNextMoveCoords(move, current);
      boolean isMoveAllowed = isInBounds(board, nextPosition) &&
              !forbiddenCoordinates.contains(nextPosition);
      if (isMoveAllowed)
        neighbors.add(nextPosition);
    }
    return neighbors;
  }

  public static double getDistance(Coordinate first, Coordinate second) {
    /*
     * Given two coordinates on a 2D grid, calculates the distance between them
     * */
    if (first.equals(second)) return 0;
    double x = first.getX() - second.getX();
    double y = first.getY() - second.getY();
    return Math.sqrt(x * x + y * y);
  }

  public static MoveType getNearestMoveToTarget(Coordinate target, Coordinate current, List<MoveType> moves) {
    /*
     * Given the target coordinate, the current coordinate and a list of moves, returns
     * the nearest move to the target, selected from the moves list
     * */
    if ((target.getX() == null) || (target.getY() == null))
      getFallbackMove(moves);
    int deltaX = target.getX() - current.getX();
    int deltaY = target.getY() - current.getY();
    if (Math.abs(deltaX) > Math.abs(deltaY)) {
      if ((deltaX > 0) && moves.contains(MoveType.RIGHT))
        return MoveType.RIGHT;
      if ((deltaX < 0) && moves.contains(MoveType.LEFT))
        return MoveType.LEFT;
    }
    if ((deltaY > 0) && moves.contains(MoveType.DOWN))
      return MoveType.DOWN;
    if ((deltaY < 0) && moves.contains(MoveType.UP))
      return MoveType.UP;
    return getFallbackMove(moves);
  }

  public static Coordinate getNearestCoordinateToTarget(Coordinate target, List<Coordinate> coords) {
    /*
     * Given the target coordinate and a list of coordinates, finds the nearest coordinate to the target
     * */
    double nearestDistance = Double.MAX_VALUE;
    Coordinate nearestCoordinate = Coordinate.builder().build();
    for (Coordinate coordinate : coords){
      if (getDistance(coordinate, target) < nearestDistance){
        nearestDistance = getDistance(coordinate, target);
        nearestCoordinate = coordinate;
      }
    }
    return nearestCoordinate;
  }

  private static MoveType getFallbackMove(List<MoveType> moves){
    if (!moves.isEmpty()) return moves.get(0);
    return MoveType.LEFT;
  }

}
