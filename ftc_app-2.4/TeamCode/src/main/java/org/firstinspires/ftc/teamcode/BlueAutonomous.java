/*
Copyright (c) 2016 Robert Atkinson

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Robert Atkinson nor the names of his contributors may be used to
endorse or promote products derived from this software without specific prior
written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESSFOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.firstinspires.ftc.teamcode;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * This file contains an minimal example of a Linear "OpMode". An OpMode is a 'program' that runs in either
 * the autonomous or the teleop period of an FTC match. The names of OpModes appear on the menu
 * of the FTC Driver Station. When an selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 *
 * This particular OpMode just executes a basic Tank Drive Teleop for a PushBot
 * It includes all the skeletal structure that all linear OpModes contain.
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@Autonomous(name="Blue Autonomous", group="Autonomous")
//@Disabled
public class BlueAutonomous extends LinearOpMode implements SensorEventListener {

    /* Declare OpMode members. */
    private ElapsedTime runtime = new ElapsedTime();

    // sensor stuff
    SensorManager sensorManager;
    Sensor sensor;
    SensorEvent rawData;
    ColorSensor colorSensor;

    double average;
    double tempAverage;

    double timer;
    double timer2;
    double rotations;

    // motors
    DcMotor leftMotor = null;
    DcMotor rightMotor = null;
    DcMotor launcherMotor = null;
    DcMotor motorHopper = null;

    Servo servo;

    // data
    int step;
    boolean turning;
    double leftMultiplier;
    double rightMultiplier;

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        sensorManager = (SensorManager) hardwareMap.appContext.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);

        leftMotor  = hardwareMap.dcMotor.get("lMotor");
        rightMotor = hardwareMap.dcMotor.get("rMotor");
        leftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        launcherMotor = hardwareMap.dcMotor.get("launcher");
        launcherMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        motorHopper = hardwareMap.dcMotor.get("hMotor");

        servo = hardwareMap.servo.get("servo");
        servo.setPosition(0.0);

        colorSensor = hardwareMap.colorSensor.get("cSensor");

        step = 0;
        leftMultiplier = 1.08;
        rightMultiplier = 0.9;

        // eg: Set the drive motor directions:
        // "Reverse" the motor that runs backwards when connected directly to the battery
        // leftMotor.setDirection(DcMotor.Direction.REVERSE); // Set to REVERSE if using AndyMark motors
         rightMotor.setDirection(DcMotor.Direction.REVERSE);// Set to FORWARD if using AndyMark motors

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Raw", rawData.values[2]);
            telemetry.addData("Rotations", rotations);
            telemetry.addData("Turning", turning);
            telemetry.addData("Blue", colorSensor.blue());

            telemetry.update();
            if(busy()) {
                telemetry.addData("busy", null);
            }
            else {

                if (step == 0) {
                    goPosition(leftMotor, rightMotor, 0.6293);
                    step++;

                }
                else if(step == 1) {
                    turn(leftMotor, rightMotor, false, 70);
                    if (!turning) {
                        rotations = 0;
                        step++;
                    }
                }
                else if(step == 2) {
                    goPosition(leftMotor, rightMotor, 1);
                    step++;
                }
                else if(step == 3) {
                    turn(leftMotor, rightMotor, true, 45);
                    if(!turning) {
                        rotations = 0;
                        step++;
                    }
                }
                else if(step == 4) {
                    goPosition(leftMotor, rightMotor, 0.08);
                    step++;
                }
                else if(step == 5) {
                    turn(leftMotor, rightMotor, true, 18);
                    if(!turning) {
                        rotations = 0;
                        step++;
                    }
                }
                else if(step == 6){
//                    goPosition(leftMotor, rightMotor, 0.1, 0.2);
//                    step++;
                    leftMotor.setPower(0.3);
                    rightMotor.setPower(0.3);
                    if(colorSensor.blue()>6) step++;
                }
                else if(step ==7){
                    leftMotor.setPower(0.0);
                    rightMotor.setPower(0.0);
                    step++;
                }
                else if(step == 8) {

                }

            }



            idle(); // Always call idle() at the bottom of your while(opModeIsActive()) loop
        }
    }

    public void goPosition(DcMotor motor1, DcMotor motor2, double distance) {
        //resets encoders
        motor1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        //sets position
        motor1.setTargetPosition((int)((motor1.getCurrentPosition()+(distance*5100))*leftMultiplier));
        motor2.setTargetPosition((int)((motor2.getCurrentPosition()+(distance*5100))*rightMultiplier));

        // run to position
        motor1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor2.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor1.setPower(0.6*leftMultiplier);
        motor2.setPower(0.6*rightMultiplier);

    }

    public void goPosition(DcMotor motor1, DcMotor motor2, double distance, double power) {
        //resets encoders
        motor1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        //sets position
        motor1.setTargetPosition((int)((motor1.getCurrentPosition()+(distance*5100))*leftMultiplier));
        motor2.setTargetPosition((int)((motor2.getCurrentPosition()+(distance*5100))*rightMultiplier));

        // run to position
        motor1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor2.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motor1.setPower(power*leftMultiplier);
        motor2.setPower(power*rightMultiplier);

    }

    public void turn(DcMotor motor1, DcMotor motor2, boolean left, int degrees) {
        motor1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motor2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        if(runtime.milliseconds()-timer >=10) {
            rotations += (Math.abs(rawData.values[2])+0.015)/100*57.2958;
            timer = runtime.milliseconds();
        }
        turning = rotations < degrees;
        if(turning) {
            if(left){
                motor1.setPower(-0.35);
                motor2.setPower(0.25);
            }
            else {
                motor1.setPower(0.35);
                motor2.setPower(-0.25);
            }

        }
        else {
            motor1.setPower(0.0);
            motor2.setPower(0.0);
        }
    }

    public void shoot(int num) {
        for(int i = 0; i < num; i++) {



//            launcherMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
//            launcherMotor.setTargetPosition(launcherMotor.getCurrentPosition()-1120);
//            launcherMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
//            launcherMotor.setPower(0.5);
        }
    }

    public boolean color(int color) {
        return colorSensor.red() > 5;
    }

    public boolean busy() {
        if(launcherMotor.isBusy()) {
            return true;
        }
        else if(leftMotor.getMode().equals(DcMotor.RunMode.RUN_TO_POSITION)&&rightMotor.getMode().equals(DcMotor.RunMode.RUN_TO_POSITION)) {
            return leftMotor.isBusy() && rightMotor.isBusy();
        }
        else {
            return false;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent e) {
        this.rawData = e;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


}