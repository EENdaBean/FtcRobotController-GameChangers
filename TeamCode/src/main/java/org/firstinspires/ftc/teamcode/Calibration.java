package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Threads.Position_File.PosThread_Callback;
import org.firstinspires.ftc.teamcode.Threads.Position_File.Position;

@Autonomous(name="Calibration", group="Linear Opmode")
@Disabled
public class Calibration extends LinearOpMode {
	
	Thread th;
	Runnable rn;
	
	Hardware r = new Hardware();
	
	double i[] = {0,0,0,0};
	int target[] = {0,0};
	
	double angle;
	
	@Override
	public void runOpMode() throws InterruptedException {
		PosThread_Callback cb = new PosThread_Callback() {
			@Override
			public void post(double[] a, boolean targeted) {
			
			}
		};
		
		rn = new Position(hardwareMap,telemetry, cb);
		th = new Thread(rn);
		th.start();
		
		r.initRobot(hardwareMap, telemetry);
		
		r.setDriveMotorMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
		
		telemetry.addData("Test", "Wait for start");
		telemetry.update();
		waitForStart();
		telemetry.addData("Test", "Running");
		telemetry.update();
		r.setDriveMotorMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//		while( (int) i[3] != 90){
//			if((int) i[3] < 90){
////				r.setToCounterwise(0.6);
//				r.frontLeft.setPower(0.2);
//				r.frontRight.setPower(-0.2);
//				r.backLeft.setPower(0.2);
//				r.backRight.setPower(-0.2);
//				telemetry.addData("Direction", "CCW");
//			}else{
////				r.setToClockwise(0.6);
//				r.frontLeft.setPower(-0.2);
//				r.frontRight.setPower(0.2);
//				r.backLeft.setPower(-0.2);
//				r.backRight.setPower(0.2);
//				telemetry.addData("Direction", "CW");
//			}
//			telemetry.addData("Test", i[3]);
//			telemetry.update();
//		}
		
		r.setDriveMotorPower(0);
		
		r.setDriveMotorMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
		
		r.setDriveMotorMode(DcMotor.RunMode.RUN_USING_ENCODER);
		
		target = new int[]{(int) (i[0] + 24), (int) i[1]};
		
		double deflate;
		
		while((int) i[0] != target[0] && (int) i[1] != target[1]){
			
			telemetry.addData("Test", "In move loop");
			telemetry.update();
			
			angle = Math.atan2((target[1]-i[1]),(target[0]-i[0])); // atan2(Y-axis, X-axis)
			
			double power1;
			double power2;
			double power3;
			double power4;
			angle = ((Math.PI)/2) - (Math.toRadians(i[3]) - angle); // Calculate the angle relative to the robot
			telemetry.addData("Angle Send", angle);
			//r.setDriveMotorMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER); // Run the motors without their encoders
			
//			deflate = (-0.0000103484 * Math.pow(Math.sqrt(Math.pow(i[0] - target[0],2) + Math.pow(i[1]-target[1], 2)), 4))
//							  + (0.000788031*(Math.pow(Math.sqrt(Math.pow(i[0] - target[0],2) + Math.pow(i[1]-target[1], 2)), 3)))
//							  + (-0.021532647*(Math.pow(Math.sqrt(Math.pow(i[0] - target[0],2) + Math.pow(i[1]-target[1], 2)), 2)))
//							  + (0.248123034*Math.sqrt(Math.pow(i[0] - target[0],2) + Math.pow(i[1]-target[1], 2)))
//							  - (0.005585635);
			
			double velocity = 0.6;// * deflate; // speed the bot will move
			double rotation = 0;   // set the rotation the robot needs to move
			
			angle+=Math.toRadians(180);
			
			//equations taking the polar coordinates and turning them into motor powers
			double vx = velocity * Math.cos(angle + (Math.PI / 4)); // determine the velocity in the Y-axis
			double vy = velocity * Math.sin(angle + (Math.PI / 4)); // determine the velocity in the X-axis
			
			power1 = vx - rotation; // Calculate the power of motor 1
			power2 = vy + rotation; // Calculate the power of motor 2
			power3 = vy - rotation; // Calculate the power of motor 3
			power4 = vx + rotation; // Calculate the power of motor 4
			//telemetry.addData("Angle", angle);
			
			r.frontLeft.setPower(power1);  // set the power of the front left motor
			r.frontRight.setPower(power2); // set the power of the front right motor
			r.backLeft.setPower(power3);   // set the power of the back left motor
			r.backRight.setPower(power4);  // set the power of the back right motor
			
			telemetry.addLine("Encoders - ")
					.addData("FrontLeft", r.frontLeft.getCurrentPosition())
					.addData("FrontRight", r.frontRight.getCurrentPosition())
					.addData("BackLeft", r.backLeft.getCurrentPosition())
					.addData("BackRight", r.backRight.getCurrentPosition());
			telemetry.update();
			
		}
	}
}
