# Maze solver - Java8

This is college's 2nd year group project. Decided to implement it with java8 to try and experiment with the new features of the language.
Contains multiple different datastructures as part of the assigment specs, but decided to do few more.
And few more solvers as well, one of the solvers is multithreaded, because just small portion was possible to execute in concurent fassion, lot of overhead cause it not perform as well. Partly this was expected, but wanted to experiment with it anyway.
More information is in readMe.pdf.

![Title screen image]
(https://raw.githubusercontent.com/truhlikfredy/commitsFromPrivateRepos/master/screenShotsM/00.jpg)

# Features
* Bread first, deep first, Astar and multithreaded Astar solvers.
  * Astar can consider or ignore heurestics in decision making.
* Option to use Java or own implementation of stacks and queues.
* Able to open mazes up to 600x600 blocks (if needed this size cap can be disabled).
* Simple maze generator.
* Few mazes files suplied.
* When build all graphics and required resources are build into jar.
* Tested on Windows / Linux.
* JUint tests:
  *  Datastructures coverage.
  *  All solvers coverage.
* Benchmark to compare performance between implementations.
* JavaDoc.

![solved maze]
(https://raw.githubusercontent.com/truhlikfredy/commitsFromPrivateRepos/master/screenShotsM/01.jpg)

# Inheritance between solvers

![packages]
(https://raw.githubusercontent.com/truhlikfredy/commitsFromPrivateRepos/master/screenShotsM/06.png)

# Metric statistics

Metric               | Total  | Mean  | Std. Dev.  
:--------------------| ------:| -----:| ----------:
Cyclomatic Complexity|        |   1.6 |        1.3 
Nested Block Depth   |        |   0.5 |        0.8 
Packages             |      3 |       |            
Classes              |     42 |       |            
Methods              |    287 |       |            
Lines of code        |   2368 |       |            

# Different solvers can find different solution path

![solvers]
(https://raw.githubusercontent.com/truhlikfredy/commitsFromPrivateRepos/master/screenShotsM/02.png)

![solvers]
(https://raw.githubusercontent.com/truhlikfredy/commitsFromPrivateRepos/master/screenShotsM/03.png)

![solvers]
(https://raw.githubusercontent.com/truhlikfredy/commitsFromPrivateRepos/master/screenShotsM/04.png)

