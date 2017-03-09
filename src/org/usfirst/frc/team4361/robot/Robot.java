package org.usfirst.frc.team4361.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Joystick;
import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.CvSink;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Relay;


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	
	DigitalInput[] limit;
	
	Relay[] relay;
	
	CANTalon[] CAN;
	Joystick[] stick;
	
	Encoder[] enc;
	
	
	double stick0Y, stick1Y, stick1X, leftInput, rightInput;
	
	Drive Left, Right, Climber, Intake, Agitator;
	Shooter Shoot;
	
	boolean blueSide, gearing, gearChanged, agitatorChange;
	
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
		limit = new DigitalInput[0];
		for(int i = 0; i < limit.length; i++)
		{
			limit[i] = new DigitalInput(i);
		}
		
		relay = new Relay[0];
		for(int i = 0; i < relay.length; i++)
		{
			relay[i] = new Relay(i);
		}
		
		agitatorChange = false;
		
		gearing = false;
		gearChanged = false;
		
		CAN = new CANTalon[9];
		for (int i = 0; i < CAN.length; i++)
		{
			CAN[i] = new CANTalon(i);
		}
		
		CANTalon[] left = {CAN[0], CAN[3]};
		Left = new Drive(left);
	
		CANTalon[] right = {CAN[7], CAN[8]};
		Right = new Drive(right);
		
		CANTalon[] intake = {CAN[6]};
		Intake = new Drive(intake);
		
		Shoot = new Shooter(CAN[1], CAN[2]);
		 
		CANTalon[] climber = {CAN[5]};
		Climber = new Drive(climber);
		
		stick = new Joystick[3];
		for (int i = 0; i < stick.length; i++)
		{
			stick[i] = new Joystick(i);
		}
		
		enc = new Encoder[2];
		enc[0] = new Encoder(1,2, false);
		enc[1] = new Encoder(3,4, false);
		
		CameraSetup();
		
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
		
		if(stick[2].getRawButton(1) && agitatorChange)
		{
			if(CAN[4].get() < 0)
				CAN[4].set(0);
			else
				CAN[4].set(-.85);
			agitatorChange = false;
		}
		else if(!stick[2].getRawButton(1) && !agitatorChange)
		{
			agitatorChange = true;
		}
		
		boolean climber1, climber2;
		boolean perfectStraight, perfectTurn;
		
		//Inputs from joystick and Main Xbox Controller
		if(stick[0].getIsXbox())
		{
			//Gearing Input
			if(stick[0].getRawButton(1) && !gearChanged)
			{
				gearing = !gearing;
				gearChanged = true;
			}
			if(!stick[0].getRawButton(1) && gearChanged)
			{
				gearChanged = false;
			}
			
			
			perfectStraight = stick[0].getRawAxis(3) != 0;
			perfectTurn = stick[0].getRawAxis(2) != 0;
			

			stick0Y = stick[0].getRawAxis(1);
			stick1Y = stick[0].getRawAxis(5);
			stick1X = stick[0].getRawAxis(4);
			
			
			//Climbing
			climber1 = stick[0].getRawButton(5);
			climber2 = stick[0].getRawButton(6);
			
			if(stick[0].getRawButton(2))
			{
				stick[0].setRumble(RumbleType.kLeftRumble, 1);
				stick[0].setRumble(RumbleType.kRightRumble, 1);
			}
			else
			{
				stick[0].setRumble(RumbleType.kLeftRumble, 0);
				stick[0].setRumble(RumbleType.kRightRumble, 0);
			}
		}
		else
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
			
			
			perfectStraight = stick[1].getRawButton(3);
			perfectTurn = stick[1].getRawButton(4);
			

			stick0Y = stick[0].getY();
			stick1Y = stick[1].getY();
			stick1X = stick[1].getX();
			
			
			//Climbing
			climber1 = stick[0].getRawButton(3);
			climber2 = stick[0].getRawButton(4);
		}
		
		
		
		if(gearing)
		{
			leftInput = -stick0Y/2;
			rightInput = stick1Y/2;
			
			stick1X = stick1X/2;
		}
		else
		{
			leftInput = -stick0Y;
			rightInput = stick1Y;
		}
		
		if(perfectStraight)
		{
			leftInput = -rightInput;
		}
		else if(perfectTurn)
		{
			leftInput = stick1X;
			rightInput = stick1X;
		}
		
		Left.drive(leftInput);
		Right.drive(rightInput);
		
		
		//Climber
		if(climber1)
			Climber.drive(.2);
		else if(climber2)
			Climber.drive(1);
		else
			Climber.drive(0);
		
		if(stick[2].getIsXbox())
		{
			//Auto Fixer
			if(stick[2].getRawButton(2))
			{
				Intake.drive(-.3);
				CAN[4].set(.75);
				agitatorChange = true;
				Shoot.Fix(stick[2].getRawButton(2));
				
			}
			else
			{
				//Intake
				if(stick[2].getRawButton(5))
					Intake.drive(.6);
				else
					Intake.drive(0);
					
	
				//Shooter
				Shoot.Shoot(stick[2].getRawButton(6));
			}
		}
		
		//Smartdashboard Values

		//SmartDashboard.putNumber("Agitator Current", CAN[4].getOutputCurrent());
		SmartDashboard.putNumber("Shooter Current", CAN[1].getOutputCurrent());
		SmartDashboard.putNumber("Climber Current", CAN[5].getOutputCurrent());
		SmartDashboard.putBoolean("Gear", gearing);
		
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
	}
	
	public void CameraSetup()
	{
		CameraServer.getInstance().startAutomaticCapture("cam0", 0);
		CameraServer.getInstance().startAutomaticCapture("cam1", 1);
		
		/*
		CameraServer.getInstance().startAutomaticCapture(0);

		new Thread(() -> {
            UsbCamera camera0 = CameraServer.getInstance().startAutomaticCapture(0);

            UsbCamera camera1 = CameraServer.getInstance().startAutomaticCapture(1);
            
            
            
            
            camera0.setResolution(160, 120);
            
            CvSink cvSink = CameraServer.getInstance().getVideo("cam0");
            CvSource outputStream = CameraServer.getInstance().putVideo("Blur0", 160, 120);
            
            Mat source = new Mat();
            Mat output = new Mat();
            
            camera1.setResolution(160, 120);
            
            CvSink cvSink1 = CameraServer.getInstance().getVideo("cam1");
            CvSource outputStream1 = CameraServer.getInstance().putVideo("Blur1", 160, 120);
            
            Mat source1 = new Mat();
            Mat output1 = new Mat();
            
            while(!Thread.interrupted()) {
                cvSink.grabFrame(source);
                //Imgproc.cvtColor(source, output, Imgproc.COLOR_BGR2GRAY);
                outputStream.putFrame(output);
                

                //cvSink1.grabFrame(source1);
                //Imgproc.cvtColor(source1, output1, Imgproc.COLOR_BGR2GRAY);
                //outputStream1.putFrame(output1);
            }
        });//.start();
		*/
	}
	
}

