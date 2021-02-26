/**
 *
 * This is the advanced version of our autonomous period for the Ultimate Goal season
 * This is for the Blue side of the field for either line
 *
 * This code will view the rings on the field and move to a position only based on time
 * @see org.firstinspires.ftc.teamcode.Autos.Blue.Blue_Supreme_Auto for information about
 * a more advanced version of the code that moves based on coordnates
 *
 * @see org.firstinspires.ftc.teamcode.Autos.Blue.Blue_Basic_Auto for infrmation about
 * a more basic version of the blue side auto
 *
* */


package org.firstinspires.ftc.teamcode.Autos.Blue;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.AutoTransitioner;
import org.firstinspires.ftc.teamcode.Hardware;
import org.firstinspires.ftc.teamcode.Threads.Position_File.PosThread_Callback;
import org.firstinspires.ftc.teamcode.Threads.Position_File.Position;
import org.firstinspires.ftc.teamcode.Threads.Rings.Rings;
import org.firstinspires.ftc.teamcode.Threads.Rings.RingsThread_Callback;

@Autonomous(name="Blue_Advanced_Auto", group="Comp")
//@Disabled
public class Blue_Advanced_Auto extends LinearOpMode {
	
	Hardware r = new Hardware();
	
	Runnable ring;
	Thread ringth;
	
	String amount;
	boolean running = true;  //if the thread is to be running
	
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
		
		ringth.start();
		
		waitForStart();
		r.timer.startTime();
		
		switch (amount){
			case "Single" :
				//move to a position
				r.setMotorEncoderForward(10);
				break;
			case "Quad" :
				//move to a position
				r.setMotorEncoderForward(20);
				break;
			default:
				//move to a position
				r.setMotorEncoderForward(5);
				break;
		}
		
	}
}