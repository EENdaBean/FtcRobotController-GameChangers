#Autonomous

##Overview
Our autonomous files, located in this file, are used during the pre-programmed portions of the robot game. We have three main files [Blue_Basic_Auto]()/[Red_Basic_Auto](), [Blue_Advanced_Auto]()/[Red_Advanced_Auto](), and [Blue_Supreme_Auto]()/[Red_Supreme_Auto]().
[Blue_Basic_Auto]() and [Red_Basic_Auto]() are the most basic version of the autonomous classes. These just move to a the third box and move out of the way, that's it.
[Blue_Advanced_Auto]() and [Red_Advanced_Auto]() just detect the amount of rings and blindly moves to where the robot thinks the correct block is and gets out of the way.
[Blue_Supreme_Auto]() and [Red_Supreme_Auto]() is the god of the autonomous period. This class does it all, from detecting rings to accurate sub-inch accuracy in movement. This class will be the main  workforce of the autonomous period.

##[Blue_Supreme_Auto]() and [Red_Supreme_Auto]()
These classes have [threaded classes found here](). The main threaded class is our [Pos_Ring]() runnable (Take a look at [this README]() for more information about our threads). These classes have the ability to detect rings using the second camera then get positioning data using the main camera.
The main camera, placed facing straight out, is used to view the VuMarks that are placed around the outside of the field. By viewing these VuMarks we get sub-inch precision coordinates around the field, as long as a VuMark is in view. 
