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
	
	int target_speed, speed = 0;
	
	public speed(HardwareMap hwmap, Telemetry tel, DcMotor tempMotor){
		hwMap = hwmap;
		telemetry = tel;
		motor = tempMotor;
	}
	
	double power;
	
	boolean running, fire, canFire = false;
	
	@Override
	public void run() {
		running = true;
		
		if(motor.getMode() != DcMotor.RunMode.RUN_USING_ENCODER)
			motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
		
		int a1, a2;
		
		while(running){
			a1 = motor.getCurrentPosition();
			waiter(500);
			a2 = motor.getCurrentPosition();
			
			speed = a2-a1;
			
			if(speed < target_speed - 100){
				power = power + 0.05;
			}
			
			if(speed < target_speed){
				power = power + 0.01;
			}
			
			if(speed > target_speed){
				power = power - 0.01;
			}
			
			if(speed > target_speed + 100){
				power = power - 0.04;
			}
			
			// launch a ring
			
			if(canFire) {
				motor.setPower(power);
			}else {
				motor.setPower(0);
				motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
				power = 0.5;
				motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
			}
		}
		
	}
	
	public synchronized void set_Speed(int speed){
		target_speed = speed;
	}
	
	public synchronized void spin(boolean Fire){
		canFire = Fire;
	}
	
	public synchronized void start_spinning(){
		fire = true;
	}
	
	public synchronized void stop_spinning(){
		fire = false;
	}
	
	public synchronized boolean can_Fire(){
		return canFire;
	}
	
	public synchronized void stop(){
		running = false;
	}
	
	public synchronized int[] speed(){
		return new int[]{speed, target_speed};
	}
	
	public void waiter(int time) {
		Timer.reset();
		while (Timer.milliseconds() < time){
			if(!running){
				break;
			}
		}
	}
}

