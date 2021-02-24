package org.firstinspires.ftc.teamcode.Autos;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Hardware;
import org.firstinspires.ftc.teamcode.Threads.Rings.RingsThread_Callback;
import org.firstinspires.ftc.teamcode.Threads.Rings.Rings;

@Autonomous(name="Rings_Test_Auto", group="Linear Opmode")
@Disabled
public class Rings_Test_Auto extends LinearOpMode {
	
	Hardware r = new Hardware();
	Rings ring;
	RingsThread_Callback rc;
	
	Thread th;
	
	String number = "none";
	boolean running = true;
	
	@Override
	public void runOpMode() throws InterruptedException {
		r.initRobot(hardwareMap, telemetry);
		r.initAutonomous();
		
		rc = new RingsThread_Callback() { // Init the callback for the thread
			@Override
			public void ring_pos(String Type) {
				number = Type;
				telemetry.addData("running", "ring_pos is running");
			}
			
			@Override
			public boolean running() {
				return running;
			}
		};
		
		ring = new Rings(hardwareMap, telemetry, rc);
		th = new Thread(ring);
		
		th.start();
		
		while (!opModeIsActive()){
			telemetry.addData("Amount", number);
			telemetry.update();
		}
		
		running = false;
		
		switch (number){
			case "Single":
				//do something
				telemetry.addData("Donig?", "Single");
				break;
			case "Quad":
				//do something else
				telemetry.addData("Donig?", "Quad");
				break;
			default:
				//do a third thing
				telemetry.addData("Donig?", "None");
				break;
		}
		
		telemetry.update();
		
		r.waiter(10000);
		
		//then go do something else
		
		th.interrupt();
	}
}