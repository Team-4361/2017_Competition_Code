package org.usfirst.frc.team4361.robot;
import com.ctre.CANTalon;

public class Drive {
	
	CANTalon[] CAN;
	
	public Drive(CANTalon[] CAN)
	{
		this.CAN = CAN;
	}
	
	public void drive(double val)
	{
		for (CANTalon tal : CAN)
		{
			tal.set(val);
		}
	}
}
