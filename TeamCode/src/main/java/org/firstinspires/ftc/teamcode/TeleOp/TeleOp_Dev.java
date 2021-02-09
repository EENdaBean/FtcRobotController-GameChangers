package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Hardware;
import org.firstinspires.ftc.teamcode.Position_File.PosThread_Callback;
import org.firstinspires.ftc.teamcode.Position_File.Position;

@TeleOp(name="TeleOp_Dev - Position", group="Iterative Opmode")
//@Disabled
public class TeleOp_Dev extends OpMode {
    
    Thread th;
    Runnable rn;
    
    Hardware r = new Hardware();
    
    double i[] = {0,0,0};
    
    @Override
    public void init() {
        PosThread_Callback cb = new PosThread_Callback() {
            @Override
            public void post(double[] a) {
                i[0] = a[0];
                i[1] = a[1];
                i[2] = a[2];
            }
        };
        r.initRobot(hardwareMap, telemetry);
        rn = new Position(hardwareMap,telemetry, cb);
        th = new Thread(rn);
        th.start();
    }
    
    @Override
    public void loop() {
        telemetry.addLine("Num - ")
                .addData("One",i[0])
                .addData("Two",i[1])
                .addData("Three",i[2]);
        telemetry.update();
    }
    
    @Override
    public void stop() {
        th.interrupt();
        super.stop();
    }
    
}
