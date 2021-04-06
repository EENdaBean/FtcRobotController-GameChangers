package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Hardware;
import org.firstinspires.ftc.teamcode.Threads.Pos_Ring.Pos_Ring;
import org.firstinspires.ftc.teamcode.Threads.Pos_Ring.Pos_RingCallback;
import org.firstinspires.ftc.teamcode.Threads.Speed.speed;

@TeleOp(name="TeleOp_Dev - Position", group="Development")
//@Disabled
public class TeleOp_Dev extends OpMode {
    
    Pos_Ring Pos_Ring;
    Thread Pos_Ring_TH;
    
    speed sp;
    Thread spTh;
    
    Hardware r = new Hardware();
    
    String amount = "None";
    static final int[] target = {13,37};  //target of the location to launch rings
    double[] location = {0,0,0,0};
    boolean is_Targeted = false;
    boolean running = true;
    String Cam = "Pos";
    
    int targetSpeed = 725; //Ticks per second/2

    @Override
    public void init() {
        r.initRobot(hardwareMap, telemetry);
        
        r.Arm.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    
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
        
            @Override
            public boolean is_running() {
                return running;
            }
            
            @Override
            public String camera(){
                return Cam;
            }
        };
        
        sp = new speed(telemetry, r.Flywheel);
        spTh = new Thread(sp);
        sp.set_Speed(targetSpeed);
    
        Pos_Ring = new Pos_Ring(hardwareMap, telemetry, prcb);
        Pos_Ring_TH = new Thread(Pos_Ring);
    
        spTh.start();
        Pos_Ring_TH.start();
    }
    
    boolean inReverse      = false;//reverse button is b
    boolean bWasPressed    = false;
    boolean intake         = false;
    boolean intake_running = false;

    @Override
    public void loop() {
        
        if(gamepad1.a)
            moveTo(target);;

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
    
        // Turn on or off the intake, toggle
        if(gamepad1.x && !intake)
            intake_running=!intake_running;
        intake=gamepad1.x;
    
        // Outtake th ring if necessary
        if(gamepad1.y){
            r.Intake.setPower(-1);
            intake_running = false;
        }else{
            r.Intake.setPower(0);
        }
    
        // actually turn the intake
        if(intake_running){
            r.Intake.setPower(1);
        }
    
        // Launch rings only if we can and want to
        if(gamepad1.dpad_down && sp.can_Fire()){
            r.Launcher.setPower(0.6);
        }else{
            r.Launcher.setPower(0);
        }
        
        if(gamepad1.dpad_up){
            r.Wobble.setPosition(1);
        } else {
            r.Wobble.setPosition(0);
        }
        
        r.Arm.setPower(gamepad2.left_stick_x);
        
        if(gamepad1.start){
            Cam = "Ring";
        }else{
            Cam = "Pos";
        }
    
        // Tell the flywheel to spin if we hold down the trigger
        sp.spin(gamepad1.left_trigger != 0);
        
        //Logging data
        telemetry.addLine("Pos - ")
                .addData("X", "%.0f", location[0])
                .addData("Y", "%.0f", location[1])
                .addData("Z", "%.0f", location[2])
                .addData("Heading", "%.0f",location[3]);
        telemetry.addData("Arm", r.Arm.getCurrentPosition());
        telemetry.update();
    }
    
    double angle = 0;
    private void moveTo(int[] targetA) {
        angle = Math.atan2((targetA[1] - location[1]), (targetA[0] - location[0])); // atan2(Y-axis, X-axis)
    
        double power1;
        double power2;
        double power3;
        double power4;
        angle = ((Math.PI) / 2) - (Math.toRadians(location[3]) - angle); // Calculate the angle relative to the robot
        telemetry.addData("Angle Send", angle);
//				r.setDriveMotorMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER); // Run the motors without their encoders
//
//			deflate = (-0.0000103484 * Math.pow(Math.sqrt(Math.pow(i[0] - target[0],2) + Math.pow(i[1]-target[1], 2)), 4))
//							  + (0.000788031*(Math.pow(Math.sqrt(Math.pow(i[0] - target[0],2) + Math.pow(i[1]-target[1], 2)), 3)))
//							  + (-0.021532647*(Math.pow(Math.sqrt(Math.pow(i[0] - target[0],2) + Math.pow(i[1]-target[1], 2)), 2)))
//							  + (0.248123034*Math.sqrt(Math.pow(i[0] - target[0],2) + Math.pow(i[1]-target[1], 2)))
//							  - (0.005585635);
        double velocity = 0.6;// * deflate; // speed the bot will move
        double rotation = (90 - location[3]) * 0.01;   // set the rotation the robot needs to move
    
        angle += Math.toRadians(0);
    
        //equations taking the polar coordinates and turning them into motor powers
        double vx = velocity * Math.cos(angle + (Math.PI / 4)); // determine the velocity in the Y-axis
        double vy = velocity * Math.sin(angle + (Math.PI / 4)); // determine the velocity in the X-axis
    
        power1 = vx - rotation; // Calculate the power of motor 1
        power2 = vy + rotation; // Calculate the power of motor 2
        power3 = vy - rotation; // Calculate the power of motor 3
        power4 = vx + rotation; // Calculate the power of motor 4
    
        if(targetA[0]-location[0] >= -1 && targetA[0]-location[0] <= 1 && targetA[1]-location[1] >= -1 && targetA[1]-location[1] <= 1)
            r.setToStill();
        
        else {
            r.frontLeft.setPower(power1);
            r.frontRight.setPower(power2);
            r.backLeft.setPower(power3);
            r.backRight.setPower(power4);
        }
    }
    
    @Override
    public void stop() {
        running = false;
        sp.stop();
        Pos_Ring_TH.interrupt();
        spTh.interrupt();
        super.stop();
    }
    
}
