package org.usfirst.frc.team4361.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.TalonSRX;
import edu.wpi.first.wpilibj.Joystick;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	
	TalonSRX[] CAN;
	Joystick[] stick;
	
	double stick0Y, stick1Y;
	
	Drive Left, Right, Shooter, Climber, Intake, Agitator;
	
	final String defaultAuto = "Default";
	final String customAuto = "My Auto";
	
	String autoSelected;
	SendableChooser<String> chooser = new SendableChooser<>();

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		
		CAN = new TalonSRX[8];
		for (int i = 0; i < CAN.length; i++)
		{
			CAN[i] = new TalonSRX(i);
		}
		
		TalonSRX[] left = {CAN[0], CAN[1]};
		Left = new Drive(left);

		TalonSRX[] right = {CAN[2], CAN[3]};
		Right = new Drive(right);
		
		TalonSRX[] intake = {CAN[4]};
		Intake = new Drive(intake);
		
		TalonSRX[] shooter = {CAN[5]};
		Shooter = new Drive(shooter);
		 
		 TalonSRX[] climber = {CAN[6]};
		 Climber = new Drive(climber);
		 
		 TalonSRX[] agitator = {CAN[7]};
		 Agitator = new Drive(agitator);
		 
		 stick = new Joystick[3];
		 for (int i = 0; i < stick.length; i++)
		 {
			 stick[i] = new Joystick(i);
		 }
		
		chooser.addDefault("Default Auto", defaultAuto);
		chooser.addObject("My Auto", customAuto);
		SmartDashboard.putData("Auto choices", chooser);
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the
	 * switch structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	@Override
	public void autonomousInit() {
		autoSelected = chooser.getSelected();
		// autoSelected = SmartDashboard.getString("Auto Selector",
		// defaultAuto);
		System.out.println("Auto selected: " + autoSelected);
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		switch (autoSelected) {
		case customAuto:
			// Put custom auto code here
			break;
		case defaultAuto:
		default:
			// Put default auto code here
			break;
		}
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		stick0Y = stick[0].getY();
		stick1Y = stick[1].getY();

		Left.drive(stick0Y);
		Right.drive(stick1Y);
		
		if(stick[2].getIsXbox())
		{
			if(stick[2].getRawButton(0))
			{
				Shooter.drive(-.55);
				Agitator.drive(1);
			}
			
			if(stick[2].getRawButton(1))
				Climber.drive(1);
			
			if(stick[2].getRawButton(2))
				Intake.drive(-1);
		}
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
	}
}

