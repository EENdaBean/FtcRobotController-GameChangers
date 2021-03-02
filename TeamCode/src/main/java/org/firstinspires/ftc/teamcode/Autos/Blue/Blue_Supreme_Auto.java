/**
 *
 * This is the advanced version of our autonomous period for the Ultimate Goal season
 * This is for the Blue side of the field for either line
 *
 * This code will view the rings on the field and move to a set position based on
 * position given by the camera and reference points
 *
 * Then the robot will move to a know position and fire the rings into the top of
 * the goal which is 12 points each during autonomous
 *
 * @see org.firstinspires.ftc.teamcode.Autos.Blue.Blue_Advanced_Auto for information
 * for a more basic version of the code that does not use vision
 *
 * @see org.firstinspires.ftc.teamcode.Autos.Blue.Blue_Basic_Auto for information about
 * the most basic version of the blue side auto
 *
 * */

package org.firstinspires.ftc.teamcode.Autos.Blue;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.AutoTransitioner;
import org.firstinspires.ftc.teamcode.Hardware;
import org.firstinspires.ftc.teamcode.Threads.Pos_Ring.Pos_Ring;
import org.firstinspires.ftc.teamcode.Threads.Pos_Ring.Pos_RingCallback;
import org.firstinspires.ftc.teamcode.Threads.Position_File.Position;

@Autonomous(name="Blue_Supreme_Auto", group="Comp")
//@Disabled
public class Blue_Supreme_Auto extends LinearOpMode {
	
	Hardware r = new Hardware();
	
	Runnable Pos_Ring;
	Thread Pos_Ring_TH;
	
	double location[] 				 = {0,0,0,0}; //x, y, z, heading
	boolean is_Targeted 			 = false;
	static final int target1[] 		 = {0,0};  //target of block 1
	static final int target2[] 		 = {30,30};//target of block 2
	static final int target3[] 		 = {0,0};  //target of block 3
	static final int target_launch[] = {3,33}; //target of the location to launch rings
	static final int target_Line[]   = {7,33}; //target of the ending line (white line)
	int target[] 					 = {0,0};  //to change where we need to move
	
	String amount = "none";
	
	String Cam = "Ring";
	
	boolean running = true;  //if the thread is to be running
	boolean placed  = false; //if we placed the wobble goal
	double angle;            //The angle the robot must move to reach its target
	
	@Override
	public void runOpMode() throws InterruptedException {
		AutoTransitioner.transitionOnStop(this, "TeleOp_Basic");
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
			public String camera() {
				return Cam;
			}
		};
		
		Pos_Ring = new Pos_Ring(hardwareMap, telemetry, prcb);
		Pos_Ring_TH = new Thread(Pos_Ring);
		
		Pos_Ring_TH.start();
		
		while(!opModeIsActive()){
			telemetry.addLine("Position - ")
					.addData("X", "%.0f" ,location[0])
					.addData("Y", "%.0f", location[1])
					.addData("Z", "%.0f", location[2])
					.addData("H", "%.0f", location[3]);
			telemetry.addLine("Rings - ")
					.addData("Amount",amount);
			telemetry.update();
			r.waiter(500);
		}
		
		switch (amount){
			case "Single" :
				//move to a position
				target = target2;
				break;
			case "Quad" :
				//move to a position
				target = target3;
				break;
			default:
				//move to a position
				target = target1;
				break;
		}
		
		Cam = "Pos";
		
		while(opModeIsActive()){
			telemetry.addLine("Position - ")
					.addData("X", "%.0f" ,location[0])
					.addData("Y", "%.0f", location[1])
					.addData("Z", "%.0f", location[2])
					.addData("H", "%.0f", location[3]);
			telemetry.addLine("Rings - ")
					.addData("Amount",amount);
			telemetry.update();
			r.waiter(500);
		};
		
		//end of the code, end all of the threads and just stop
		running = false;
		Pos_Ring_TH.interrupt();
	}
	
	private void moveTo(int[] target){
		placed = false;
		while(!placed) {
			if (target[0] - location[0] >= 5 && target[0] - location[0] <= -5 &&
						target[1] - location[1] >= 5 && target[1] - location[1] <= -5) {
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
				double velocity = 0.6;// * deflate; // speed the bot will move
				double rotation = 0;   // set the rotation the robot needs to move
				
				angle += Math.toRadians(180);
				
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
				telemetry.update();
			} else {
				placed = true;
			}
		}
	}
	
}