package org.usfirst.frc.team4361.robot;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class Drive {
	
	WPI_TalonSRX[] CAN;
	
	public Drive(WPI_TalonSRX[] CAN)
	{
		this.CAN = CAN;
	}
	
	public void drive(double val)
	{
		for (WPI_TalonSRX tal : CAN)
		{
			tal.set(val);
		}
	}
}