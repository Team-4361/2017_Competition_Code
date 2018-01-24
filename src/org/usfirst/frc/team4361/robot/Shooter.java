package org.usfirst.frc.team4361.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.Timer;

public class Shooter {

	WPI_TalonSRX Shooter;
	WPI_TalonSRX Indexer;
	
	public Shooter(WPI_TalonSRX Shooter, WPI_TalonSRX Indexer)
	{
		this.Shooter = Shooter;
		this.Indexer = Indexer;
	}
	
	Boolean Start = true;
	Timer time = new Timer();
	public void Shoot(boolean Button)
	{
		if(Button && time.hasPeriodPassed(1))
		{
			Indexer.set(.3);
			time.stop();
		}
		else if(Button && Start)
		{
			Shooter.set(-.75);
			time.reset();
			time.start();
			Start = false;
		}
		else if(!Button)
		{
			Indexer.set(0);
			Shooter.set(0);

			time.stop();
			time.reset();
			
			Start = true;
		}
	}
	
	public void Fix(boolean Button)
	{
		if(Button)
		{
			Indexer.set(-.5);			
		}
	}
}
