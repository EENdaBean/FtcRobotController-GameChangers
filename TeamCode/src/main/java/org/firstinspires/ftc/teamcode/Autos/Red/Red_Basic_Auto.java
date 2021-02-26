package org.firstinspires.ftc.teamcode.Autos.Red;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.AutoTransitioner;
import org.firstinspires.ftc.teamcode.Autos.Auto;
import org.firstinspires.ftc.teamcode.Hardware;

@Autonomous(name="Red_Basic_Auto", group="Comp")
//@Disable
public class Red_Basic_Auto extends LinearOpMode {

    Hardware r = new Hardware();

    @Override
    public void runOpMode() {
        AutoTransitioner.transitionOnStop(this, "TeleOp_Basic");
        r.initRobot(hardwareMap, telemetry);
        r.initAutonomous();
        
        // Wait for the game to start (driver presses PLAY)
        waitForStart();

    }
}
