#Threading

##Overview
These files are made to run in the background of the main thread, either [autonomous]() or [TeleOp](). There are three threads that work: [Pos_Ring](), [Position](), and [Rings](). Each file has a callback interface which allows the threads to talk to the thread which ran it.
[Pos_Ring]() is the god class that is meant to be used with two cameras, one for position and one for detecting rings. 
[Position]() is designed to run one camera and only determines position using the VuMarks.
[Rings]() is designed to run on one camera and returns haw many rings there are in a stack.

##[Pos_Ring]()
This is the main class that runs behind [Blue_Supreme_Auto]() to get rings at the beginning of the match and returns position of where the robot is located on the field. 
[Pos_Ring]() uses the initiation found in both [Position]() and [Rings]() with some minor adjustment to make them work together