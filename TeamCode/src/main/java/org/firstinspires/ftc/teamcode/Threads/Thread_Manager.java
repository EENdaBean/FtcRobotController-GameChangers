package org.firstinspires.ftc.teamcode.Threads;


import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Hardware;
import org.firstinspires.ftc.teamcode.Threads.Pos_Ring.Pos_Ring;
import org.firstinspires.ftc.teamcode.Threads.Pos_Ring.Pos_RingCallback;
import org.firstinspires.ftc.teamcode.Threads.Speed.speed;

/**
 * This class is meant to assist in the creation and management of out threads
 */
public class Thread_Manager {
	
	HardwareMap hwMap;
	Hardware r;
	Telemetry telemetry;
	
	Pos_Ring Pos_Ring;
	Thread Pos_Ring_TH;
	
	speed sp;
	Thread spTh;
	
	double[] location = {0,0,0,0};
	boolean isTargeted = false;
	String amount = "none";
	
	Pos_RingCallback Pos_Ring_Callback = new Pos_RingCallback(){
		
		@Override
		public void ring(String Amount) {
			amount = Amount;
		}
		
		@Override
		public void pos(double[] a, boolean targeted) {
			location = a;
			isTargeted = targeted;
		}
	};
	
	/**
	 * This class is meant to assist in the creation and management of out threads
	 * @param hwtemp Init your HardwareMap
	 * @param rT Pass your hardware file
	 * @param telemetryT Pass your telemetry
	 */
	public Thread_Manager(HardwareMap hwtemp, Hardware rT, Telemetry telemetryT){
		hwMap = hwtemp;
		r = rT;
		telemetry = telemetryT;
		r.initAutonomous();
		init();
	}
	
	private void init(){
		Pos_Ring = new Pos_Ring(hwMap, telemetry, Pos_Ring_Callback);
		Pos_Ring_TH = new Thread(Pos_Ring);
		
		sp = new speed(telemetry, r.Flywheel);
		spTh = new Thread(sp);
	}
	
	/**
	 * Start the threads, choose which one
	 * @param which <b>Pos_Ring</b> for just positioning, <b>speed</b> for just a speed controller, and <b>All</b> for all of them
	 */
	public void start_Threads(String which){
		switch (which){
			case "Pos_Ring":
				if(Pos_Ring_TH.isAlive())
					break;
				Pos_Ring_TH.start();
				break;
			case "speed":
				if(spTh.isAlive())
					break;
				spTh.start();
				break;
			case "All":
				if(spTh.isAlive() || Pos_Ring_TH.isAlive())
					break;
				spTh.start();
				Pos_Ring_TH.start();
				break;
			default:
				throw new IllegalStateException("Unexpected value: " + which);
		}
	}
	
	/**
	 * Get the location of the robot
	 * @return location of the robot (x,y,z,heading)
	 */
	public double[] get_Location(){
		return location;
	}
	
	/**
	 * Return weather or not we are detecting a VuMark
	 * @return if VuMark is in view
	 */
	public boolean isTargeted(){
		return isTargeted;
	}
	
	/**
	 * Get the speed of the flywheel
	 * @return speed of flywheel
	 */
	public int[] get_speed(){
		return sp.get_speed();
	}
	
	/**
	 * Switch which camera we're using
	 * @param camera <b>Pos</b> if we want the position camera
	 *               <b>Ring</b> if we want the ring camera
	 */
	public void cam(String camera){
		Pos_Ring.switch_cam(camera);
	}
	
	/**
	 * This method is used to move us around the field accurately
	 * <p>
 	 * Default speed is 0.6
	 * <p>
	 * By default you will not rotate
	 * @param location pass the location you want to move to (x,y)
	 */
	public void moveTo(int[] location){
	
	}
	
	/**
	 * This method is used to move us around the field accurately
	 * <p>
 	 * Speed will be set to the parameter (0.0 -> 1.0)
	 * <p>
	 * By default you will not rotate
	 * @param location pass the location you want to move to (x,y)
	 * @param speed pass a motor power (0.0 - 1.0)
	 */
	public void moveTo(int[] location, double speed){
	
	}
	
	/**
	 * This method is used to move us around the field accurately
	 * <p>
 	 * This one will move at the specified speed and will rotate to align with the target
	 * @param location pass the location you want to move to (x,y)
	 * @param speed pass a motor power (0.0 - 1.0)
	 * @param rotate boolean on if you want to line up with the target
	 */
	public void moveTo(int[] location, double speed, boolean rotate){
	
	}
	
	/**
	 * This is meant to align with a target point that is in view
	 */
	public void align(){
	
	}
	
	/**
	 * Stop all threads
	 */
	public void stop(){
		if(Pos_Ring_TH.isAlive()){
			Pos_Ring.stop();
			Pos_Ring_TH.interrupt();
		}
		if(spTh.isAlive()){
			sp.stop();
			spTh.interrupt();
		}
	}
}
