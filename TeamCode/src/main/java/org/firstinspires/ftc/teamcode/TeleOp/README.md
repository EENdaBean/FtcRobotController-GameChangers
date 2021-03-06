# TeleOp

## Overview

Our TeleOp files, located in this file, are used during the driver controlled portions of the robot game. We have three main files [TeleOp_Basic](), [TeleOp_Dev](), and [TeleOp_Pub](). 
[TeleOp_Basic]() is our most basic code, this contains just what we need to control the basic functions of the robot such as driving, intake, and launcher. This is mainly used to test our robot before matches without burning up the control hub.
[TeleOp_Dev]() is our [TeleOp_Basic]() but with some development code, we use this to test new functions we wish to implement into the robot. This is important so that we can keep the versions of the code the same and only push this once we have some main bugs worked out.
[TeleOp_Pub]() is our competition code, this is the version of TeleOp that we will use during competitions. This is more than [TeleOp_Basic]() and is proven code most likely developed from [TeleOp_Dev]().

##Components

In our TeleOp code we have code that we have been improving upon for 5+ years. Our old head programmer, Tanner, figured out all of the trig that we still use to this day.
```java
        if(gamepad1.b && !bWasPressed)
            inReverse=!inReverse;
        bWasPressed=gamepad1.b;
        //first we must translate the rectangular values of the joystick into polar coordinates;
        double y = -1 * gamepad1.left_stick_y;
        double x = gamepad1.left_stick_x;
        double angle = 0;

        if(y>0 && x>0)//quadrant 1
            angle=Math.atan(y/x);
        else if(y>0 && x<0)//quadrant 2
            angle= Math.toRadians(180)+Math.atan(y/x);
        else if(y<0 && x<0)//quadrant 3
            angle=Math.toRadians(180)+Math.atan(y/x);
        else if(y<0 && x>0)//quadrant 4
            angle=Math.toRadians(360)+Math.atan(y/x);

        if(y==0 && x>1)
            angle=0;
        if(y>0 && x==0)
            angle=Math.PI/2;
        if(y==0 && x<0)
            angle=Math.PI;
        if(y<0 && x==0)
            angle=3*Math.PI/2;

        double velocity=Math.sqrt(Math.pow(gamepad1.left_stick_y, 2)+Math.pow(gamepad1.left_stick_x, 2));
        double rotation=gamepad1.right_stick_x;

        if(inReverse)//reverse button
            angle+=Math.toRadians(180);

        angle+=Math.toRadians(270);

        //equations taking the polar coordinates and turing them into motor powers
        double power1=velocity*Math.cos(angle+(Math.PI/4))-rotation;
        double power2=velocity*Math.sin(angle+(Math.PI/4))+rotation;
        double power3=velocity*Math.sin(angle+(Math.PI/4))-rotation;
        double power4=velocity*Math.cos(angle+(Math.PI/4))+rotation;
        r.frontLeft.setPower(power1 * deflator);
        r.frontRight.setPower(power2 * deflator);
        r.backLeft.setPower(power3 * deflator);
        r.backRight.setPower(power4 * deflator);
```

This takes in the locations of where the sticks are, converts them to polar coordinates, then calculates the velocity for each wheel and moves us there. That is all this part of the code does.