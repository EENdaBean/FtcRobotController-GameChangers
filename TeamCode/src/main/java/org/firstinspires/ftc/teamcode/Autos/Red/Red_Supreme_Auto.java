package org.firstinspires.ftc.teamcode.Autos.Red;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.AutoTransitioner;
import org.firstinspires.ftc.teamcode.Hardware;
import org.firstinspires.ftc.teamcode.Threads.Position_File.PosThread_Callback;
import org.firstinspires.ftc.teamcode.Threads.Position_File.Position;
import org.firstinspires.ftc.teamcode.Threads.Rings.Rings;
import org.firstinspires.ftc.teamcode.Threads.Rings.RingsThread_Callback;

@Autonomous(name = "Red_Supreme_Auto", group = "Comp")
//@Disabled
public class Red_Supreme_Auto extends LinearOpMode {
	
	Hardware r = new Hardware();
	
	Runnable ring;
	Thread ringth;
	Runnable pos;
	Thread posth;
	
	double location[] 				 = {0,0,0};
	boolean is_Targeted 			 = false;
	static final int target1[] 		 = {0,0};  //target of block 1
	static final int target2[] 		 = {0,0};  //target of block 2
	static final int target3[] 		 = {0,0};  //target of block 3
	static final int target_launch[] = {0,0};  //target of the location to launch rings
	static final int target_Line[]   = {0,0};  //target of the ending line (white line)
	int target[] 					 = {0,0};  //to change where we need to move
	
	String amount;
	
	boolean running = true;  //if the thread is to be running
	boolean placed  = false; //if we placed the wobble goal
	double angle;            //The angle the robot must move to reach its target
	
	@Override
	public void runOpMode() throws InterruptedException {
		AutoTransitioner.transitionOnStop(this, "TeleOp_Basic");
		r.initRobot(hardwareMap, telemetry);
		r.initAutonomous();
		
		RingsThread_Callback rtcb = new RingsThread_Callback() {
			@Override
			public void ring_pos(String Type) {
				amount = Type;
				telemetry.addData("Amount", amount);
				telemetry.update();
			}
			
			@Override
			public boolean running() {
				return running;
			}
		};
		
		ring   = new Rings(hardwareMap,telemetry, rtcb);
		ringth = new Thread(ring);
		
		PosThread_Callback ptcb = new PosThread_Callback() {
			@Override
			public void post(double[] a, boolean targeted) {
				location = a;
				is_Targeted = targeted;
			}
		};
		
		pos   = new Position(hardwareMap,telemetry, ptcb);
		posth = new Thread(pos);
		
		ringth.start();
		posth.start();
		
		waitForStart();
		r.timer.startTime();
		
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
		
		moveTo(target); //Move to the specified target location
		
		moveTo(target_launch);
		
		r.Intake.setPower(0.6);
		r.Flywheel.setPower(1);
		r.waiter(1000);
		
		for(int i = 1; i<=3; i++) {
			telemetry.addData("Launching", i);
			r.Launcher.setPower(0.5);
			r.waiter(100);
			r.Launcher.setPower(0);
			r.waiter(250);
			telemetry.addData("Launched", i);
			telemetry.update();
		}
		
		moveTo(target_Line);
		
		//end of the code, end all of the threads and just stop
		running = false;
		ringth.interrupt();
		posth.interrupt();
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
				//r.setDriveMotorMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER); // Run the motors without their encoders

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