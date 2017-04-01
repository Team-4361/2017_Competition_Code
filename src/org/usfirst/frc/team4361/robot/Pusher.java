package org.usfirst.frc.team4361.robot;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Timer;

public class Pusher {

	CANTalon CAN;
	DigitalInput limit;
	boolean start, delay;
	
	Timer timer, delayTimer;
	
	public Pusher(CANTalon CAN, DigitalInput limit)
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
		if(delayTimer.get() > .1 && delay == false)
		{
			System.out.println("Delay End");
			
			delayTimer.stop();
			delayTimer.reset();
			
			start = true;
		}
		
		if(start)
		{
			System.out.println("Start Push");
			
			CAN.set(speed);
			
			start = false;
			
			timer.reset();
			timer.start();
		}
		
		if(timer.get() > time && !start)
		{
			System.out.println("Stop Push");
			
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
			System.out.println("Back");
			CAN.set(.2);
		}
		else
		{
			CAN.set(0);
		}
	}
	
}
