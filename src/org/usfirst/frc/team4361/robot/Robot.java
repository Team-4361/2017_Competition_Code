package org.usfirst.frc.team4361.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Joystick;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.XboxController;


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
	
	WPI_TalonSRX[] CAN;
	Joystick[] stick;
	XboxController XContr;
	
	Encoder[] enc;

	Servo[] Doors;
	
	double stick0Y, stick0X, stick1Y, stick1X, leftInput, rightInput;
	
	Drive Left, Right, Climber, Intake, Agitator;
	Shooter Shoot;
	
	boolean blueSide, gearing, gearChanged, agitatorChange, gearPosition, gearChangingState, hasPushed, XboxMode;
	
	Autonomous auto;
	
	Pusher push;
	
	GearHolder holder;
	
	String autoSelected;
	SendableChooser<String> chooser = new SendableChooser<>();

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() 
	{
		limit = new DigitalInput[1];
		limit[0] = new DigitalInput(9);
		
		relay = new Relay[0];
		for(int i = 0; i < relay.length; i++)
		{
			relay[i] = new Relay(i);
		}
		
		Doors = new Servo[2];
		for(int i = 0; i < Doors.length; i++)
		{
			Doors[i] = new Servo(i);
		}
		
		agitatorChange = false;
		
		gearing = false;
		gearChanged = false;
		
		
		
		CAN = new WPI_TalonSRX[10];
		for (int i = 0; i < CAN.length; i++)
		{
			CAN[i] = new WPI_TalonSRX(i);
		}
		
		WPI_TalonSRX[] left = {CAN[0], CAN[3]};
		Left = new Drive(left);
	
		WPI_TalonSRX[] right = {CAN[7], CAN[8]};
		Right = new Drive(right);
		
		WPI_TalonSRX[] intake = {CAN[6]};
		Intake = new Drive(intake);
		
		Shoot = new Shooter(CAN[1], CAN[2]);
		 
		WPI_TalonSRX[] climber = {CAN[5]};
		Climber = new Drive(climber);
		
		stick = new Joystick[2];
		for (int i = 0; i < stick.length; i++)
		{
			stick[i] = new Joystick(i);
		}
		
		XContr = new XboxController(2);
		
		XboxMode = false;
		
		push = new Pusher(CAN[9], limit[0]);
		
		holder = new GearHolder(Doors, push);
		
		enc = new Encoder[2];
		enc[0] = new Encoder(1,2, false);
		enc[0].setDistancePerPulse(1.0/250.2);
		
		enc[1] = new Encoder(3,4, false);
		enc[1].setDistancePerPulse(1.0/250.2);
		
		CameraSetup();
		
		chooser.addDefault("Drive to Line", "line");
		chooser.addObject("Feeder Side", "feeder");
		chooser.addObject("Airship Side", "airship");
		//chooser.addObject("Airship Drop", "airshipDrop");
		chooser.addObject("Boiler Side", "boiler");
		chooser.addObject("Shoot in Boiler", "shootBoiler");
		chooser.addObject("Test", "test");
		
		//SmartDashboard Values
		SmartDashboard.putData("Auto choices", chooser);
		SmartDashboard.putBoolean("BlueSide", false);
		SmartDashboard.putBoolean("Switched", false);
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
		try
		{
			if(holder.gearPosition)
			{
				holder.ChangePosition(true);
				holder.ChangePosition(false);
			}
		}
		catch (Exception e)
		{
			
		}
		
		blueSide = SmartDashboard.getBoolean("BlueSide", false);
		
		auto = new Autonomous(Left, Right, Shoot, holder, blueSide, enc[0], enc[1]);
		
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
			
		case "airshipDrop":
			auto.AirshipDrop();
			break;
			
		case "boiler":
			auto.Boiler();
			break;
			
		case "shootBoiler":
			auto.ShootInBoiler();
			break;
			
		case "test":
			auto.Test();
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
		
		if(XContr.getStartButtonPressed())
		{
			XboxMode = !XboxMode;
		}
		
		boolean climber1, climber2;
		boolean perfectStraight = false, perfectTurn = false;
		
		//Inputs from Main Xbox Controller
		if(XboxMode)
		{
			holder.ChangePosition(XContr.getYButton());
			
			//Gearing Input
			if(XContr.getBButtonPressed())
			{
				gearing = !gearing;
			}
			

			//Climbing
			climber1 = XContr.getTriggerAxis(Hand.kRight) != 0;
			climber2 = XContr.getTriggerAxis(Hand.kLeft) != 0;
			

			stick0Y = XContr.getRawAxis(1);
			stick0X = XContr.getRawAxis(0);
			
			
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
			
			
			//Changing Gear State
			holder.ChangePosition(stick[1].getRawButton(1));
			
			
			perfectStraight = stick[1].getRawButton(3);
			perfectTurn = stick[1].getRawButton(4);
			

			stick0Y = stick[0].getY();
			stick1Y = stick[1].getY();
			stick1X = stick[1].getX();
			
			
			//Climbing
			climber1 = stick[0].getRawButton(3);
			climber2 = stick[0].getRawButton(4);
		}

		if(XboxMode)
		{
			leftInput = stick0X - stick0Y;
			rightInput = stick0X + stick0Y;
		}
		else
		{
			leftInput = -stick0Y;
			rightInput = stick1Y;
		}
		
		if(gearing)
		{
			leftInput = leftInput/2;
			rightInput = rightInput/2;
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

		
		if(SmartDashboard.getBoolean("Switched", false))
		{
			double tempSwitch = rightInput;
			rightInput = leftInput;
			leftInput = tempSwitch;
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
		
		if(XContr.getAButtonPressed())
		{
			if(CAN[4].get() < 0)
				CAN[4].set(0);
			else
				CAN[4].set(-.85);
		}
		
		//Intake
		if(XContr.getBumper(Hand.kLeft))
			Intake.drive(.8);
		else
			Intake.drive(0);
		

		//Shooter
		Shoot.Shoot(XContr.getBumper(Hand.kRight));
		
		if(XContr.getXButtonPressed())
		{
			XContr.setRumble(RumbleType.kLeftRumble, 1);
			XContr.setRumble(RumbleType.kRightRumble, 1);
		}
		else
		{
			XContr.setRumble(RumbleType.kLeftRumble, 0);
			XContr.setRumble(RumbleType.kRightRumble, 0);
		}
		
		//Smartdashboard Values

		try
		{
			SmartDashboard.putNumber("Agitator Current", CAN[4].getOutputCurrent());
			SmartDashboard.putNumber("Shooter Current", CAN[1].getOutputCurrent());
			SmartDashboard.putNumber("Climber Current", CAN[5].getOutputCurrent());
			SmartDashboard.putBoolean("Geared", gearing);
			//SmartDashboard.putBoolean("GearIn", !limit[0].get());
			//SmartDashboard.putNumber("Pusher Current", CAN[9].getOutputCurrent());
			SmartDashboard.putBoolean("Gear Position", !holder.gearPosition);
			SmartDashboard.putBoolean("Xbox Control Mode", XboxMode);
		}
		catch (Exception e)
		{
			//System.out.println("Smartdashboard: " + e.getMessage());
		}
		
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
	}
	
	public void CameraSetup()
	{
		try 
		{
			CameraServer.getInstance().startAutomaticCapture("cam0", 0);
			CameraServer.getInstance().startAutomaticCapture("cam1", 1);
		}
		catch (Exception e)
		{
			System.out.println("Camera Error: " + e.getMessage());
		}
		
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

