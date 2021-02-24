package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Hardware;
import org.firstinspires.ftc.teamcode.Threads.Position_File.PosThread_Callback;
import org.firstinspires.ftc.teamcode.Threads.Position_File.Position;

@TeleOp(name="TeleOp_Dev - Position", group="Development")
//@Disabled
public class TeleOp_Dev extends OpMode {
    
    Thread th;
    Runnable rn;
    
    Hardware r = new Hardware();
    
    double i[] = {0,0,0,0};
    int target[] = {0,0};
    double Tangle;
    boolean Targeted;
    
    @Override
    public void init() {
        PosThread_Callback cb = new PosThread_Callback() {
            @Override
            public void post(double[] a, boolean targeted) {
//                i[0] = a[0];
//                i[1] = a[1];
//                i[2] = a[2];
                i = a;
                Targeted = targeted;
            }
        };
        r.initRobot(hardwareMap, telemetry);
        rn = new Position(hardwareMap,telemetry, cb);
        th = new Thread(rn);
        th.start();
    }
    
    boolean inReverse=false;//reverse button is b
    boolean bWasPressed=false;

    @Override
    public void loop() {
    
        //int speed = 0;
        double deflator = .9;
    
        //this code determines what percentage of the motor power that will be used.
        if(gamepad1.right_bumper){
            deflator = .4;
        }else {
            deflator = .9;
        }
    
        if(gamepad1.left_bumper)
            deflator = 1;
    
        //legacy code that runs our mecanum drive wheels in any direction we want
    
        //this first section creates the variables that will be used later
    
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
            angle=Math.toRadians(180)+Math.atan(y/x);
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
            angle+=Math.toRadians(270);
    
        angle+=Math.toRadians(180);
    
        //equations taking the polar coordinates and turing them into motor powers
        double power1=velocity*Math.cos(angle+(Math.PI/4))-rotation;
        double power2=velocity*Math.sin(angle+(Math.PI/4))+rotation;
        double power3=velocity*Math.sin(angle+(Math.PI/4))-rotation;
        double power4=velocity*Math.cos(angle+(Math.PI/4))+rotation;
        
        if(gamepad1.a && Targeted){
            target = new int[]{30, 30};
            
            if(target[0] - i[0] >=5 && target[0] -i[0] <=-5 && target[1] - i[1] >=5 && target[1] - i[1] <=-5) {
    
                Tangle = Math.atan2((target[1] - i[1]), (target[0] - i[0])); // atan2(Y-axis, X-axis)
                Tangle = ((Math.PI) / 2) - (Math.toRadians(i[3]) - Tangle); // Calculate the angle relative to the
    
                velocity = 0.6;
    
                double vx = velocity * Math.cos(Tangle + (Math.PI / 4)); // determine the velocity in the Y-axis
                double vy = velocity * Math.sin(Tangle + (Math.PI / 4)); // determine the velocity in the X-axis
    
                power1 = vx; // Calculate the power of motor 1
                power2 = vy; // Calculate the power of motor 2
                power3 = vy; // Calculate the power of motor 3
                power4 = vx; // Calculate the power of motor 4
            
            }
            
        }
    
        //Run the motors after all calculations
        r.frontLeft.setPower(power1 * deflator);
        r.frontRight.setPower(power2 * deflator);
        r.backLeft.setPower(power3 * deflator);
        r.backRight.setPower(power4 * deflator);
        
        //Logging data
        telemetry.addLine("Pos - ")
                .addData("X",i[0])
                .addData("Y",i[1])
                .addData("Z",i[2])
                .addData("Heading",i[3]);
        telemetry.update();
    }
    
    @Override
    public void stop() {
        th.interrupt();
        super.stop();
    }
    
}
