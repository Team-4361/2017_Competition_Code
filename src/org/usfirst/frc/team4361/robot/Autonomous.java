package org.usfirst.frc.team4361.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.*;


public class Autonomous {

	double diameter;
	double circumference;
	double distanceNeeded, large;
	final double robotLength;
	
	boolean isEnc, hasRun;
	int runNum, lEncNum, rEncNum;
	Timer timer, timerSpeed, shotTime;
	
	Drive left, right;
	Shooter shoot;
	
	Encoder lEnc, rEnc;
	
	int minDist, maxDist;

	AHRS navx;
	
	TurnControl turn;
	
	boolean redSide;
	
	//Constructers
	public Autonomous(Drive left, Drive right, Shooter shoot, boolean redSide)
	{
		robotLength = 35.5;
		
		diameter = 6 + 1/8;
		circumference = diameter * Math.PI;
		
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
		
		this.left = left;
		this.right = right;
		this.shoot = shoot;
		
		navx = new AHRS(SerialPort.Port.kMXP);
		
		this.redSide = redSide;
	}
	public Autonomous(Drive left, Drive right, Shooter shoot, boolean redSide, Encoder lEnc, Encoder rEnc)
	{
		
		this(left, right, shoot, redSide);
		
		diameter = 6 + 1/8;
		circumference = diameter * Math.PI;
		
		lEnc.setDistancePerPulse(1.0/360);
		rEnc.setDistancePerPulse(1.0/360);
		
		this.lEnc = lEnc;
		this.rEnc = rEnc;

		isEnc = true;
	}
	
	
	//Different Autonomous Modes
	public void defaultGoToBaseLine()
	{
		//System.out.println(lEnc.getRaw() + " : " + rEnc.getRaw() + " : " + lEnc.getEncodingScale() + " : " + rEnc.getEncodingScale() + " : " + lEnc.getRate());
		
		if(runNum == 0)
			goDistance(94 - robotLength, -.5);
	}
	
	public void Feeder()
	{
		if(runNum == 0)
			goDistance(108.39373 - robotLength, .3);
		if(runNum == 1)
			turn(-120);
		if(runNum == 2)
			goDistance(46.79856 - robotLength, -.3);
	}
	
	public void Airship()
	{
		if(runNum == 0)
			goDistance(114.6985- robotLength, -.3);
	}
	
	public void Boiler()
	{
		if(runNum == 0)
			goDistance(89.09147 - robotLength, .3);
		if(runNum == 1)
			turn(120);
		if(runNum == 2)
			goDistance(85.30307 + robotLength, -.3);
	}
	
	public void ShootInBoiler()
	{
		if(runNum == 0)
			goDistance(67.8642 - robotLength, .3);
		if(runNum == 1)
			turn(-46.24);
		if(runNum == 2)
			goDistance(84.46555 - robotLength, -.3);
	}
	
	
	//Autonomous Commands
	private void goDistance(double dist, double speed)
	{
		if(!hasRun)
		{
			right.drive(-speed);
			left.drive(speed);
		}
		
		if(isEnc)
		{
			if(!hasRun)
			{
				lEnc.reset();
				rEnc.reset();
				hasRun = true;
			}
			
			large = Math.abs(Math.max(lEnc.getDistance(), rEnc.getDistance()));
			
			if(large * circumference > dist)
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
			double timeWarm = .5;
			int dist2 = 0;
			double timeNeeded = timeWarm + ((dist / circumference) / ((speed * 5310) / (60 * 12.75)));
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
		
		large = Math.abs(Math.max(lEnc.getDistance(), rEnc.getDistance()));
		double radius = ((30+1/4)*Math.PI);
		if(large>=radius*percent)
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
		
		double speed =turn.turnAngle(navx.getAngle(), angle);
   		left.drive(-speed);
		right.drive(-speed);
		if(speed==0)
		{
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
}
