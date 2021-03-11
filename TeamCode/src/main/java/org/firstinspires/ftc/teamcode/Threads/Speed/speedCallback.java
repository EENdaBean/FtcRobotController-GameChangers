package org.firstinspires.ftc.teamcode.Threads.Speed;

public interface speedCallback{
	int speed();
	boolean fire();
	void can_Fire(boolean can, int speed_Flywheel);
	boolean running();
}
