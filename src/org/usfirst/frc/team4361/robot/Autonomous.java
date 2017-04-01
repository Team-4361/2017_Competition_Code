package org.usfirst.frc.team4361.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.*;


public class Autonomous {

	double diameter;
	double circumference;
	double distanceNeeded, large, StartAngle;
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
		large = 0;
		hasRun = false;
		runNum = 0;
		timer = new Timer();
		shotTime = new Timer();
		timerSpeed = new Timer();
		GearTimer = new Timer();
		
		this.left = left;
		this.right = right;
		this.shoot = shoot;
		
		navx = new AHRS(SerialPort.Port.kMXP);
		
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
			//turnNavx(90);
			goDistance(94 - robotLength, -.4);
	}
	
	public void Feeder()
	{
		if(runNum == 0)
			goDistance(108.39373 - robotLength, -.5);
		if(runNum == 1)
			turnEncoder(-60);
		if(runNum == 2)
			goDistance(46.79856 - robotLength, -.5);
	}

	public void FeederDrop()
	{
		if(runNum == 0)
			goDistance(108.39373 - robotLength, -.5);
		if(runNum == 1)
			turnEncoder(-60);
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
			turnEncoder(60);
		if(runNum == 2)
			goDistance(85.30307 + robotLength, -.5);
	}
	
	public void BoilerDrop()
	{
		if(runNum == 0)
			goDistance(89.09147 - robotLength, -.5);
		if(runNum == 1)
			turnEncoder(60);
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
			turnEncoder(-46.24);
		if(runNum == 2)
			goDistance(84.46555 - robotLength, -.3);
	}
	
	
	//Autonomous Commands
	private void goDistance(double dist, double speed)
	{

		double timeWarm = .5;
		double timeNeeded = timeWarm + ((dist / circumference) / ((speed * 5310) / (60 * 12.75)));
		
		if(!hasRun)
		{
			right.drive(-speed);
			left.drive(speed);
			timer.start();
		}
		
		if(isEnc)
		{
			if(!hasRun)
			{
				lEnc.reset();
				rEnc.reset();
				hasRun = true;
			}
			
			large = Math.max(Math.abs(lEnc.getDistance()), Math.abs(rEnc.getDistance()));
			
			if(large * circumference > dist || timer.get() - 7 > timeNeeded)
			{
				System.out.println("Stop");
				
				right.drive(0);
				left.drive(0);
				
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

	private void turnEncoder(double angle)
	{
		if(redSide) angle = -angle;
		
		double percent = Math.abs(angle)/360;
		if(!hasRun)
		{
			lEnc.reset();
			rEnc.reset();
		}
		if(!hasRun&&angle<0)
		{
			right.drive(.3);
			left.drive(.3);
			hasRun = true;
		}
		else if(!hasRun&&angle>0)
		{
			right.drive(-.3);
			left.drive(-.3);
			hasRun = true;
		}
		else if(!hasRun&&angle==0)
			hasRun=true;
		
		large = Math.max(Math.abs(lEnc.getDistance()), Math.abs(rEnc.getDistance()));
		
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
	
	private void turn(double angle)
	{
		if(redSide) angle = -angle;
		
		
		System.out.println("Current is " + navx.getAngle());
		
		double speed = turn.turnAngle(navx.getAngle(), angle);
		System.out.println(speed);
   		//left.drive(speed);
		//right.drive(speed);
		if(speed==0)
		{
			System.out.println("Done");
			
			runNum++;
		}
	}
	
	private void turnNavx(double angle)
	{
		if(redSide) angle = -angle;
		
		if(!hasRun)
		{
			StartAngle = navx.getAngle();
		}
		if(!hasRun&&angle<0)
		{
			right.drive(.3);
			left.drive(.3);
			hasRun = true;
		}
		else if(!hasRun&&angle>0)
		{
			right.drive(-.3);
			left.drive(-.3);
			hasRun = true;
		}
		else if(!hasRun&&angle==0)
			hasRun=true;
		
		if(Math.abs(StartAngle - navx.getAngle()) >= Math.abs(angle))
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
			timer.start();

			hasRun = true;
		}
		
		
		if(timer.get() > .3)
		{
			hasRun = false;
			if(runNum>=0)
				runNum++;
			else
				runNum--;
		}
		
	}
}
