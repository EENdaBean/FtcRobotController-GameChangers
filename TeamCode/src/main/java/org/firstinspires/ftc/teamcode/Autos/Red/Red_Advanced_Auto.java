package org.firstinspires.ftc.teamcode.Autos.Red;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.AutoTransitioner;
import org.firstinspires.ftc.teamcode.Hardware;
import org.firstinspires.ftc.teamcode.Threads.Rings.Rings;
import org.firstinspires.ftc.teamcode.Threads.Rings.RingsThread_Callback;

@Autonomous(name = "Red_Advanced_Auto", group = "Comp")
//@Disabled
public class Red_Advanced_Auto extends LinearOpMode {
	
	Hardware r = new Hardware();
	
	Runnable ring;
	Thread ringth;
	
	String amount;
	boolean running = true;  //if the thread is to be running
	
	@Override
	public void runOpMode() throws InterruptedException {
		AutoTransitioner.transitionOnStop(this, "TeleOp_Basic");
		r.initRobot(hardwareMap,telemetry);
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
	//	r.Timer.startTime();
		
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