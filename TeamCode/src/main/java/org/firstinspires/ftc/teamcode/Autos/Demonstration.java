package org.firstinspires.ftc.teamcode.Autos;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Hardware;
import org.firstinspires.ftc.teamcode.Threads.Pos_Ring.Pos_Ring;
import org.firstinspires.ftc.teamcode.Threads.Pos_Ring.Pos_RingCallback;

@Autonomous(name = "Demonstration", group = "Test")
//@Disabled
public class Demonstration extends LinearOpMode {
	
	Hardware r = new Hardware();
	
	Pos_Ring Pos_Ring;
	Thread Pos_Ring_TH;
	
	double[] location	 			 = {0,0,0,0}; //x, y, z, heading
	boolean is_Targeted 			 = false;
	
	String amount = "none";
	
	String Cam = "Ring";
	
	boolean running = true;
	
	@Override
	public void runOpMode() {
		r.initRobot(hardwareMap, telemetry);
		r.initAutonomous();
		
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
		
		Pos_Ring = new Pos_Ring(hardwareMap, telemetry, prcb);
		Pos_Ring_TH = new Thread(Pos_Ring);
		
		Pos_Ring_TH.start();
		
		waitForStart();
		
		int[] target = {30,30};
		
		while(!is_Targeted){
			r.setDriveMotorPower(0.6);
		}
		
		double angle;
		while(opModeIsActive() && !isStopRequested()){
			angle = Math.atan2((target[1] - location[1]), (target[0] - location[0])); // atan2(Y-axis, X-axis)
			
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
			double velocity = 0.4;// * deflate; // speed the bot will move
			double rotation = (90 - location[3]) * 0.01;   // set the rotation the robot needs to move
			
			angle += Math.toRadians(0);
			
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
			telemetry.addLine("Position - ")
					.addData("X", "%.0f", location[0])
					.addData("Y", "%.0f", location[1])
					.addData("Z", "%.0f", location[2])
					.addData("H", "%.0f", location[3])
					.addData("Visable", is_Targeted)
					.addData("Rotation", rotation);
			telemetry.addLine("Rings - ")
					.addData("Amount", amount);
			telemetry.update();
			if(target[0]-location[0] >= -2
					   && target[0]-location[0] <= 2
					   && target[1]-location[1] >= -2
					   && target[1]-location[1] <= 2){
				target = new int[]{(int) (Math.random()*(30-(-30))+(-30)), (int) (Math.random()*(30-(-30))+(-30))};
			}
		}
		
	}
}