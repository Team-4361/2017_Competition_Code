package org.usfirst.frc.team4361.robot;

import edu.wpi.first.wpilibj.*;

public class GearHolder {
	

	private Servo[] Doors;
	private Pusher push;
	
	private boolean gearChangingState, hasPushed;
	public boolean gearPosition;
	private Timer resetTimer;
	
	public GearHolder(Servo[] Doors, Pusher push)
	{
		this.Doors = Doors;
		this.push = push;

		gearPosition = false;
		gearChangingState = true;

		hasPushed = false;
		
		resetTimer = new Timer();
	}
	
	public void ChangePosition(boolean input)
	{
		if(input && gearChangingState)
		{
			gearPosition = !gearPosition;
			
			gearChangingState = false;
			
			//push.stop();
		}
		
		if(!input && !gearChangingState)
		{
			gearChangingState = true;
		}
		
		//Automatic reset for Gear Holder
		if(gearPosition && resetTimer.get() == 0)
		{
			resetTimer.start();
		}
		
		if(gearPosition && resetTimer.get() > 7)
		{
			gearPosition = false;
			
			resetTimer.stop();
			resetTimer.reset();
		}
		
		if(!input && !gearPosition)
		{
			resetTimer.stop();
			resetTimer.reset();
		}
		
		
		//Gear pusher and Gear door Controls
		if(!gearPosition)
		{
			//Doors Close
			Doors[0].set(.85);
			Doors[1].set(.1);
			
			//Pusher pushes
			push.startPosition();

			hasPushed = false;
		}
		
		if(gearPosition)
		{
			//Doors Open
			Doors[0].set(.35);
			Doors[1].set(.24);
			
			
			if(!hasPushed)
			{
				//Pusher pushes
				if(push.move(-.55, .1))
				{
					hasPushed = true;
				}
			}
			
		}
	}

}
