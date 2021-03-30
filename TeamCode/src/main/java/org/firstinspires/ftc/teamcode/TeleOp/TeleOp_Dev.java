package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Hardware;
import org.firstinspires.ftc.teamcode.Threads.Pos_Ring.Pos_Ring;
import org.firstinspires.ftc.teamcode.Threads.Pos_Ring.Pos_RingCallback;

@TeleOp(name="TeleOp_Dev - Position", group="Development")
//@Disabled
public class TeleOp_Dev extends OpMode {
    
    Pos_Ring Pos_Ring;
    Thread Pos_Ring_TH;
    
    Hardware r = new Hardware();
    
    String amount = "None";
    double[] location = {0,0,0,0};
    boolean is_Targeted = false;

    @Override
    public void init() {
        r.initRobot(hardwareMap,telemetry);
    
        Pos_RingCallback prcb = new Pos_RingCallback() {
            @Override
            public void ring(String Amount) {
                amount = Amount;
            }
        
            @Override
            public void pos(double[] a, boolean targeted) {
                location = a;
                is_Targeted = targeted;
            }
        };
    
        Pos_Ring = new Pos_Ring(hardwareMap, telemetry, prcb);
        Pos_Ring_TH = new Thread(Pos_Ring);
        
        Pos_Ring_TH.start();
    
        Pos_Ring.switch_cam("Pos");
        Pos_Ring.switchDetection("Pos");
        
    }
    
    boolean inReverse=false;//reverse button is b
    boolean bWasPressed=false;

    @Override
    public void loop() {
    
        //int speed = 0;
        double deflator;
    
        //this code determines what percentage of the motor power that will be used.
        if(gamepad1.right_bumper){
            deflator = .4;
        }else {
            deflator = .7;
        }
    
        if(gamepad1.left_bumper)
            deflator = 1;
    
        //legacy code that runs our mecanum drive wheels in any direction we want
        /*
         *
         * The mecanum wheels should be setup like this:
         *
         * \     /
         *
         * /     \
         *
         * With the black part of the wheel facing towards the center of the bot
         * */
        //we need to determine which direction we want to front of the robot to be
        //that is done here
        if(gamepad1.back && !bWasPressed)
            inReverse=!inReverse;
        bWasPressed=gamepad1.b;
        //first we must translate the rectangular values of the joystick into polar coordinates;
        double y = -1 * gamepad1.left_stick_y;
        double x = gamepad1.left_stick_x;
        double angle = 0;
    
        if(y>0 && x>0)//quadrant 1
            angle=Math.atan(y/x);
        else {
            double angle1 = Math.toRadians(180) + Math.atan(y / x);
            if(y>0 && x<0)//quadrant 2
                angle= angle1;
            else if(y<0 && x<0)//quadrant 3
                angle= angle1;
            else if(y<0 && x>0)//quadrant 4
                angle=Math.toRadians(360)+Math.atan(y/x);
        }
    
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
        double v1 = velocity * Math.cos(angle + (Math.PI / 4));
        double power1= v1 +rotation;
        double v2 = velocity * Math.sin(angle + (Math.PI / 4));
        double power2= v2 -rotation;
        double power3= v2 +rotation;
        double power4= v1 -rotation;
        r.frontLeft.setPower(power1 * deflator);
        r.frontRight.setPower(power2 * deflator);
        r.backLeft.setPower(power3 * deflator);
        r.backRight.setPower(power4 * deflator);
        
        //Logging data
        telemetry.addLine("Pos - ")
                .addData("X", location[0])
                .addData("Y", location[1])
                .addData("Z", location[2])
                .addData("Heading",location[3]);
        telemetry.update();
    }
    
    @Override
    public void stop() {
        Pos_Ring.stop();
        Pos_Ring_TH.interrupt();
        super.stop();
    }
    
}
