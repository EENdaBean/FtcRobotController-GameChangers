# Position Data

  ## Overview
    
  Using the reference images placed around the field we are able to get XYZ data of where the camera is reletive to the field. By doing this we can see exactly where we are on the field and tell the robot to go wherever we want, as long as the camera has an image in sight.
  
  ### Coords
  
  ```
  _________________________________________________________________
  |       |                                               |       |
  |   3   |                                               |   3   |
  |_______|_______                                 _______|_______|
  |       |       |                               |       |       |
  |       |   2   |                               |   2   |       |
  |_______|_______|                               |_______|_______|
  |       |                                               |       |
  |   1   |                                               |   1   |
  |_______|                     x+                        |_______|
  |                           y+   y-                             |
  |                             x-                                |
  |                                                               |
  |                                                               |
  |                                                               |
  |                                                               |
  |                                                               |
  |             .                                   .             |
  |                                                               |
  |         |       |                           |       |         |
  |         |       |                           |       |         |
  __________|_______|___________________________|_______|__________
  
  
  ```
    
  ## The Code
    
  This was branched off of the [ConceptVuforiaUltimateGoalNavigationWebcam.](https://github.com/BenGhent/FtcRobotController-GameChangers/blob/Dev-Pos/FtcRobotController/src/main/java/org/firstinspires/ftc/robotcontroller/external/samples/ConceptVuforiaUltimateGoalNavigationWebcam.java) We use this as the baseline of the navigation. 

  ### Explanation
  
  During the init process the robot defines what the targets are and where they are located to the field as shown here:
  ```java
  VuforiaTrackables targetsUltimateGoal = this.vuforia.loadTrackablesFromAsset("UltimateGoal");
  VuforiaTrackable blueTowerGoalTarget = targetsUltimateGoal.get(0);
  blueTowerGoalTarget.setName("Blue Tower Goal Target");
  VuforiaTrackable redTowerGoalTarget = targetsUltimateGoal.get(1);
  redTowerGoalTarget.setName("Red Tower Goal Target");
  VuforiaTrackable redAllianceTarget = targetsUltimateGoal.get(2);
  redAllianceTarget.setName("Red Alliance Target");
  VuforiaTrackable blueAllianceTarget = targetsUltimateGoal.get(3);
  blueAllianceTarget.setName("Blue Alliance Target");
  VuforiaTrackable frontWallTarget = targetsUltimateGoal.get(4);
  frontWallTarget.setName("Front Wall Target");
  
  List<VuforiaTrackable> allTrackables = new ArrayList<VuforiaTrackable>();
  allTrackables.addAll(targetsUltimateGoal);
  
  redAllianceTarget.setLocation(OpenGLMatrix
          .translation(0, -halfField, mmTargetHeight)
          .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 180)));

  blueAllianceTarget.setLocation(OpenGLMatrix
          .translation(0, halfField, mmTargetHeight)
          .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 0)));
  frontWallTarget.setLocation(OpenGLMatrix
          .translation(-halfField, 0, mmTargetHeight)
          .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0 , 90)));

  // The tower goal targets are located a quarter field length from the ends of the back perimeter wall.
  blueTowerGoalTarget.setLocation(OpenGLMatrix
          .translation(halfField, quadField, mmTargetHeight)
          .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0 , -90)));
  redTowerGoalTarget.setLocation(OpenGLMatrix
          .translation(halfField, -quadField, mmTargetHeight)
          .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, -90)));
  ```
  
  Then it goes on to start the init process of the camera which we named Webcam 1, for debug purposes we are displaying the camera on the screen connected by an HDMI cable.
  
  Next our main loop begins with a while statement `while(!isStopRequested())`. This says, "while the user has not hit the stop button, run this loop." Inside this loop is where all the magic happenes!
  
  First we check if any reference markers are visable and if they are to take note and display the name through the telemetry.
  ```java
  targetVisible = false;
  for (VuforiaTrackable trackable : allTrackables) {
      if (((VuforiaTrackableDefaultListener)trackable.getListener()).isVisible()) {
          telemetry.addData("Visible Target", trackable.getName());
          targetVisible = true;

          // getUpdatedRobotLocation() will return null if no new information is available since
          // the last time that call was made, or if the trackable is not currently visible.
          OpenGLMatrix robotLocationTransform = ((VuforiaTrackableDefaultListener)trackable.getListener()).getUpdatedRobotLocation();
          if (robotLocationTransform != null) {
              lastLocation = robotLocationTransform;
          }
          break;
      }
  }
  ```
  
  Next we display it using AR while also getting XYZ and YPR.
  ```java
  if (targetVisible) {
      // express position (translation) of robot in inches.
      VectorF translation = lastLocation.getTranslation();
      telemetry.addData("Pos (in)", "{X, Y, Z} = %.1f, %.1f, %.1f",
              translation.get(0) / mmPerInch, translation.get(1) / mmPerInch, translation.get(2) / mmPerInch);
      xyz[0] = translation.get(0) / mmPerInch;
      xyz[1] = translation.get(1) / mmPerInch;
      xyz[2] = translation.get(2) / mmPerInch;

      // express the rotation of the robot in degrees.
      Orientation rotation = Orientation.getOrientation(lastLocation, EXTRINSIC, XYZ, DEGREES);
      telemetry.addData("Rot (deg)", "{Roll, Pitch, Heading} = %.0f, %.0f, %.0f", rotation.firstAngle, rotation.secondAngle, rotation.thirdAngle);
      xyz[3] = rotation.thirdAngle;
  }
  else {
      telemetry.addData("Visible Target", "none");
  }
  ```
  XYZ[] is a variable to transfer the coords to the next method that moves the robot to the target position.
  
  In this bunch of code we are only moving the robot if and only if opModeIsActive, otherwise just run vuforia.
	Next we are determaning if we are where we want to be, if we are then we aren't trying to get into position, but if we aren't at our target position then we try to move to the position.
	Next, if we loose the reference points then we rotate the robot to try to re-aquire the target. We are doing this every five times we running through the loop.
  
  ```java
  if(opModeIsActive() && targetVisible){
      telemetry.addData("OpMode", "True all the way");
      angle = Math.atan2((target[1]-xyz[1]),(target[0]-xyz[0]));
      telemetry.addData("Angle",angle);
      if(target[0]-xyz[0] >= -5 && target[0]-xyz[0] <= 5 && target[1]-xyz[1] >= -5 && target[1]-xyz[1] <= 5 ){
          telemetry.addData("Debug", "Getting in false");
          move(angle, xyz[3],false);
      }else{
          telemetry.addData("Debug", "Getting in true");
          move(angle, xyz[3],true);
      }
  }else{
      telemetry.addData("OpMode", "False");
      move(angle + (Math.PI)/2, xyz[3],false);
      if(a % 5 ==0){
          rotate(0.3);
      }
  }
  ```
  
  
