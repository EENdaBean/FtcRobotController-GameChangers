package org.firstinspires.ftc.teamcode.Threads.Speed;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class speed implements Runnable {
	
	HardwareMap hwMap;
	Telemetry telemetry;
	DcMotor motor;
	
//	Hardware r = new Hardware();
	
	speedCallback CB;
	
	speed(HardwareMap hwmap, Telemetry tel, DcMotor tempMotor, speedCallback cb){
		hwMap = hwmap;
		telemetry = tel;
		motor = tempMotor;
		CB = cb;
	}
	
	int a;
	int b;
	int speed;
	
	@Override
	public void run() {
		
		//r.initRobot(hwMap, telemetry);
		
		if(motor.getMode() != DcMotor.RunMode.RUN_USING_ENCODER)
			motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
		
		while(true){
			while(CB.running()){
				a = motor.getCurrentPosition();
				try {
					wait(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				b = motor.getCurrentPosition();
				
				speed = b-a;
				telemetry.addLine().addData("Speed",speed);
			}
		}
		
	}
}

