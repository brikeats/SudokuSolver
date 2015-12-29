# SudokuSolver

This is an Android app that solves sudoku puzzles. It doesn't just present the solution to a preset number of 
puzzles -- rather it allows the user to enter an arbitrary puzzle and finds the solution via search. The solver
is a rather literal Java translation of [Peter Norvig's python code](http://norvig.com/sudoku.html). If there is 
no solution (i.e., there is a contradiction), the user is informed. If there are multiple solutions, it will 
select one more-or-less randomly.

## TODO
* Allow user to take a photo from which the puzzle is extracted
* Rather than showing entire solution, allow user to select which squares to reveal the correct answer.
* Improve UI & test on a few devices

