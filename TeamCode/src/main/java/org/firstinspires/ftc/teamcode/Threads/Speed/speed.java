package org.firstinspires.ftc.teamcode.Threads.Speed;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class speed implements Runnable {
	
	HardwareMap hwMap;
	Telemetry telemetry;
	DcMotor motor;
	
	private final ElapsedTime Timer = new ElapsedTime();
	
	int target_speed;
	
	speedCallback CB;
	
	public speed(HardwareMap hwmap, Telemetry tel, DcMotor tempMotor, speedCallback cb){
		hwMap = hwmap;
		telemetry = tel;
		motor = tempMotor;
		CB = cb;
	}
	
	double speed;
	
	@Override
	public void run() {
		
		if(motor.getMode() != DcMotor.RunMode.RUN_USING_ENCODER)
			motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
		
		int a1, a2;
		
		while(CB.running()){
			target_speed = CB.speed();
			
			a1 = motor.getCurrentPosition();
			waiter(500);
			a2 = motor.getCurrentPosition();
			
			if(a2-a1 < target_speed - 100){
				speed = speed + 0.05;
			}
			
			if(a2-a1 < target_speed){
				speed = speed + 0.01;
			}
			
			if(a2-a1 > target_speed){
				speed = speed - 0.01;
			}
			
			if(a2-a1 > target_speed + 100){
				speed = speed - 0.04;
			}
			
			// launch a ring
			CB.can_Fire(((a2 - a1) <= (target_speed + 10)) && ((a2 - a1) >= (target_speed - 10)), a2-a1);
			if(CB.fire()) {
				motor.setPower(speed);
			}else {
				motor.setPower(0);
			}
		}
		
	}
	
	public void waiter(int time) {
		Timer.reset();
		while (Timer.milliseconds() < time){
			if(!CB.running()){
				break;
			}
		}
	}
}

