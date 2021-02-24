package org.firstinspires.ftc.teamcode.Threads.Rings;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.teamcode.Hardware;

import java.util.List;

public class Rings implements Runnable {
	
	HardwareMap hwMap;           //Create a HardwareMap
	Hardware r = new Hardware(); //Create hardware class for camera TODO: along with IMU
	Telemetry telemetry;         //Create logger
	RingsThread_Callback rtc;      //Add the callback interface
	
	//Specific for detecting rings
	private static final String TFOD_MODEL_ASSET = "UltimateGoal.tflite";
	private static final String LABEL_FIRST_ELEMENT = "Quad";
	private static final String LABEL_SECOND_ELEMENT = "Single";
	
	private static final String VUFORIA_KEY =
			"AZdDUFH/////AAABmcE1YUJiRkFeqUd1ljT7cbdPlx6u99cBf3BUJkI0x0olgxQwoyRsI+d8nyiSxYL2wiDc1vclp+Ql47jL6T5X1SYSxpK7xywrV8oRnT46GyN1bCUz7K+vjW5IP7XTP9QzV831LHvu5cjc+++k/KafMAu9tcnEeGGVjqQBoAO01SfFn09TrNc3FyvHBtLHQlGi08VmF2M2koexANGpCG9gcBxWhkPvbbgAyR5MbZ4iiKLUSltYooplimJS/JX/QFqSfqQEMP7Lzq0xX+ngWdUP3Tuc45ggmJjbHTAS3dA1+hD8iFURON2gcw8/nZqsD/GJcxlocvU3FTeFpsIxWd0ow/S3jjQ3ZplJ7PuvTm1BSwfC";
	
	VuforiaLocalizer vuforia;
	TFObjectDetector tfod;
	
	public Rings(HardwareMap hwmap, Telemetry tm, RingsThread_Callback RTC){ //Init the class
		hwMap = hwmap;
		telemetry = tm;
		rtc = RTC;
	}
	
	boolean running = false;
	
	@Override
	public void run() {
		running = true;
		initVuforia();
		initTfod();
		
		if (tfod != null)
			tfod.activate();
		
		String type = "none";
		
		while (rtc.running()) {
			if (tfod != null) {
				// getUpdatedRecognitions() will return null if no new information is available since
				// the last time that call was made.
				List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
				if (updatedRecognitions != null) {
					telemetry.addData("# Object Detected", updatedRecognitions.size());
					// step through the list of recognitions and display boundary info.
					int i = 0;
					for (Recognition recognition : updatedRecognitions) {
						telemetry.addData(String.format("label (%d)", i), recognition.getLabel());
						telemetry.addData(String.format("  left,top (%d)", i), "%.03f , %.03f",
								recognition.getLeft(), recognition.getTop());
						telemetry.addData(String.format("  right,bottom (%d)", i), "%.03f , %.03f",
								recognition.getRight(), recognition.getBottom());
						rtc.ring_pos(recognition.getLabel());
					}
					telemetry.update();
				}else{
					//rtc.ring_pos("none");
				}
			}
		}
		
		if (tfod != null) {
			tfod.shutdown();
		}
		
	}
	
	private void initVuforia() {
		/*
		 * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
		 */
		VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();
		
		parameters.vuforiaLicenseKey = VUFORIA_KEY;
		parameters.cameraName = hwMap.get(WebcamName.class, "Webcam 1"); //Ring camera
		
		//  Instantiate the Vuforia engine
		vuforia = ClassFactory.getInstance().createVuforia(parameters);
		
		
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
		
		//allTrackables.addAll(targetsUltimateGoal);
		
		// Loading trackables is not necessary for the TensorFlow Object Detection engine.
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
