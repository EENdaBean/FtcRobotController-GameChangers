/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Hardware {
    //created by team 9161 overload on 1/31/20

    /*
     *
     * To add DC motor, use "public DCMotor %var name%;"
     *
     * To add Servo, use "public Servo %var name%;"
     *
     * ====!then refer to initHardware()!====
     *
     * */

    public DcMotor frontLeft, backLeft, frontRight, backRight;    // Drive motors
    public DcMotor Intake, Launcher, Flywheel;                    //Launcher motors
    public DcMotor[] Drive_Motors;
    public DcMotor[] All_Motors;
    
    public Servo Wobble;
    public Servo[] Servos;

    Telemetry telemetry;
    HardwareMap hwMap;

    private final ElapsedTime Timer = new ElapsedTime();
    public ElapsedTime timer = new ElapsedTime();

    public static final int ticksPerInch=56;

    public static final int encoderSafeZone=50;/*a motor must be within this many ticks of its
   target to be considered "on target"*/

//    public static final int minRotDist=0;

    //angle variables
//    public static final double ticksPerDeg=9.7; //the number of ticks it takes for the axle of the motor to rotate 1 deg, doing a (28(5*5*5))/360 = 9.7

//    public static final int GoalHeight=92;  //Height of the goal from the ground

    
    public void initRobot(HardwareMap spareMap, Telemetry tempTelemetry){
        hwMap = spareMap;
        telemetry = tempTelemetry;
        initHardware();
    }

    public void initHardware() {
        /*
         *
         * To init a DC motor, use %var name% = hwMap.dcMotor.get("%name of motor%");
         * To set the direction of DC motor, use %var name%.setDirection(DcMotorSimple.Direction.REVERSE);
         *                                                                                       FORWARDS
         *
         * L = Left
         * R = Right
         * F = Front
         * B = Back
         * M = Motor
         *
         * I = intake
         * Lau = Launcher
         *
         *
         *  ___        _____
         * |   |       |   |
         * |FLM|       |FRM|
         * |   ‾‾‾‾‾‾‾‾    |
         * |   ________    |
         * |BLM|       |BRM|
         * |   |       |   |
         * ‾‾‾‾‾       ‾‾‾‾‾
         *
         *
         * To map servo, use %var name% = hwMap.servo.get("%name of servo%");
         *
         * */
        //Wheels
        frontLeft = hwMap.dcMotor.get("FLM");  //Front left motor
        frontRight = hwMap.dcMotor.get("FRM"); //Front right motor
        backLeft = hwMap.dcMotor.get("BLM");   //Back left motor
        backRight = hwMap.dcMotor.get("BRM");  //Back right motor
        
        Intake = hwMap.dcMotor.get("intake");    //Intake motor
        Launcher = hwMap.dcMotor.get("push");    //Launcher motor
        Flywheel = hwMap.dcMotor.get("launch");  //Launcher flywheel
        
        Flywheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        Intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        Launcher.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        
        //Intake.setDirection(DcMotorSimple.Direction.REVERSE);
        Launcher.setDirection(DcMotorSimple.Direction.REVERSE);
        
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        
        Drive_Motors = new DcMotor[]{frontLeft, frontRight, backLeft, backRight};
        All_Motors = new DcMotor[]{frontLeft, frontRight, backLeft, backRight, Intake, Launcher, Flywheel};
    
        Wobble = hwMap.servo.get("wob");
        
        Servos = new Servo[] {Wobble};
        
    }

    // be sure to init all servos in initAutonomous()
    public void initAutonomous(){
        setDriveMotorMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        for(Servo servo : Servos){
            servo.setPosition(0);
        }
        waiter(500);
    }

    /*=======================================
     *
     * =============Do not edit===============
     *
     * =======================================*/

    public void setDriveMotorMode(DcMotor.RunMode mode) {
        for(DcMotor dcMotor : Drive_Motors){
            dcMotor.setMode(mode);
        }
    }

    //methods to set the four drive wheels in specific directions
    public void setMotorEncoderForward(int distance){
        frontLeft.setTargetPosition(distance);
        frontRight.setTargetPosition(distance);
        backLeft.setTargetPosition(distance);
        backRight.setTargetPosition(distance);
    }
    public void setMotorEncoderBackward(int distance) {
        frontLeft.setTargetPosition(-distance);
        frontRight.setTargetPosition(-distance);
        backLeft.setTargetPosition(-distance);
        backRight.setTargetPosition(-distance);
    }
    public void setMotorEncoderLeft(int distance) {
        frontLeft.setTargetPosition(-distance);
        frontRight.setTargetPosition(distance);
        backLeft.setTargetPosition(distance);
        backRight.setTargetPosition(-distance);
    }
    public void setMotorEncoderRight(int distance) {
        frontLeft.setTargetPosition(distance);
        frontRight.setTargetPosition(-distance);
        backLeft.setTargetPosition(-distance);
        backRight.setTargetPosition(distance);
    }
    public void setMotorEncoderClockwise(int distance) {
        frontLeft.setTargetPosition(distance);
        frontRight.setTargetPosition(-distance);
        backLeft.setTargetPosition(distance);
        backRight.setTargetPosition(-distance);
    }
    public void setMotorEncoderCounterwise(int distance) {
        frontLeft.setTargetPosition(-distance);
        frontRight.setTargetPosition(distance);
        backLeft.setTargetPosition(-distance);
        backRight.setTargetPosition(distance);
    }

    public void setToForward(double power) {
        frontLeft.setPower(power);
        frontRight.setPower(power);
        backLeft.setPower(power);
        backRight.setPower(power);
    }
    public void setToBackward(double power) {
        frontLeft.setPower(-1 * power);
        frontRight.setPower(-1 * power);
        backLeft.setPower(-1 * power);
        backRight.setPower(-1 * power);
    }
    public void setToCounterwise(double power) {
        frontLeft.setPower(-1 * power);
        frontRight.setPower(1 * power);
        backLeft.setPower(-1 * power);
        backRight.setPower(1 * power);
    }
    public void setToClockwise(double power) {
        frontLeft.setPower(1 * power);
        frontRight.setPower(-1 * power);
        backLeft.setPower(1 * power);
        backRight.setPower(-1 * power);
    }
    public void setToRight(double power) {
        frontLeft.setPower(1 * power);
        frontRight.setPower(-1 * power);
        backLeft.setPower(-1 * power);
        backRight.setPower(1 * power);
    }
    public void setToLeft(double power) {
        frontLeft.setPower(-1 * power);
        frontRight.setPower(1 * power);
        backLeft.setPower(1 * power);
        backRight.setPower(-1 * power);
    }
    public void setToStill() {
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }

    public void driveForwardEncoder(double power, int distance) {

        int frontLDist, frontRDist, backLDist, backRDist;
        setMotorEncoderForward(distance*ticksPerInch+frontLeft.getCurrentPosition());
        setDriveMotorMode(DcMotor.RunMode.RUN_TO_POSITION);

        setToForward(power);
        do{
            frontLDist=Math.abs(frontLeft.getTargetPosition()-frontLeft.getCurrentPosition());
            frontRDist=Math.abs(frontRight.getTargetPosition()-frontRight.getCurrentPosition());
            backLDist=Math.abs(backLeft.getTargetPosition()-backLeft.getCurrentPosition());
            backRDist=Math.abs(backRight.getTargetPosition()-backRight.getCurrentPosition());

            telemetry.addData("frontLeft distanceFrom: ", frontLeft.getCurrentPosition());
            telemetry.addData("frontRight distanceFrom: ",frontRight.getCurrentPosition());
            telemetry.addData("backLeft distanceFrom: ",backLeft.getCurrentPosition());
            telemetry.addData("backRight distanceFrom: ",backRight.getCurrentPosition());
            telemetry.update();
        }while(
                frontLDist>encoderSafeZone &&
                        frontRDist>encoderSafeZone &&
                        backLDist>encoderSafeZone &&
                        backRDist>encoderSafeZone
        );
        setToStill();
    }
    public void driveBackwardEncoder(double power, int distance) {
        int frontLDist, frontRDist, backLDist, backRDist;
        setMotorEncoderBackward(distance*ticksPerInch+frontLeft.getCurrentPosition());
        setDriveMotorMode(DcMotor.RunMode.RUN_TO_POSITION);

        setToForward(power);
        do{
            frontLDist=Math.abs(frontLeft.getTargetPosition()-frontLeft.getCurrentPosition());
            frontRDist=Math.abs(frontRight.getTargetPosition()-frontRight.getCurrentPosition());
            backLDist=Math.abs(backLeft.getTargetPosition()-backLeft.getCurrentPosition());
            backRDist=Math.abs(backRight.getTargetPosition()-backRight.getCurrentPosition());

            telemetry.addData("frontLeft distanceFrom: ",frontLDist);
            telemetry.addData("frontRight distanceFrom: ",frontRDist);
            telemetry.addData("backLeft distanceFrom: ",backLDist);
            telemetry.addData("backRight distanceFrom: ",backRDist);
            telemetry.update();
        }while(
                frontLDist>encoderSafeZone &&
                        frontRDist>encoderSafeZone &&
                        backLDist>encoderSafeZone &&
                        backRDist>encoderSafeZone
        );
        setToStill();
    }
    public void driveLeftEncoder(double power, int distance) {

        int frontLDist, frontRDist, backLDist, backRDist;
        setMotorEncoderLeft(distance*ticksPerInch+frontLeft.getCurrentPosition());
        setDriveMotorMode(DcMotor.RunMode.RUN_TO_POSITION);

        setToForward(power);
        do{
            frontLDist=Math.abs(frontLeft.getTargetPosition()-frontLeft.getCurrentPosition());
            frontRDist=Math.abs(frontRight.getTargetPosition()-frontRight.getCurrentPosition());
            backLDist=Math.abs(backLeft.getTargetPosition()-backLeft.getCurrentPosition());
            backRDist=Math.abs(backRight.getTargetPosition()-backRight.getCurrentPosition());

            telemetry.addData("frontLeft distanceFrom: ",frontLDist);
            telemetry.addData("frontRight distanceFrom: ",frontRDist);
            telemetry.addData("backLeft distanceFrom: ",backLDist);
            telemetry.addData("backRight distanceFrom: ",backRDist);
            telemetry.update();
        }while(
                frontLDist>encoderSafeZone &&
                        frontRDist>encoderSafeZone &&
                        backLDist>encoderSafeZone &&
                        backRDist>encoderSafeZone
        );
        setToStill();
    }
    public void driveRightEncoder(double power, int distance) {
        int frontLDist, frontRDist, backLDist, backRDist;
        setMotorEncoderRight(distance*ticksPerInch+frontLeft.getCurrentPosition());
        setDriveMotorMode(DcMotor.RunMode.RUN_TO_POSITION);

        setToForward(power);
        do{
            frontLDist=Math.abs(frontLeft.getTargetPosition()-frontLeft.getCurrentPosition());
            frontRDist=Math.abs(frontRight.getTargetPosition()-frontRight.getCurrentPosition());
            backLDist=Math.abs(backLeft.getTargetPosition()-backLeft.getCurrentPosition());
            backRDist=Math.abs(backRight.getTargetPosition()-backRight.getCurrentPosition());

            telemetry.addData("frontLeft distanceFrom: ",frontLDist);
            telemetry.addData("frontRight distanceFrom: ",frontRDist);
            telemetry.addData("backLeft distanceFrom: ",backLDist);
            telemetry.addData("backRight distanceFrom: ",backRDist);
            telemetry.update();
        }while(
                frontLDist>encoderSafeZone &&
                        frontRDist>encoderSafeZone &&
                        backLDist>encoderSafeZone &&
                        backRDist>encoderSafeZone
        );
        setToStill();
    }
    public void turnClockwiseEncoder(double power, int distance) {
        int frontLDist, frontRDist, backLDist, backRDist;
        setMotorEncoderClockwise(distance*ticksPerInch+frontLeft.getCurrentPosition());
        setDriveMotorMode(DcMotor.RunMode.RUN_TO_POSITION);

        setToForward(power);
        do{
            frontLDist=Math.abs(frontLeft.getTargetPosition()-frontLeft.getCurrentPosition());
            frontRDist=Math.abs(frontRight.getTargetPosition()-frontRight.getCurrentPosition());
            backLDist=Math.abs(backLeft.getTargetPosition()-backLeft.getCurrentPosition());
            backRDist=Math.abs(backRight.getTargetPosition()-backRight.getCurrentPosition());

            telemetry.addData("frontLeft distanceFrom: ",frontLDist);
            telemetry.addData("frontRight distanceFrom: ",frontRDist);
            telemetry.addData("backLeft distanceFrom: ",backLDist);
            telemetry.addData("backRight distanceFrom: ",backRDist);
            telemetry.update();
        }while(
                frontLDist>encoderSafeZone &&
                        frontRDist>encoderSafeZone &&
                        backLDist>encoderSafeZone &&
                        backRDist>encoderSafeZone
        );
        setToStill();
    }
    public void turnCounterwiseEncoder(double power, int distance) {
        int frontLDist, frontRDist, backLDist, backRDist;
        setMotorEncoderCounterwise(distance * ticksPerInch + frontLeft.getCurrentPosition());
        setDriveMotorMode(DcMotor.RunMode.RUN_TO_POSITION);

        setToForward(power);
        do {
            frontLDist = Math.abs(frontLeft.getTargetPosition() - frontLeft.getCurrentPosition());
            frontRDist = Math.abs(frontRight.getTargetPosition() - frontRight.getCurrentPosition());
            backLDist = Math.abs(backLeft.getTargetPosition() - backLeft.getCurrentPosition());
            backRDist = Math.abs(backRight.getTargetPosition() - backRight.getCurrentPosition());

            telemetry.addData("frontLeft distanceFrom: ", frontLDist);
            telemetry.addData("frontRight distanceFrom: ", frontRDist);
            telemetry.addData("backLeft distanceFrom: ", backLDist);
            telemetry.addData("backRight distanceFrom: ", backRDist);
            telemetry.update();
        } while (
                frontLDist > encoderSafeZone &&
                        frontRDist > encoderSafeZone &&
                        backLDist > encoderSafeZone &&
                        backRDist > encoderSafeZone
        );
        setToStill();
    }

    public void setDriveMotorZeroPowerBehavior(DcMotor.ZeroPowerBehavior behavior) {
        for(DcMotor dcMotor : Drive_Motors){
            dcMotor.setZeroPowerBehavior(behavior);
        }
    }

    //for use with driveEncoder methods
    public void setDriveMotorPower(double power){
        frontLeft.setPower(power);
        frontRight.setPower(power);
        backLeft.setPower(power);
        backRight.setPower(power);
    }

    //to use with servos or to wait
    public void waiter(int time) {
        Timer.reset();
        while (Timer.milliseconds() < time) {
        }
    }
    /*=======================================
     *
     *============End do not edit============
     *
     *=======================================*/

    /*
     * __________________
     * |                 |
     * | _______________ |
     * | |      92     | |
     * | |_____________| |
     * | |_____________| |
     * | |             | |
     * | |      67     | |
     * | |             | |
     * | |_____________| |
     * | |_____________| |
     * | |             | |
     * | |      42     | |
     * | |             | |
     * | |_____________| |
     * |                 |
     * |                 |
     * |_________________|
     *
     * ______
     * | 1  |
     * |____|______
     *       | 2  |
     * ______|____|
     * | 3  |
     * |____|
     *
     */
        
}
