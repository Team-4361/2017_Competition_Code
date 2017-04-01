package org.usfirst.frc.team4361.robot;

import edu.wpi.first.wpilibj.Servo;

public class GearHolder {
	

	private Servo[] Doors;
	private Pusher push;
	
	private boolean gearChangingState, hasPushed;
	public boolean gearPosition;
	
	public GearHolder(Servo[] Doors, Pusher push)
	{
		this.Doors = Doors;
		this.push = push;

		gearPosition = false;
		gearChangingState = true;

		hasPushed = false;
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
		
		
		//Gear pusher and Gear door Controls
		if(!gearPosition)
		{
			//Doors Close
			Doors[0].set(.9);
			Doors[1].set(.05);
			
			//Pusher pushes
			push.startPosition();

			hasPushed = false;
		}
		
		if(gearPosition)
		{
			//Doors Open
			Doors[0].set(.7);
			Doors[1].set(.24);
			
			
			if(!hasPushed)
			{
				//Pusher pushes
				if(push.move(-.6, .2))
				{
					System.out.println("Has Pushed");
					hasPushed = true;
				}
			}
			
		}
	}

}
