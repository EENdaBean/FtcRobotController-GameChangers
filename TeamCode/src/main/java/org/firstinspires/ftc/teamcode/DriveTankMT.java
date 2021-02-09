//// simple teleop program that drives bot using controller joysticks in tank mode.
//// this code monitors the period and stops when the period is ended.
//
//package org.firstinspires.ftc.teamcode;
//
//import android.annotation.SuppressLint;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
//
//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
//import com.qualcomm.robotcore.hardware.DcMotor;
//import com.qualcomm.robotcore.util.Range;
//
//import static android.os.Looper.myLooper;
//
//@TeleOp(name="Drive Tank Multi-Thread", group="Exercises")
////@Disabled
//public class DriveTankMT extends LinearOpMode
//{
//	private static final int UPDATE_COUNT = 101;
//	DcMotor leftMotor, rightMotor;
//	float   leftY, rightY;
//
//	Handler h;
//
//	// called when init button is  pressed.
//	@Override
//	public void runOpMode() throws InterruptedException
//	{
//		//leftMotor = hardwareMap.dcMotor.get("left_motor");
//		//rightMotor = hardwareMap.dcMotor.get("right_motor");
//
//		//leftMotor.setDirection(DcMotor.Direction.REVERSE);
//
//		// create an instance of the DriveThread.
//
//		Thread  driveThread = new DriveThread();
//
//		telemetry.addData("Mode", "waiting");
//		telemetry.update();
//
//		Looper l;
//
//		// wait for start button.
//
//		telemetry.addData("Thread","wait for start");
//
//		waitForStart();
//
//
//		// continue with main thread.
//
////		Looper.prepare();
////		l = myLooper();
////
////		h = new Handler(){
////			//@SuppressLint("HandlerLeak")
////			public void handleMessage(Message msg){
////				telemetry.addData("Threaded message", msg.arg1);
////				telemetry.update();
////			}
////		};
////
////		Looper.loop();
//
//
//
//		// start the driving thread.
//
//		driveThread.start();
//
//		try
//		{
//			if(opModeIsActive()) {
//				while (opModeIsActive()) {
//					idle();
//
//				}
//			}
//		}
//		catch(Exception e) {}
////
//		// stop the driving thread.
//
//		//l.quit();
//
//		driveThread.interrupt();
//
//	}
//
//	private class DriveThread extends Thread
//	{
//		public DriveThread()
//		{
//			this.setName("DriveThread");
//		}
//
//		// called when tread.start is called. thread stays in loop to do what it does until exit is
//		// signaled by main code calling thread.interrupt.
//		@Override
//		public void run()
//		{
//			//Handler h = null;
//			int i = 0;
//
//
//			Looper.prepare();
//			//Looper.prepare();
//
//			h = new Handler() {
//				//@SuppressLint("HandlerLeak")
//				@Override
//				public void handleMessage(Message msg) {
//					telemetry.addData("Threaded message", msg.arg1);
//					telemetry.update();
//				}
//			};
//			Looper.loop();
//
//			try
//			{
//				while (!isInterrupted())
//				{
//					if(!opModeIsActive()){
//						return;
//					}
//					if(isInterrupted()){
//						return;
//					}
//					// we record the Y values in the main class to make showing them in telemetry
//					// easier.
//
//					//leftY = gamepad1.left_stick_y * -1;
//					//rightY = gamepad1.right_stick_y * -1;
//
//					//leftMotor.setPower(Range.clip(leftY, -1.0, 1.0));
//					//rightMotor.setPower(Range.clip(rightY, -1.0, 1.0));
//					//telemetry.addData("In Thread", "Running!");
//					//telemetry.update();
//					idle();
//
////					Looper.prepare();
//
//					Message message = new Message();
//					message.what = UPDATE_COUNT;
//					message.arg1 = i;
//					h.sendMessage(message);
//					i++;
////					Looper.loop();
//
//					if(gamepad1.a){
//						//Looper.quit();
//						//myLooper().quit();
//						this.interrupt();
//						requestOpModeStop();
//					}
//					if(gamepad1.b){
//						telemetry.addData("Thread", "thread stopping");
//						telemetry.update();
//						this.interrupt();
//						requestOpModeStop();
//						telemetry.addData("Thread", "Thread stoped?");
//						telemetry.update();
//					}
//
//				}
//				//return;
//			}
//			// interrupted means time to shutdown. note we can stop by detecting isInterrupted = true
//			// or by the interrupted exception thrown from the sleep function.
//			// an error occurred in the run loop.
//			catch (Exception e) {}
//			//return;
//		}
//
//	}
//}