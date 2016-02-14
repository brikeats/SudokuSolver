# SudokuSolver

This is an Android app that solves sudoku puzzles. It doesn't just present the solution to a preset number of 
puzzles -- rather it allows the user to enter an arbitrary puzzle and finds the solution via search. The solver
is a rather literal Java translation of [Peter Norvig's python code](http://norvig.com/sudoku.html). If there is 
no solution (i.e., there is a contradiction), the user is informed. If there are multiple solutions, it will 
select one more-or-less randomly.

## OpenCV install
I followed the directions [here](https://blog.hig.no/gtl/2015/10/01/android-studio-opencv/)
I had to install 'Android SDK Build Tools' from the SDK manager to get it to build correctly in Android Studio.
Note that this probably doesn't require the native libraries, but I copied them into the project anyway.
Opencv encourages use of their OpenCv manager. If it's not installed, it prompts the user to install it. It is possible to statically link opencv using [these instructions](http://docs.opencv.org/2.4/doc/tutorials/introduction/android_binary_package/dev_with_OCV_on_Android.html?highlight=static%2520initialization#application-development-with-static-initialization).

## Notes
I initially intended to use OpenCV's machine learning package to do the digit classification, since I had already have OpenCV included in the project. Unfortunately, there doesn't seem to be a way to load models from file in Java, which is probably [this bug](https://github.com/Itseez/opencv/issues/4969). Going forward, I'm going to use the [Weka](http://www.cs.waikato.ac.nz/ml/weka/) library. The jar file is only 7 MB, so including it in the package is no big deal. It also has a handy GUI that are useful for interactively trying different classifiers/parameters.

### Training
I'm using the MNIST data [here](http://www.cs.ubc.ca/labs/beta/Projects/autoweka/datasets/) for training. I modified [this script](https://weka.wikispaces.com/Converting+CSV+to+ARFF) to convert the arff files to csv. In a spreadsheet, I manually deleted the instances with 0's. (Sudoku only uses the digits 1-9). The model is trained offline with the Weka GUI. As a first go, I got 95% accuracy with a committee of random forests, each with 20 trees. (All other parameters were default.)

Note that in production, most of the images to be classified will be machine printed, although there may be some handwritten digits as well. 


## TODO
* Allow user to take a photo from which the puzzle is extracted
* Rather than showing entire solution, allow user to select which squares to reveal the correct answer.
* Improve UI & test on a few devices

