package org.usfirst.frc.team4361.robot;
import edu.wpi.first.wpilibj.TalonSRX;

public class Drive {
	TalonSRX[] CAN;
	
	public Drive(TalonSRX[] CAN)
	{
		this.CAN = CAN;
	}
	
	public void drive(double val)
	{
		for (TalonSRX tal : CAN)
		{
			tal.set(val);
		}
	}
}
