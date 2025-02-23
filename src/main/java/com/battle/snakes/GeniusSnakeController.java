package com.battle.snakes;

import com.battle.snakes.game.*;
import com.battle.snakes.util.SnakeUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/genius")
public class GeniusSnakeController extends BaseController {

  @RequestMapping(value = "/start", method = RequestMethod.POST, produces = "application/json")
  public StartResponse start(@RequestBody StartRequest request) {

    log.info(request.toString());

    return StartResponse.builder()
      .color("#188936")
      .headType(HeadType.BELUGA.getValue())
      .tailType(TailType.BOLT.getValue())
      .build();
  }

  @RequestMapping(value = "/end", method = RequestMethod.POST)
  public Object end(@RequestBody EndRequest request) {

    log.info(request.toString());

    return new HashMap<String, Object>();
  }

  @RequestMapping(value = "/move", method = RequestMethod.POST, produces = "application/json")
  public MoveResponse move(@RequestBody MoveRequest request) {

    log.info(request.toString());
    List<Coordinate> food = request.getBoard().getFood();

    SnakeUtil.TargetPath targetPath = SnakeUtil.getBestPathToTarget(request, food);
    if (targetPath.isReachable())
      return getMoveResponse(targetPath.getMove());

    // if no food is reachable, try to chase tail
    SnakeUtil.TargetPath tailPath = SnakeUtil.getBestPathToTail(request);
    if (tailPath.isReachable())
      return getMoveResponse(tailPath.getMove());

    // tail is not reachable, just move somehow
    List<MoveType> moves = SnakeUtil.getAllowedMoves(request);
    if (!moves.isEmpty())
      return getMoveResponse(moves.get(0));

    // seems that we're trapped :(
    return getMoveResponse(MoveType.LEFT);
  }

  private MoveResponse getMoveResponse(MoveType move){
    return MoveResponse.builder()
            .move(move.getValue())
            .build();
  }
}
