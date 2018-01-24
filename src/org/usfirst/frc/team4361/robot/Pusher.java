package org.usfirst.frc.team4361.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Timer;

public class Pusher {

	WPI_TalonSRX CAN;
	DigitalInput limit;
	boolean start, delay;
	
	Timer timer, delayTimer;
	
	public Pusher(WPI_TalonSRX CAN, DigitalInput limit)
	{
		this.CAN = CAN;
		this.limit = limit;
		
		start = false;
		delay = true;
		
		timer = new Timer();
		delayTimer = new Timer();
	}
	
	public boolean move(double speed, double time)
	{
		if(delay)
		{
			delayTimer.start();

			delay = false;
		}
		if(delayTimer.get() > .5 && delay == false)
		{
			delayTimer.stop();
			delayTimer.reset();
			
			start = true;
		}
		
		if(start)
		{
			CAN.set(speed);
			
			start = false;
			
			timer.reset();
			timer.start();
		}
		
		if(timer.get() > time && !start)
		{
			CAN.set(0);
			
			start = false;
			delay = true;
			
			timer.stop();
			timer.reset();
			
			return true;
		}
		
		return false;
	}
	
	
	public void stop()
	{
		CAN.set(0);
	}
	
	
	public void startPosition()
	{
		if(limit.get())
		{
			CAN.set(.2);
		}
		else
		{
			CAN.set(0);
		}
	}
	
}
