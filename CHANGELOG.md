This is an example changelog, the purpose of it is to help us figure out faster what you worked
on and what you skipped.You will not be graded on how great this changelog is (or is not),
so make it simple. Delete the contents and write your own notes here.


-- TASK 1
* SnakeUtilTest - implemented getRandomMove test
* SnakeUtil - implemented getRandomMove() method to satisfy the test

-- TASK 2
* SnakeUtil - implemented getAllowedMoves() method

-- TASK 3
* SnakeUtil - implemented remaining methods so that all tests in SnakeUtilTest pass
and "smart" snake works

-- TASK 4
* SnakeUtil / GeniusSnakeController - implemented slightly smarter snake logic
  * Got some ideas from here <https://rdbrck.com/news/battlesnake-2018-retrospective/> (Shiffany snake) 
  * uses A* (A-Star) search algorithm to find path to food
  * uses fake heads, but only to reduce risk of collision with another snake
