package com.battle.snakes.util;


import com.battle.snakes.game.*;

import java.util.*;

public class SnakeUtil {

  public static class TargetPath {

    private final boolean reachable;
    private final MoveType move;
    private final int distance;
    private final int weight;

    private TargetPath(boolean reachable, MoveType move, int distance, int weight) {
      this.reachable = reachable;
      this.move = move;
      this.distance = distance;
      this.weight = weight;
    }

    private TargetPath(){
      this(false, MoveType.LEFT, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    public boolean isReachable(){
      return reachable;
    }

    public MoveType getMove(){
      return move;
    }

    public int getDistance(){
      return distance;
    }

    public int getWeight() {
      return weight;
    }

  }

  private static final Random RANDOM = new Random();

  private static final int HEAD_PADDING_WEIGHT = 25;

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
    List<Coordinate> forbiddenCoordinates = getForbiddenCoordinates(request);
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

  private static List<Coordinate> getForbiddenCoordinates(MoveRequest moveRequest){
    ArrayList<Coordinate> coordinates = new ArrayList<>();
    for (Snake snake : moveRequest.getBoard().getSnakes()){
      coordinates.addAll(snake.getBody());
    }
    // probably not needed
    addMissingElements(coordinates, moveRequest.getYou().getBody());
    return coordinates;
  }

  public static TargetPath getBestPathToTarget(MoveRequest moveRequest, List<Coordinate> targets){
    if (targets.isEmpty()) {
      return new TargetPath();
    }
    TargetPath bestPath = new TargetPath();
    Coordinate head = moveRequest.getYou().getBody().get(0);
    TargetPath tailPath = getBestPathToTail(moveRequest);
    for (Coordinate target : targets){
      TargetPath currentPath = getAStarPathToTarget(moveRequest, head, target);
      if (currentPath.isReachable() && (currentPath.getWeight() < bestPath.getWeight())){
        boolean trap = tailPath.isReachable() &&
                isPotentialTrap(moveRequest, getNextMoveCoords(currentPath.getMove(), head));
        if (!trap)
          bestPath = currentPath;
      }
    }
    return bestPath;
  }

  public static TargetPath getBestPathToTail(MoveRequest moveRequest){
    return getBestPathToTail(moveRequest, moveRequest.getYou().getBody().get(0));
  }

  private static TargetPath getBestPathToTail(MoveRequest moveRequest, Coordinate start){
    List<Coordinate> body = moveRequest.getYou().getBody();
    Coordinate head = body.get(0);
    Coordinate tail = body.get(body.size() - 1);
    List<Coordinate> forbiddenCoordinates = getForbiddenCoordinates(moveRequest);
    List<Coordinate> additionalForbiddenCoordinates = new ArrayList<>();
    if (!head.equals(start)){
      additionalForbiddenCoordinates.add(start);
      forbiddenCoordinates.add(start);
    }

    List<Coordinate> tailNeighbors = getTailNeighbors(moveRequest, forbiddenCoordinates, tail);

    TargetPath bestPath = new TargetPath();
    for (Coordinate current : tailNeighbors){
      TargetPath currentPath = getAStarPathToTarget(moveRequest, start, current, additionalForbiddenCoordinates);
      if (currentPath.isReachable() && currentPath.getDistance() < bestPath.getWeight())
        bestPath = currentPath;
    }
    return bestPath;
  }

  private static List<Coordinate> getTailNeighbors(MoveRequest moveRequest,
                                                   List<Coordinate> forbiddenCoordinates,
                                                   Coordinate tail){
    ArrayList<Coordinate> fakeTailCoords = new ArrayList<>();
    for (MoveType move : MoveType.values()){
      Coordinate current = getNextMoveCoords(move, tail);
      if (isInBounds(moveRequest.getBoard(), current) && !forbiddenCoordinates.contains(current)){
        fakeTailCoords.add(current);
      }
    }
    return fakeTailCoords;
  }

  // checks whether we may lose ability to go back to the tail if we make 1 step towards the target
  private static boolean isPotentialTrap(MoveRequest moveRequest, Coordinate start){
    TargetPath tailPath = getBestPathToTail(moveRequest, start);
    return !tailPath.isReachable();
  }

  private static TargetPath getAStarPathToTarget(MoveRequest moveRequest, Coordinate start, Coordinate target){
    return getAStarPathToTarget(moveRequest, start, target, new ArrayList<>());
  }

  // finds path to target using A* search algorithm
  private static TargetPath getAStarPathToTarget(MoveRequest moveRequest,
                                                 Coordinate start,
                                                 Coordinate target,
                                                 List<Coordinate> additionalForbiddenCoords){
    List<Coordinate> forbiddenCoordinates = getForbiddenCoordinates(moveRequest);
    addMissingElements(forbiddenCoordinates, additionalForbiddenCoords);

    Map<Coordinate, Integer> headWeights = getHeadPaddingWeightsForAll(moveRequest, forbiddenCoordinates);
    List<Coordinate> openSet = new ArrayList<>();
    openSet.add(start);

    Map<Coordinate, Coordinate> cameFrom = new HashMap<>();
    Map<Coordinate, Integer> gScore = new HashMap<>();
    gScore.put(start, 0);

    Map<Coordinate, Double> fScore = new HashMap<>();
    fScore.put(start, getDistance(start, target));

    while (!openSet.isEmpty()){
      Coordinate current = getNodeWithLowestFScore(openSet, fScore);
      if (current.equals(target)){
        List<Coordinate> pathPoints = getPathPoints(cameFrom, current);
        MoveType move = getNearestMoveToTarget(
                pathPoints.get(pathPoints.size() - 1),
                start,
                getAllowedMoves(moveRequest)
        );
        return new TargetPath(true, move, pathPoints.size(),
                gScore.getOrDefault(target, Integer.MAX_VALUE));
      }
      openSet.remove(current);

      for (Coordinate neighbor : getNeighbors(moveRequest.getBoard(), forbiddenCoordinates, current)){
        Integer tentativeGScore = gScore.get(current) + getWeight(current, neighbor, headWeights);
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
    return new TargetPath();
  }

  private static void addMissingElements(List<Coordinate> toList, List<Coordinate> fromList){
    for (Coordinate coordinate : fromList){
      if (!toList.contains(coordinate))
        toList.add(coordinate);
    }
  }

  private static int getWeight(Coordinate current, Coordinate neighbor, Map<Coordinate, Integer> weights){
    int weight = Math.abs(current.getX() - neighbor.getX()) + Math.abs(current.getY() - neighbor.getY());
    return weight + weights.getOrDefault(neighbor, 0);
  }

  private static List<Coordinate> getPathPoints(Map<Coordinate, Coordinate> cameFrom, Coordinate current){
    List<Coordinate> points = new ArrayList<>();
    while (cameFrom.containsKey(current)){
      points.add(current);
      current=cameFrom.get(current);
    }
    return points;
  }

  private static Coordinate getNodeWithLowestFScore(List<Coordinate> openSet, Map<Coordinate, Double> fScore){
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

  private static Map<Coordinate, Integer> getHeadPaddingWeightsForAll(MoveRequest moveRequest,
                                                                      List<Coordinate> forbiddenCoordinates){
    Map<Coordinate, Integer> weightMap = new HashMap<>();
    Coordinate mySnakeHead = moveRequest.getYou().getBody().get(0);
    for (Snake snake : moveRequest.getBoard().getSnakes()){
      if (snake.getBody().get(0).equals(mySnakeHead))
        continue;
      Map<Coordinate, Integer> snakeWeightMap = getHeadPaddingWeightsForSnake(
              moveRequest.getBoard(),
              snake.getBody(),
              forbiddenCoordinates
      );
      weightMap.putAll(snakeWeightMap);
    }
    return weightMap;
  }

  private static Map<Coordinate, Integer> getHeadPaddingWeightsForSnake(Board board,
                                                                    List<Coordinate> snakeBody,
                                                                    List<Coordinate> forbiddenCoordinates){
    Map<Coordinate, Integer> weightMap = new HashMap<>();
    if (snakeBody.size() > 1){
      Coordinate head = snakeBody.get(0);
      for (MoveType move : MoveType.values()){
        Coordinate current = getNextMoveCoords(move, head);
        if (isInBounds(board, current) && !forbiddenCoordinates.contains(current)){
          weightMap.put(current, HEAD_PADDING_WEIGHT);
        }
      }
    }
    return weightMap;
  }

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
