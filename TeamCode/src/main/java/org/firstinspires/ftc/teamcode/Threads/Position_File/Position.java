package org.firstinspires.ftc.teamcode.Threads.Position_File;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.firstinspires.ftc.teamcode.Hardware;

import java.util.ArrayList;
import java.util.List;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XYZ;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.YZX;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC;
import static org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection.BACK;
import static org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection.FRONT;

/**
 * This was designed to grab the position of the robot using the camera
 * mounted at the front of the robot.
 *
 * This runs in a separate thread as not to slow down anything that is
 * more important such as TeleOp
 *
 * @see org.firstinspires.ftc.robotcontroller.external.samples.ConceptVuforiaUltimateGoalNavigation
 * for the base code without any threading or custom code
 *
 * */

public class Position implements Runnable{
	
	private static final String VUFORIA_KEY = //Add your Vufoia key here
			"AZdDUFH/////AAABmcE1YUJiRkFeqUd1ljT7cbdPlx6u99cBf3BUJkI0x0olgxQwoyRsI+d8nyiSxYL2wiDc1vclp+Ql47jL6T5X1SYSxpK7xywrV8oRnT46GyN1bCUz7K+vjW5IP7XTP9QzV831LHvu5cjc+++k/KafMAu9tcnEeGGVjqQBoAO01SfFn09TrNc3FyvHBtLHQlGi08VmF2M2koexANGpCG9gcBxWhkPvbbgAyR5MbZ4iiKLUSltYooplimJS/JX/QFqSfqQEMP7Lzq0xX+ngWdUP3Tuc45ggmJjbHTAS3dA1+hD8iFURON2gcw8/nZqsD/GJcxlocvU3FTeFpsIxWd0ow/S3jjQ3ZplJ7PuvTm1BSwfC";
	
	/**
	 *
	 * DO NOT EDIT BEYOND THIS POINT
	 *
	 * */
	
	HardwareMap hwMap;           //Create a HardwareMap
	Hardware r = new Hardware(); //Create hardware class for camera
	Telemetry telemetry;         //Create logger
	PosThread_Callback ptc;      //Add the callback interface
	
	//Vision variables
	private static final VuforiaLocalizer.CameraDirection CAMERA_CHOICE = BACK;
	private static final boolean PHONE_IS_PORTRAIT = false  ;
	
	private static final float mmPerInch        = 25.4f;
	private static final float mmTargetHeight   = (6) * mmPerInch;          // the height of the center of the target image above the floor
	
	// Constants for perimeter targets
	private static final float halfField = 72 * mmPerInch;
	private static final float quadField  = 36 * mmPerInch;
	
	// Class Members
	private OpenGLMatrix lastLocation = null;
	private VuforiaLocalizer vuforia = null;
	
	WebcamName webcamName = null;
	
	private boolean targetVisible = false;
	private float phoneXRotate    = 0;
	private float phoneYRotate    = 0;
	private float phoneZRotate    = 0;
	
	private double angle;                    // Angle of the heading
	private double xyz[] = {0,0,0,0};        // {x,y,z,heading}
	
	List<VuforiaTrackable> allTrackables = new ArrayList<VuforiaTrackable>();
	
//	Vuforia vuforia = new Vuforia();
	
	//End vision variables
	
	boolean running = false;
	
	public Position(HardwareMap hwmap, Telemetry tm, PosThread_Callback PTC){ //Init the class
		hwMap = hwmap;
		telemetry = tm;
		ptc = PTC;
	}
	
	public void init_pos(){ //init class w/ vision
		//if(vuforia.getCamera() != null)
			//vuforia.getCamera().close();
		
		// Load the data sets for the trackable objects. These particular data
		// sets are stored in the 'assets' part of our application.
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
	
	@Override
	public void run() {
		running=true;
		telemetry.addData("Runnable", "Running!"); //tell us the thread is running
		telemetry.update();
		init_pos();
		while(running){ //do this for the entire time this thread is active
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
			
			ptc.post(xyz, targetVisible); //send the position back through the callback to the thread that started this thread
			
			//telemetry.update();
		}
	}
	
}