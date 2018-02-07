package org.usfirst.frc.team4361.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.*;


public class Autonomous implements PIDOutput{

	double diameter;
	double circumference;
	double distanceNeeded, StartAngle;
	final double robotLength;
	
	boolean isEnc, hasRun;
	int runNum, lEncNum, rEncNum;
	Timer timer, timerSpeed, shotTime, GearTimer;
	
	Drive left, right;
	Shooter shoot;
	
	Encoder lEnc, rEnc;
	
	int minDist, maxDist;

	AHRS navx;
	
	TurnControl turn;
	
	boolean redSide;
	
	GearHolder holder;

	PIDController turnController;
	double rotateToAngleRate;
	WPI_TalonSRX[] CAN;
	
	/* The following PID Controller coefficients will need to be tuned */
	/* to match the dynamics of your drive system.  Note that the      */
	/* SmartDashboard in Test mode has support for helping you tune    */
	/* controllers by displaying a form where you can enter new P, I,  */
	/* and D constants and test the mechanism.                         */
	
	static final double kP = 0.05;
	static final double kI = 0.001;
	static final double kD = 0.00;
	static final double kF = 0.00;
	  
	/* This tuning parameter indicates how close to "on target" the    */
	/* PID Controller will attempt to get.  */
	
	static final double kToleranceDegrees = 0.5f;
	
	//Constructers
	public Autonomous(Drive left, Drive right, Shooter shoot, GearHolder holder, boolean redSide)
	{
		robotLength = 35.5;
		
		diameter = 6 + 1/8;
		circumference = diameter * Math.PI;
		StartAngle = 0;
		
		if(lEnc != null)
			isEnc = true;
		else
			isEnc = false;
		lEncNum = 0;
		rEncNum = 0;
		hasRun = false;
		runNum = 0;
		timer = new Timer();
		shotTime = new Timer();
		timerSpeed = new Timer();
		GearTimer = new Timer();
		
		this.left = left;
		this.right = right;
		this.shoot = shoot;
		
		try {
	          /* Communicate w/navX-MXP via the MXP SPI Bus.                                     */
	          /* Alternatively:  I2C.Port.kMXP, SerialPort.Port.kMXP or SerialPort.Port.kUSB     */
	          /* See http://navx-mxp.kauailabs.com/guidance/selecting-an-interface/ for details. */
			navx = new AHRS(SPI.Port.kMXP);
	      } catch (RuntimeException ex ) {
	          DriverStation.reportError("Error instantiating navX-MXP:  " + ex.getMessage(), true);
	      }
	      turnController = new PIDController(kP, kI, kD, kF, navx, this);
	      turnController.setInputRange(-180.0f,  180.0f);
	      turnController.setOutputRange(-1.0, 1.0);
	      turnController.setAbsoluteTolerance(kToleranceDegrees);
	      turnController.setContinuous(true);
		
		this.redSide = redSide;
	}
	public Autonomous(Drive left, Drive right, Shooter shoot, GearHolder holder, boolean redSide, Encoder lEnc, Encoder rEnc)
	{
		
		this(left, right, shoot, holder, redSide);
		
		diameter = 6 + 1/8;
		circumference = diameter * Math.PI;
		
		
		this.lEnc = lEnc;
		this.rEnc = rEnc;

		isEnc = true;
	}
	
	
	//Different Autonomous Modes
	public void defaultGoToBaseLine()
	{
		//turn = new TurnControl(navx.getAngle());
		if(runNum == 0)
			goDistance(94 - robotLength, -.4);
	}
	
	public void Feeder()
	{
		if(runNum == 0)
			goDistance(108.39373 - robotLength, -.5);
		if(runNum == 1)
			turnEncoder(-60, .3);
		if(runNum == 2)
			goDistance(46.79856 - robotLength, -.5);
	}

	public void FeederDrop()
	{
		if(runNum == 0)
			goDistance(108.39373 - robotLength, -.5);
		if(runNum == 1)
			turnEncoder(-60, .3);
		if(runNum == 2)
			goDistance(46.79856 - robotLength, -.5);
		if(runNum == 3)
			ChangeGear();
		if(runNum == 4)
			goDistance(6, .3);
		if(runNum == 3)
			ChangeGear();
	}
	
	
	public void Airship()
	{
		if(runNum == 0)
			goDistance(114.6985- robotLength, -.3);
		if(runNum == 1)
			goDistance(2, .3);
		
	}

	public void AirshipDrop()
	{
		if(runNum == 0)
			goDistance(114.6985- robotLength, -.5);
		if(runNum == 1)
			ChangeGear();
		if(runNum == 2)
			goDistance(6, .3);
		if(runNum == 3)
			ChangeGear();
	}
	
	public void Boiler()
	{
		if(runNum == 0)
			goDistance(89.09147 - robotLength, -.5);
		if(runNum == 1)
			turnEncoder(60, .3);
		if(runNum == 2)
			goDistance(85.30307 + robotLength, -.5);
	}
	
	public void BoilerDrop()
	{
		if(runNum == 0)
			goDistance(89.09147 - robotLength, -.5);
		if(runNum == 1)
			turnEncoder(60, .3);
		if(runNum == 2)
			goDistance(85.30307 + robotLength, -.5);
		if(runNum == 3)
			ChangeGear();
		if(runNum == 4)
			goDistance(6, .3);
		if(runNum == 5)
			ChangeGear();
	}
	
	public void ShootInBoiler()
	{
		if(runNum == 0)
			goDistance(67.8642 - robotLength, .3);
		if(runNum == 1)
			turnEncoder(-46.24, .3);
		if(runNum == 2)
			goDistance(84.46555 - robotLength, -.3);
		if(runNum == 3)
			StartShooter(7);
		if(runNum == 4)
			goDistance(20, .5);
		if(runNum == 5)
			turnEncoder(-45, .3);
		if(runNum == 6)
			goDistance(40, .5);
	}
	
	public void Test()
	{
		if(runNum == 0)
			goDistance(10 * 12, .3);
		if(runNum == 1)
			turn(90, .5);
		if(runNum == 2)
			goDistance(10 * 12, .3);
		if(runNum == 3)
			turn(-90, .5);
		if(runNum == 4)
			goDistance(2 * 12, .3);
	}
	
	
	//Autonomous Commands
	private void goDistance(double dist, double speed)
	{

		double timeWarm = .5;
		double timeNeeded = timeWarm + ((dist / circumference) / ((speed * 5310) / (60 * 12.75)));
		
		if(!hasRun)
		{
			timerSpeed.start();
		}
		
		if(isEnc)
		{
			if(!hasRun)
			{
				lEnc.reset();
				rEnc.reset();
				hasRun = true;
			}
			
			speed *= -1;
			
			if(Math.abs(lEnc.getDistance()) - Math.abs(rEnc.getDistance()) > 5)
			{
				left.drive(-speed);
				right.drive(speed-.1);
			}
			else if(Math.abs(rEnc.getDistance()) - Math.abs(lEnc.getDistance()) > 5)
			{
				left.drive(-speed+.1);
				right.drive(speed);
			}
			else
			{
				left.drive(-speed);
				right.drive(speed);
			}
			
			double small = Math.min(Math.abs(lEnc.getDistance()), Math.abs(rEnc.getDistance()));
			
			if(small * circumference > dist || timerSpeed.get()> timeNeeded + 5)
			{
				System.out.println("Stop");
				
				right.drive(0);
				left.drive(0);
				
				turnController.disable();
				
				hasRun = false;
				if(runNum>=0)
					runNum++;
				else
					runNum--;
			}
		}
		
		//For when the encoders break
		else if(!isEnc)
		{
			int dist2 = 0;
			
			if(!hasRun)
			{
				timer.start();
				hasRun = true;
			}
			
			if(timer.get()>timeNeeded)
			{
				right.drive(0);
				left.drive(0);
				
				hasRun = false;
				if(runNum>=0)
					runNum++;
				else
					runNum--;
				timeNeeded = 0;
				
				timer.stop();
				timer.reset();
			}
		}
	}

	private void turn(double angle, double speed)
	{
		if(redSide) angle = -angle;
		
		if(navx != null)
			turnNavx(angle, speed);
		else
			turnEncoder(angle, speed);
	}
	
	private void turnEncoder(double angle, double speed)
	{
		double percent = Math.abs(angle)/360;
		if(!hasRun)
		{
			lEnc.reset();
			rEnc.reset();
		}
		if(!hasRun&&angle<0)
		{
			right.drive(speed);
			left.drive(speed);
			hasRun = true;
		}
		else if(!hasRun&&angle>0)
		{
			right.drive(-speed);
			left.drive(-speed);
			hasRun = true;
		}
		else if(!hasRun&&angle==0)
			hasRun=true;
		
		double large = Math.max(Math.abs(lEnc.getDistance()), Math.abs(rEnc.getDistance()));
		
		if(large*circumference >= (19.5*Math.PI)*percent)
		{
			right.drive(0);
			left.drive(0);
			
			hasRun = false;
			if(runNum>=0)
				runNum++;
			else
				runNum--;
		}
	}

	private void turnNavx(double angle, double speed)
	{
		if(!hasRun)
		{
			navx.reset();
			hasRun = true;
			
			turnController.enable();
			turnController.setOutputRange(-speed, speed);
			turnController.setSetpoint(angle * 1f);
		}
		
		left.drive(rotateToAngleRate);
		right.drive(rotateToAngleRate);
		
		if(turnController.onTarget())
		{
			if(timer.get() == 0)
			{
				timer.reset();
				timer.start();
			}
			
			if(timer.get() > .25)
			{
				right.drive(0);
				left.drive(0);
				
				turnController.disable();
				
				timer.stop();
				timer.reset();
				
				hasRun = false;
				if(runNum>=0)
					runNum++;
				else
					runNum--;
			}
		}
		else
		{
			timer.stop();
			timer.reset();
		}
	}
	
	private void wait(double time)
	{
		if(!hasRun)
		{
			timer.start();
			hasRun = true;
		}
		if(timer.get()== time && hasRun)
		{
			timer.stop();
			timer.reset();
			hasRun = false;
			if(runNum>=0)
				runNum++;
			else
				runNum--;
		}
	}

	private void ChangeGear()
	{
		holder.ChangePosition(true);
		holder.ChangePosition(false);
		
		if(!hasRun)
		{
			timer.reset();
			timer.start();

			hasRun = true;
		}
		
		
		if(timer.get() > .3 && hasRun == true)
		{
			timer.stop();
			timer.reset();
			
			hasRun = false;
			if(runNum>=0)
				runNum++;
			else
				runNum--;
		}
		
	}

	private void StartShooter(double time)
	{
		if(!hasRun)
		{
			timer.reset();
			timer.start();
			
			

			hasRun = true;
		}
		
		
		shoot.Shoot(timer.get() <= time);
		
		if(timer.get() > time && hasRun == true)
		{
			timer.stop();
			timer.reset();
			
			shoot.Shoot(false);
			
			hasRun = false;
			if(runNum>=0)
				runNum++;
			else
				runNum--;
		}
	}
	
	@Override
	public void pidWrite(double output)
	{
		rotateToAngleRate = output;
	}
}
