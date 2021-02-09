package org.firstinspires.ftc.teamcode.Tests;

import android.hardware.Sensor;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.hardware.bosch.NaiveAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcontroller.external.samples.SensorBNO055IMU;
import org.firstinspires.ftc.robotcore.external.Func;
import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.teamcode.Hardware;

import java.util.Locale;

import static android.hardware.Sensor.TYPE_GAME_ROTATION_VECTOR;

@TeleOp(name="Test_Tele", group="Iterative Opmode")
//@Disabled
public class Test extends OpMode {

    boolean inReverse=false;//reverse button is b
    boolean bWasPressed=false;
    
    // The IMU sensor object
    BNO055IMU imu;
    public int sm = TYPE_GAME_ROTATION_VECTOR;
    // State used for updating telemetry
    Orientation angles;
    Acceleration gravity;
    
    Position n = new Position();

    @Override
    public void init() {
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
        parameters.loggingEnabled      = true;
        parameters.loggingTag          = "IMU";
//        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();
//        parameters.mode = BNO055IMU.SensorMode.ACCGYRO;
        
        // Retrieve and initialize the IMU. We expect the IMU to be attached to an I2C port
        // on a Core Device Interface Module, configured to be a sensor of type "AdaFruit IMU",
        // and named "imu".
        imu = hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);
        
        n.x = 0;
        n.y = 0;
        n.z = 0;
        
        imu.startAccelerationIntegration(n, null, 1);
        
        //composeTelemetry();
    }

    boolean a = true;

    @Override
    public void loop() {
        telemetry.addLine("Acceleration - ")
                .addData("X", imu.getLinearAcceleration().xAccel)
                .addData("Y", imu.getLinearAcceleration().yAccel)
                .addData("Z", imu.getLinearAcceleration().zAccel);
        telemetry.addLine("Position - ")
                .addData("X", imu.getPosition().x)
                .addData("Y", imu.getPosition().y)
                .addData("Z", imu.getPosition().z);
        telemetry.addLine("Velocity - ")
                .addData("X", imu.getVelocity().xVeloc)
                .addData("Y", imu.getVelocity().yVeloc)
                .addData("Z", imu.getVelocity().zVeloc);
        telemetry.addLine("Position? - ")
                .addData("X", n.x)
                .addData("Y", n.y)
                .addData("Z", n.z);
        telemetry.update();
    }
    
    void composeTelemetry() {
        
        // At the beginning of each telemetry update, grab a bunch of data
        // from the IMU that we will then display in separate lines.
        telemetry.addAction(new Runnable() { @Override public void run()
        {
            // Acquiring the angles is relatively expensive; we don't want
            // to do that in each of the three items that need that info, as that's
            // three times the necessary expense.
            angles   = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
            gravity  = imu.getGravity();
        }
        });
        
        telemetry.addLine()
                .addData("status", new Func<String>() {
                    @Override public String value() {
                        return imu.getSystemStatus().toShortString();
                    }
                })
                .addData("calib", new Func<String>() {
                    @Override public String value() {
                        return imu.getCalibrationStatus().toString();
                    }
                });
        
        telemetry.addLine()
                .addData("heading", new Func<String>() {
                    @Override public String value() {
                        return formatAngle(angles.angleUnit, angles.firstAngle);
                    }
                })
                .addData("roll", new Func<String>() {
                    @Override public String value() {
                        return formatAngle(angles.angleUnit, angles.secondAngle);
                    }
                })
                .addData("pitch", new Func<String>() {
                    @Override public String value() {
                        return formatAngle(angles.angleUnit, angles.thirdAngle);
                    }
                });
        
        telemetry.addLine()
                .addData("grvty", new Func<String>() {
                    @Override public String value() {
                        return gravity.toString();
                    }
                })
                .addData("mag", new Func<String>() {
                    @Override public String value() {
                        return String.format(Locale.getDefault(), "%.3f",
                                Math.sqrt(gravity.xAccel*gravity.xAccel
                                                  + gravity.yAccel*gravity.yAccel
                                                  + gravity.zAccel*gravity.zAccel));
                    }
                });
    }
    
    String formatAngle(AngleUnit angleUnit, double angle) {
        return formatDegrees(AngleUnit.DEGREES.fromUnit(angleUnit, angle));
    }
    
    String formatDegrees(double degrees){
        return String.format(Locale.getDefault(), "%.1f", AngleUnit.DEGREES.normalize(degrees));
    }
}

