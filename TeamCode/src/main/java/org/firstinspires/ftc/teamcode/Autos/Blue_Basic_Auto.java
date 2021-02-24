/**
 *
 * This autonomous is made to "randomly" move to a box
 * so have a one in three chance of getting the correct position
 *
 */

package org.firstinspires.ftc.teamcode.Autos;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.AutoTransitioner;
import org.firstinspires.ftc.teamcode.Hardware;

@Autonomous(name="Blue_Basic_Auto", group="Comp")
//@Disable
public class Blue_Basic_Auto extends LinearOpMode {

    Hardware r = new Hardware();

    @Override
    public void runOpMode() {
        AutoTransitioner.transitionOnStop(this, "TeleOp_Basic");
        r.initRobot(hardwareMap, telemetry);
        r.initAutonomous();
        
        waitForStart();//Wait for us to start the game
    }
}
