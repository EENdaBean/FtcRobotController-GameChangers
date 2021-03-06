package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Hardware;

@TeleOp(name="TeleOp_Basic", group="Comp")
//@Disabled
public class TeleOp_Basic extends OpMode {

    boolean inReverse=false;//reverse button is back button
    boolean bWasPressed=false;
    
    boolean intake = false;
    boolean intake_running = false;

    Hardware r = new Hardware();
    
    double speed = 0.5;
    
    int a1;
    int a2;
    int runs = 0;
    
    int target_speed = 675;

    @Override
    public void init() {

        r.initRobot(hardwareMap, telemetry);
        r.Flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    @Override
    public void loop() {
        
        if(r.timer.time() != 0){
            r.timer.startTime();
        }
        
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
            a1 = r.Flywheel.getCurrentPosition();
            r.waiter(500);
            a2 = r.Flywheel.getCurrentPosition();
            
            if(a2-a1 < target_speed - 100){
                speed = speed + 0.05;
            }
            
            if(a2-a1 < target_speed){
                speed = speed + 0.01;
            }
            
            if(a2-a1 > target_speed){
                speed = speed - 0.01;
            }
            
            if(a2-a1 > target_speed+ 100){
                speed = speed - 0.05;
            }
            
            r.Flywheel.setPower(speed);
            
            telemetry.addData("Running?", a2-a1);
            telemetry.addData("Speed?", speed);
        }else{
            r.Flywheel.setPower(0);
        }
        
        telemetry.update();
        
        runs++;

    }
}
