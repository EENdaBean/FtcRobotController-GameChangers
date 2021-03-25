package org.firstinspires.ftc.teamcode.Tests;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.Hardware;

@Autonomous(name = "Motor_Check", group = "Test")
//@Disabled
public class Motor_Check extends LinearOpMode {
	
	Hardware r = new Hardware();
	
	int initial, end;
	DcMotor[] motors;
	
	boolean TF = false;
	
	@Override
	public void runOpMode() {
		r.initRobot(hardwareMap, telemetry);
		r.initAutonomous();
		
		motors = r.All_Motors;
		
		telemetry.setAutoClear(false);
		
		telemetry.addData("Pre-Check", "Commencing Pre-Check");
		telemetry.update();
		
		for (DcMotor dcMotor : motors) {
			dcMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
			dcMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
		}
		
		telemetry.addData("Pre-Check", "Pre-Check Complete");
		telemetry.addData("Waiting", " to start");
		telemetry.update();
		
		waitForStart();
		
		telemetry.addData("Starting", "Yay!");
		telemetry.update();
		
		// This class was specifically developed to test all of our motors
		// before a match, this insures that our motors are not burnt out
		// or not working before we require it
		
		// To start we are going to test our driving wheels
		// actually.. lets just do everything in one loop
		for (DcMotor motor : motors) {
			initial = motor.getCurrentPosition();
			motor.setPower(0.6);
			r.waiter(500);
			end = motor.getCurrentPosition();
			motor.setPower(0);
			telemetry.addLine(motor.getPortNumber() + " |")
					.addData("initial", initial)
					.addData("ending", end)
					.addData("Error?", initial == end);
			telemetry.update();
			
			if(initial == end){
				TF = true;
			}
			
		}
		
		telemetry.addData("Any errors?", TF);
		telemetry.update();
		
		telemetry.addLine("==============================");
		telemetry.addLine("This concludes the test, please refer to the information above for any errors with the motors! Have a good day ;)");
		telemetry.update();
		
		// Continue displaying the telemetry
		while (true) if (isStopRequested()) break;
		
		
	}
}