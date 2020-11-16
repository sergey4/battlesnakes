## Task 1
* increased Gradle version to make project compatible with JDK 14+
* SnakeUtilTest - implemented getRandomMove test
* SnakeUtil - implemented getRandomMove() method to satisfy the test

## Task 2
* SnakeUtil - implemented getAllowedMoves() method

## Task 3
* SnakeUtil - implemented remaining methods so that all tests in SnakeUtilTest pass
and "smart" snake works

## Task 4
* SnakeUtil / GeniusSnakeController - implemented slightly smarter snake logic
  * Got some ideas from here <https://rdbrck.com/news/battlesnake-2018-retrospective/> (Shiffany snake) 
  * uses A* (A-Star) search algorithm to find path to food
  * uses fake heads, but only to reduce risk of collision with another snake
