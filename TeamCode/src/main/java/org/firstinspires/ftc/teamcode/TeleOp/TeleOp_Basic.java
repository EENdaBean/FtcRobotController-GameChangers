package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Hardware;

@TeleOp(name="TeleOp_Basic", group="Comp")
//@Disabled
public class TeleOp_Basic extends OpMode {

    boolean inReverse=false;//reverse button is b
    boolean bWasPressed=false;
    
    boolean intake = false;
    boolean intake_running = false;

    Hardware r = new Hardware();

    @Override
    public void init() {

        r.initRobot(hardwareMap, telemetry);
    }

    @Override
    public void loop() {
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
    
        if(gamepad1.x && !intake)
            intake_running=!intake_running;
        intake=gamepad1.x;
        
        if(gamepad1.y){
            r.Intake.setPower(-1);
            intake_running = false;
        }else{
            r.Intake.setPower(0);
        }
        
        if(intake_running){
            r.Intake.setPower(1);
        }

        if(gamepad1.dpad_down){
            r.Launcher.setPower(1);
        }else{
            r.Launcher.setPower(0);
        }
        
        if(gamepad1.left_trigger !=0){
            r.Flywheel.setPower(0.95);
        }else{
            r.Flywheel.setPower(0);
        }

    }
}
