package org.firstinspires.ftc.teamcode.Tests;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Hardware;

@TeleOp(name="Test_Tele", group="Iterative Opmode")
//@Disabled
public class Test extends OpMode {

    boolean inReverse=false;//reverse button is b
    boolean bWasPressed=false;

    Hardware r = new Hardware();

    @Override
    public void init() {

        r.initRobot(hardwareMap, telemetry);
        //r.deInitColor(hardwareMap);
    }

    boolean a = true;

    @Override
    public void loop() {
        double power1;
        double power2;
        double power3;
        double power4;
        if(a == true) {
            double angle = 3*(Math.PI)/2;

            double velocity = 0.6;
            double rotation = 0;

            if (inReverse)//reverse button
                angle += Math.toRadians(180);

            angle += Math.toRadians(270);

            //equations taking the polar coordinates and turning them into motor powers
            double vx = velocity * Math.cos(angle + (Math.PI / 4));
            double vy = velocity * Math.sin(angle + (Math.PI / 4));

            power1 = vx - rotation;
            power2 = vy + rotation;
            power3 = vy - rotation;
            power4 = vx + rotation;
            telemetry.addData("Angle", angle);
        }else{
            power1 = 0;
            power2 = 0;
            power3 = 0;
            power4 = 0;
        }
        r.frontLeft.setPower(power1);
        r.frontRight.setPower(power2);
        r.backLeft.setPower(power3);
        r.backRight.setPower(power4);

    }
}
