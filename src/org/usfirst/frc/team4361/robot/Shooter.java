package org.usfirst.frc.team4361.robot;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.Timer;

public class Shooter {

	CANTalon Shooter;
	CANTalon Indexer;
	CANTalon Agitator;
	
	public Shooter(CANTalon Shooter, CANTalon Indexer, CANTalon Agitator)
	{
		this.Shooter = Shooter;
		this.Indexer = Indexer;
		this.Agitator = Agitator;
	}
	
	Boolean Start = true;
	Timer time = new Timer();
	public void Shoot(boolean Button)
	{
		if(Button && time.get() >= 10)
		{
			Indexer.set(.3);
			time.stop();
			time.reset();
		}
		else if(Button && Start)
		{
			Agitator.set(-.5);
			Shooter.set(-.65);
			time.reset();
			time.start();
			Start = false;
		}
		else if(!Button)
		{
			Indexer.set(0);
			Shooter.set(0);
			Agitator.set(0);
			
			Start = true;
		}
	}
}
