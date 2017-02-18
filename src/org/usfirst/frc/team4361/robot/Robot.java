package org.usfirst.frc.team4361.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Joystick;
import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.Encoder;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	
	CANTalon[] CAN;
	Joystick[] stick;
	
	Encoder[] enc;
	
	
	double stick0Y, stick1Y, stick1X, leftInput, rightInput;
	
	Drive Left, Right, Climber, Intake, Agitator;
	Shooter Shoot;
	
	boolean blueSide, gearing, gearChanged;
	
	Autonomous auto;
	
	String autoSelected;
	SendableChooser<String> chooser = new SendableChooser<>();

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() 
	{
	
		gearing = false;
		gearChanged = false;
		
		CAN = new CANTalon[9];
		for (int i = 0; i < CAN.length; i++)
		{
			CAN[i] = new CANTalon(i);
		}
		
		CANTalon[] left = {CAN[0], CAN[1]};
		Left = new Drive(left);
	
		CANTalon[] right = {CAN[2], CAN[3]};
		Right = new Drive(right);
		
		CANTalon[] intake = {CAN[4]};
		Intake = new Drive(intake);
		
		Shoot = new Shooter(CAN[5], CAN[6], CAN[7]);
		 
		CANTalon[] climber = {CAN[8]};
		Climber = new Drive(climber);
		
		stick = new Joystick[3];
		for (int i = 0; i < stick.length; i++)
		{
			stick[i] = new Joystick(i);
		}
		
		enc = new Encoder[2];
		for (int i = 0; i < enc.length; i+=2)
		{
			enc[i/2] = new Encoder(i, i + 1);
		}
		
		chooser.addDefault("Drive to Line", "line");
		chooser.addObject("Feeder Side", "feeder");
		chooser.addObject("Airship Side", "airship");
		chooser.addObject("Boiler Side", "boiler");
		chooser.addObject("Shoot in Boiler", "shootBoiler");
		
		//SmartDashboard Values
		SmartDashboard.putData("Auto choices", chooser);
		SmartDashboard.putBoolean("BlueSide", false);
		SmartDashboard.putNumber("ShooterVoltage", CAN[5].getOutputVoltage());
		SmartDashboard.putNumber("ClimberVoltage", CAN[7].getOutputVoltage());
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
		blueSide = SmartDashboard.getBoolean("BlueSide", false);
		
		auto = new Autonomous(Left, Right, Shoot, blueSide, enc[0], enc[1]);
		
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
		
		case "feeder":
			auto.Feeder();
			break;
			
		case "airship":
			auto.Airship();
			break;
			
		case "boiler":
			auto.Boiler();
			break;
			
		case "shootBoiler":
			auto.ShootInBoiler();
			break;
			
		case "line":
		default:
			auto.defaultGoToBaseLine();
			break;
		}
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() 
	{
		//Gearing Input
		if(stick[1].getRawButton(2) && !gearChanged)
		{
			gearing = !gearing;
			gearChanged = true;
		}
		if(!stick[1].getRawButton(2) && gearChanged)
		{
			gearChanged = false;
		}
		
		stick0Y = stick[0].getY();
		stick1Y = stick[1].getY();
		stick1X = stick[1].getX();
		
		if(gearing)
		{
			leftInput = stick0Y/2;
			rightInput = stick1Y/2;
			
			stick1X = stick1X/2;
		}
		else
		{
			leftInput = stick0Y;
			rightInput = stick1Y;
		}
		
		if(stick[1].getRawButton(3))
		{
			Left.drive(-rightInput);
			Right.drive(rightInput);
		}
		else if(stick[1].getRawButton(4))
		{
			Left.drive(stick1X);
			Right.drive(stick1X);
		}
		else
		{
			Left.drive(leftInput);
			Right.drive(rightInput);
		}
		
		
		//Climber
		if(stick[0].getRawButton(3))
			Climber.drive(.2);
		if(stick[0].getRawButton(4))
			Climber.drive(.75);
		
		if(stick[2].getIsXbox())
		{
			//Shooter
			Shoot.Shoot(stick[2].getRawButton(0));
			
			//Intake
			if(stick[2].getRawButton(2))
				Intake.drive(-1);
		}
		
		//Smartdashboard Values
		SmartDashboard.putNumber("ShooterVoltage", CAN[5].getOutputVoltage());
		SmartDashboard.putNumber("ClimberVoltage", CAN[7].getOutputVoltage());
		SmartDashboard.putBoolean("Gear", gearing);
		
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
	}
}

