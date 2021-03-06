/*
 
  This is the advanced version of our autonomous period for the Ultimate Goal season
  This is for the Blue side of the field for either line
 
  This code will view the rings on the field and move to a set position based on
  position given by the camera and reference points
 
  Then the robot will move to a know position and fire the rings into the top of
  the goal which is 12 points each during autonomous
 
  @see org.firstinspires.ftc.teamcode.Autos.Blue.Blue_Advanced_Auto for information
 * for a more basic version of the code that does not use vision
 *
 * @see org.firstinspires.ftc.teamcode.Autos.Blue.Blue_Basic_Auto for information about
 * the most basic version of the blue side auto
 *
 * The field is layed out as such:
 * ______
 * | 3  |
 * |____|______
 *       | 2  |
 * ______|____|
 * | 1  |
 * |____|
 *
 * */

package org.firstinspires.ftc.teamcode.Autos.Blue;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.AutoTransitioner;
import org.firstinspires.ftc.teamcode.Hardware;
import org.firstinspires.ftc.teamcode.Threads.Pos_Ring.Pos_Ring;
import org.firstinspires.ftc.teamcode.Threads.Pos_Ring.Pos_RingCallback;

@Autonomous(name="Blue_Supreme_Auto", group="Comp")
//@Disabled
public class Blue_Supreme_Auto extends LinearOpMode {
	
	Hardware r = new Hardware();
	
	Runnable Pos_Ring;
	Thread Pos_Ring_TH;
	
	int targetSpeed = 700; //Ticks per second
	
	double[] location	 			 = {0,0,0,0}; //x, y, z, heading
	boolean is_Targeted 			 = false;
	static final int[] target1 		 = {27,56};  //target of block 1
	static final int[] target2 		 = {47,25};  //target of block 2
	static final int[] target3       = {47,26};  //target of block 3
	static final int[] target_launch = {21,35};  //target of the location to launch rings
	static final int[] target_Line 	 = {30,35};  //target of the ending line (white line)
//	static final int[] target_ring   = {0,0};    //where the reference rings are
	int[] target 					 = {50,60};  //to change where we need to move
	
	String amount = "none";
	
	String Cam = "Ring";
	
	boolean running = true;  //if the thread is to be running
	boolean placed  = false; //if we placed the wobble goal
	double angle;            //The angle the robot must move to reach its target
	
	@Override
	public void runOpMode() {
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
					.addData("H", "%.0f", location[3])
					.addData("Visable", is_Targeted);
			telemetry.addLine("Rings - ")
					.addData("Amount",amount);
			telemetry.update();
			if(isStopRequested()){
				running = false;
				break;
			}
			r.waiter(500);
		}
		
		switch (amount){
			case "Single" :
				// Move to the third box
				target = target2;
				break;
			case "Quad" :
				// Move to the second box
				target = target3;
				break;
			default:
				// Move to the first box
				target = target1;
				break;
		}
		
		// Set the camera to the 1080p positioning camera
		Cam = "Pos";
		
		// To begin we must move to a relative position so that we can see the VuMarks
		// In this case we will just move forward until we see a VuMark
		r.setDriveMotorMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
		
		do{
			telemetry.addData("Targeted?", is_Targeted);
			telemetry.update();
			r.setDriveMotorPower(0.4);
			//TODO: If this doesn't get us in view we need to move at a different angle
		} while(!is_Targeted && opModeIsActive());
		
		r.setToStill();
		
		// Once we see a VuMark we will move to the position designated by the amount
		// of rings we saw at the beginning
		moveTo(target);
		
		// If we have to move to the Quad position  then we need to move to a location
		// then hit the wall then move into the zone hitting the other wall, place the
		// wobble, then move out of it back into line of sight of the VuMark
		if(target == target3){
			r.setDriveMotorPower(0.8);
			r.waiter(750);
			r.setToLeft(0.6);
			r.waiter(750);
		}
		
		// Place the wobble goal
		r.Wobble.setPosition(1);
		r.waiter(500);
		
		if(target == target3){
			r.setDriveMotorPower(-0.6);
			r.waiter(500);
			r.setToRight(0.6);
			r.waiter(500);
		}
		
		// next we want to move out of the goal area, for this we will just back up
		r.setToForward(-0.6);
		r.waiter(100);
		r.setToStill();
		
		// Next we want to move to the launch position, this is just behind the white line
		// and in front of the goal area
		moveTo(target_launch);
		
		// Now that we are at the launching position we want to actually launch the rings
		// we will accomplish this by spinning the Flywheel at a specific speed and once
		// it reaches this speed, launch the ring into the goal... Hopefully..
		// TODO: make sure that the motor is not burnt out
		r.Flywheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
		r.Flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
		int launch = 0;
		int a1, a2;
		double speed = 0.5;
		do{
			//change speed based on encoder directions
			a1 = r.Flywheel.getCurrentPosition();
			telemetry.addData("before", a1);
			r.waiter(500);
			a2 = r.Flywheel.getCurrentPosition();
			telemetry.addData("after", a2);
			
			if(a2-a1 < targetSpeed-100){
				speed = speed + 0.04;
			}
			if(a2-a1 < targetSpeed){
				speed = speed + 0.01;
			}else if(a2-a1 > targetSpeed){
				speed = speed - 0.01;
			}
			if(((a2-a1) <= (targetSpeed+10)) && ((a2-a1) >= (targetSpeed-10))){
				// launch a ring
				r.Launcher.setPower(1);
				r.waiter(750);
				r.Launcher.setPower(0);
				launch++;
			}
			r.Flywheel.setPower(speed);
			telemetry.addData("Speed", a2-a1);
			telemetry.addData("Target Speed", targetSpeed);
			telemetry.update();
		}while(launch != 3 && opModeIsActive());
		r.Flywheel.setPower(0);
		
		// Now we move to the line for those points
		moveTo(target_Line);
		
		// This should be the end of the Blue auto, nothing else should need to be done
		
		// end of the code, end all of the threads and just stop
		running = false;
		Pos_Ring_TH.interrupt();
	}
	
	private void moveTo(int[] target){
		placed = false;
		int i = 0;
		while(!placed && opModeIsActive()){
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
			telemetry.addData("Working?", i);
			telemetry.addData("Working?", i);
			telemetry.update();
			i++;
			if(target[0]-location[0] >= -2
					   && target[0]-location[0] <= 2
					   && target[1]-location[1] >= -2
					   && target[1]-location[1] <= 2){
				placed = true;
			}
		}
		r.setToStill(); // Stop moving, in the case we don't have any more movement commands after
	}
	
}