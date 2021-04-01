package org.firstinspires.ftc.teamcode.Threads.Speed;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class speed implements Runnable {
	
	Telemetry telemetry;
	DcMotor motor;
	DcMotor[] driveMotors;
	
	private final ElapsedTime Timer = new ElapsedTime();
	
	int target_speed, speed = 0;
	
	public speed(Telemetry tel, DcMotor tempMotor){
		telemetry = tel;
		motor = tempMotor;
	}
	
	public speed(Telemetry tel, DcMotor flywheel, DcMotor[] drivemotors){
		telemetry = tel;
		motor = flywheel;
		driveMotors = drivemotors;
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
			
			canFire = target_speed - speed < 5 && target_speed - speed > -5;
			
			if(fire) {
				motor.setPower(power);
			}else {
				motor.setPower(0);
				motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
				power = 0.5;
				motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
			}
			
			if(driveMotors != null){
			
			}
			
		}
		
	}
	
	/**
	 * Set the speed of the Flywheel
	 *
	 * @param speed set the target speed we want the flywheel to spin
	 */
	
	public synchronized void set_Speed(int speed){
		target_speed = speed;
	}
	
	/**
	 * Spin the Flywheel in TeleOp
	 *
	 * @param Fire tell this thread to start or stop spinning
	 */
	public synchronized void spin(boolean Fire){
		fire = Fire;
	}
	
	/** Start spinning in Auto */
	public synchronized void start_spinning(){
		fire = true;
	}
	
	/** Stop spinning is Auto */
	public synchronized void stop_spinning(){
		fire = false;
	}
	
	/** Tell the main thread if we can fire a ring */
	public synchronized boolean can_Fire(){
		return canFire;
	}
	
	/** Stops the thread */
	public synchronized void stop(){
		fire = false;
		running = false;
	}
	
	// Tell the main thread what the speed of the Flywheel is
	// and what we have it set to
	public synchronized int[] get_speed(){
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