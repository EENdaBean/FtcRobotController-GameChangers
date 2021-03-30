package org.firstinspires.ftc.teamcode.Autos;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Hardware;

@Autonomous(name = "Tester", group = "Test")
@Disabled
public class Tester extends LinearOpMode {
	
	Hardware r = new Hardware();
	
	@Override
	public void runOpMode() {
		r.initRobot(hardwareMap,telemetry);
		r.initAutonomous();
		
		int targetSpeed = 700; //This means we are going 700 ticks ber 1/2 second which is 70rev/s or 4200rev/min
		
		waitForStart();
		
		while(opModeIsActive()) {
			r.Wobble.setPosition(1);
			r.waiter(1000);
			r.Wobble.setPosition(0);
			r.waiter(1000
			);
		}
//		r.Flywheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//		r.Flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
//		int launch = 0;
//		int a1, a2;
//		double speed = 0.5;
//		do{
//			//change speed based on encoder directions
//			a1 = r.Flywheel.getCurrentPosition();
//			telemetry.addData("before", a1);
//			r.waiter(500);
//			a2 = r.Flywheel.getCurrentPosition();
//			telemetry.addData("after", a2);
//			if (!(a2 - a1 >= targetSpeed - 5) && !(a2 - a1 <= targetSpeed + 5)) {
//				if (a2 - a1 < targetSpeed - 100) {
//					speed = speed + 0.04;
//				}
//				if (a2 - a1 < targetSpeed) {
//					speed = speed + 0.01;
//				} else if (a2 - a1 > targetSpeed) {
//					speed = speed - 0.01;
//				}
//			}
//			if(((a2-a1) <= (targetSpeed+10)) && ((a2-a1) >= (targetSpeed-10))){
//				// launch a ring
//				r.Launcher.setPower(1);
//				r.waiter(1000);
//				r.Launcher.setPower(0);
//				launch++;
//			}
//			r.Flywheel.setPower(speed);
//			telemetry.addData("Speed", a2-a1);
//			telemetry.addData("Target Speed", targetSpeed);
//			telemetry.update();
//		}while(launch != 20 && opModeIsActive());
		
		
	}
}