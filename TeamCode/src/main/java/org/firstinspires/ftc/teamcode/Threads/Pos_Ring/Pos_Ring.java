/**
 *
 * This is the thread to run both ring recognition and position calculations
 *
 * @see org.firstinspires.ftc.teamcode.Threads.Position_File.Position for
 * info about how the positiong works
 *
 * @see org.firstinspires.ftc.teamcode.Threads.Rings.Rings for info about
 * how the ring recognition works
 *
 * @see org.firstinspires.ftc.teamcode.Threads.Pos_Ring.Pos_RingCallback for
 * information about the thread callback for this thread
 *
 * */

package org.firstinspires.ftc.teamcode.Threads.Pos_Ring;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.SwitchableCamera;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.teamcode.Hardware;

import java.util.ArrayList;
import java.util.List;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XYZ;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.YZX;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC;
import static org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection.BACK;
import static org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection.FRONT;

public class Pos_Ring implements Runnable {
	
	HardwareMap hwMap;           //Create a HardwareMap
	Hardware r = new Hardware(); //Create hardware class for camera
	Telemetry telemetry;         //Create logger
	Pos_RingCallback cb;         //Add the callback interface
	
	//Specific for detecting rings
	private static final String TFOD_MODEL_ASSET = "UltimateGoal.tflite";
	private static final String LABEL_FIRST_ELEMENT = "Quad";
	private static final String LABEL_SECOND_ELEMENT = "Single";
	
	private static final String VUFORIA_KEY =
			"AZdDUFH/////AAABmcE1YUJiRkFeqUd1ljT7cbdPlx6u99cBf3BUJkI0x0olgxQwoyRsI+d8nyiSxYL2wiDc1vclp+Ql47jL6T5X1SYSxpK7xywrV8oRnT46GyN1bCUz7K+vjW5IP7XTP9QzV831LHvu5cjc+++k/KafMAu9tcnEeGGVjqQBoAO01SfFn09TrNc3FyvHBtLHQlGi08VmF2M2koexANGpCG9gcBxWhkPvbbgAyR5MbZ4iiKLUSltYooplimJS/JX/QFqSfqQEMP7Lzq0xX+ngWdUP3Tuc45ggmJjbHTAS3dA1+hD8iFURON2gcw8/nZqsD/GJcxlocvU3FTeFpsIxWd0ow/S3jjQ3ZplJ7PuvTm1BSwfC";
	
	VuforiaLocalizer vuforia;
	TFObjectDetector tfod;
	
	//cameras
	WebcamName webcam1, webcam2; //for the two cameras
	private SwitchableCamera switchableCamera;
	
	//Position vars
	private static final VuforiaLocalizer.CameraDirection CAMERA_CHOICE = BACK;
	private static final boolean PHONE_IS_PORTRAIT = false  ;
	
	private static final float mmPerInch        = 25.4f;
	private static final float mmTargetHeight   = (6) * mmPerInch;          // the height of the center of the target image above the floor
	
	// Constants for perimeter targets
	private static final float halfField = 72 * mmPerInch;
	private static final float quadField  = 36 * mmPerInch;
	
	private OpenGLMatrix lastLocation = null;
	
	private boolean targetVisible = false;
	private float phoneXRotate    = 0;
	private float phoneYRotate    = 0;
	private float phoneZRotate    = 0;
	
	double xyz[] = {0,0,0,0}; //x, y , z, heading
	
	List<VuforiaTrackable> allTrackables = new ArrayList<VuforiaTrackable>();
	
	//End Position vars
	
	public Pos_Ring(HardwareMap hwmap, Telemetry tm, Pos_RingCallback CB){ //Init the class
		hwMap = hwmap;
		telemetry = tm;
		cb = CB;
		initVuforia();
		initTfod();
	}
	
	@Override
	public void run() { //Main method, this is what will run everything
		
		tfod.activate();
		
		while(cb.is_running()){
			
			switch_cam();
			
			if (tfod != null) {
				// getUpdatedRecognitions() will return null if no new information is available since
				// the last time that call was made.
				List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
				if (updatedRecognitions != null) {
//					telemetry.addData("# Object Detected", updatedRecognitions.size());
					// step through the list of recognitions and display boundary info.
					int i = 0;
					for (Recognition recognition : updatedRecognitions) {
//						telemetry.addData(String.format("label (%d)", i), recognition.getLabel());
//						telemetry.addData(String.format("  left,top (%d)", i), "%.03f , %.03f",
//								recognition.getLeft(), recognition.getTop());
//						telemetry.addData(String.format("  right,bottom (%d)", i), "%.03f , %.03f",
//								recognition.getRight(), recognition.getBottom());
						cb.ring(recognition.getLabel());
					}
					//telemetry.update();
				}
				
				if(updatedRecognitions == null){
					cb.ring("none here!");
				}
				
			}
			
			// check all the trackable targets to see which one (if any) is visible.
			targetVisible = false;
			for (VuforiaTrackable trackable : allTrackables) {
				if (((VuforiaTrackableDefaultListener)trackable.getListener()).isVisible()) {
//					telemetry.addData("Visible Target", trackable.getName());
					targetVisible = true;
					
					// getUpdatedRobotLocation() will return null if no new information is available since
					// the last time that call was made, or if the trackable is not currently visible.
					OpenGLMatrix robotLocationTransform = ((VuforiaTrackableDefaultListener)trackable.getListener()).getUpdatedRobotLocation();
					if (robotLocationTransform != null) {
						lastLocation = robotLocationTransform;
					}
					break;
				}
			}
			
			// Provide feedback as to where the robot is located (if we know).
			if (targetVisible) {
				// express position (translation) of robot in inches.
				VectorF translation = lastLocation.getTranslation();
//				telemetry.addData("Pos (in)", "{X, Y, Z} = %.1f, %.1f, %.1f",
//						translation.get(0) / mmPerInch, translation.get(1) / mmPerInch, translation.get(2) / mmPerInch);
				xyz[0] = translation.get(0) / mmPerInch; // X-axis
				xyz[1] = translation.get(1) / mmPerInch; // Y-axis
				xyz[2] = translation.get(2) / mmPerInch; // Z-axis (not used but here for any future projects)
				
				// express the rotation of the robot in degrees.
				Orientation rotation = Orientation.getOrientation(lastLocation, EXTRINSIC, XYZ, DEGREES);
//				telemetry.addData("Rot (deg)", "{Roll, Pitch, Heading} = %.0f, %.0f, %.0f", rotation.firstAngle, rotation.secondAngle, rotation.thirdAngle);
				xyz[3] = rotation.thirdAngle; // Heading in deg
			}
			else {
//				telemetry.addData("Visible Target", "none");
			}
			
			cb.pos(xyz, targetVisible); //send the position back through the callback to the thread that started this thread
			
			//telemetry.update();
		}
		
		if(tfod != null){
			tfod.shutdown();
		}
		
	}
	
	//Change what the input is that you want to pass here
	private void switch_cam(){
		switch (cb.camera()){
			case "Pos":
				switchableCamera.setActiveCamera(webcam1);
				break;
			case "Ring":
				switchableCamera.setActiveCamera(webcam2);
				break;
			default:
				switchableCamera.setActiveCamera(webcam2);
				break;
		}
	}
	
	//Point vuforia at the camera that you want to use
	private void initVuforia() {
		/*
		 * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
		 */
		
		VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();
		
		parameters.vuforiaLicenseKey = VUFORIA_KEY;
		webcam1 = hwMap.get(WebcamName.class, "Webcam 1"); //Position camera
		webcam2 = hwMap.get(WebcamName.class, "Webcam 2"); //Ring camera
		parameters.cameraName = ClassFactory.getInstance().getCameraManager().nameForSwitchableCamera(webcam1, webcam2);
		
		//  Instantiate the Vuforia engine
		vuforia = ClassFactory.getInstance().createVuforia(parameters);
		
		switchableCamera = (SwitchableCamera) vuforia.getCamera();
		switchableCamera.setActiveCamera(webcam2);
		
		initPos();
		
		// Loading trackables is not necessary for the TensorFlow Object Detection engine.
	}
	
	//Start the finding where you are
	private void initPos(){
		//  load reference images
		VuforiaTrackables targetsUltimateGoal = this.vuforia.loadTrackablesFromAsset("UltimateGoal");
		VuforiaTrackable blueTowerGoalTarget = targetsUltimateGoal.get(0);
		blueTowerGoalTarget.setName("Blue Tower Goal Target");
		VuforiaTrackable redTowerGoalTarget = targetsUltimateGoal.get(1);
		redTowerGoalTarget.setName("Red Tower Goal Target");
		VuforiaTrackable redAllianceTarget = targetsUltimateGoal.get(2);
		redAllianceTarget.setName("Red Alliance Target");
		VuforiaTrackable blueAllianceTarget = targetsUltimateGoal.get(3);
		blueAllianceTarget.setName("Blue Alliance Target");
		VuforiaTrackable frontWallTarget = targetsUltimateGoal.get(4);
		frontWallTarget.setName("Front Wall Target");
		
		// For convenience, gather together all the trackable objects in one easily-iterable collection */
		allTrackables.addAll(targetsUltimateGoal);
		
		//Set the position of the perimeter targets with relation to origin (center of field)
		redAllianceTarget.setLocation(OpenGLMatrix
											  .translation(0, -halfField, mmTargetHeight)
											  .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 180)));
		
		blueAllianceTarget.setLocation(OpenGLMatrix
											   .translation(0, halfField, mmTargetHeight)
											   .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, 0)));
		frontWallTarget.setLocation(OpenGLMatrix
											.translation(-halfField, 0, mmTargetHeight)
											.multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0 , 90)));
		
		// The tower goal targets are located a quarter field length from the ends of the back perimeter wall.
		blueTowerGoalTarget.setLocation(OpenGLMatrix
												.translation(halfField, quadField, mmTargetHeight)
												.multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0 , -90)));
		redTowerGoalTarget.setLocation(OpenGLMatrix
											   .translation(halfField, -quadField, mmTargetHeight)
											   .multiplied(Orientation.getRotationMatrix(EXTRINSIC, XYZ, DEGREES, 90, 0, -90)));
		
		// We need to rotate the camera around it's long axis to bring the correct camera forward.
		if (CAMERA_CHOICE == BACK) {
			phoneYRotate = -90;
		} else {
			phoneYRotate = 90;
		}
		
		// Rotate the phone vertical about the X axis if it's in portrait mode
		if (PHONE_IS_PORTRAIT) {
			phoneXRotate = 90 ;
		}
		
		// Next, translate the camera lens to where it is on the robot.
		// In this example, it is centered (left to right), but forward of the middle of the robot, and above ground level.
		final float CAMERA_FORWARD_DISPLACEMENT  = 4.0f * mmPerInch;   // eg: Camera is 4 Inches in front of robot-center
		final float CAMERA_VERTICAL_DISPLACEMENT = 8.0f * mmPerInch;   // eg: Camera is 8 Inches above ground
		final float CAMERA_LEFT_DISPLACEMENT     = 0;     // eg: Camera is ON the robot's center line
		
		OpenGLMatrix robotFromCamera = OpenGLMatrix
											   .translation(CAMERA_FORWARD_DISPLACEMENT, CAMERA_LEFT_DISPLACEMENT, CAMERA_VERTICAL_DISPLACEMENT)
											   .multiplied(Orientation.getRotationMatrix(EXTRINSIC, YZX, DEGREES, phoneYRotate, phoneZRotate, phoneXRotate));
		
		/**  Let all the trackable listeners know where the phone is.  */
		for (VuforiaTrackable trackable : allTrackables) {
			((VuforiaTrackableDefaultListener) trackable.getListener()).setPhoneInformation(robotFromCamera, FRONT);
		}
		
		targetsUltimateGoal.activate();
	}
	
	/**
	 * Initialize the TensorFlow Object Detection engine.
	 */
	private void initTfod() {
		int tfodMonitorViewId = hwMap.appContext.getResources().getIdentifier(
				"tfodMonitorViewId", "id", hwMap.appContext.getPackageName());
		TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
		tfodParameters.minResultConfidence = 0.8f;
		tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
		tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_FIRST_ELEMENT, LABEL_SECOND_ELEMENT);
	}
}

