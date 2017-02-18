package org.usfirst.frc.team4361.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.*;


public class Autonomous {

	double diameter;
	double circumference;
	double distanceNeeded;
	
	boolean isEnc, hasRun;
	int runNum, lEncNum, rEncNum, large;
	Timer timer, timerSpeed, shotTime;
	
	Drive left, right;
	Shooter shoot;
	
	Encoder lEnc, rEnc;
	
	int minDist, maxDist;

	AHRS navx;
	
	TurnControl turn;
	
	boolean blueSide;
	
	//Constructers
	public Autonomous(Drive left, Drive right, Shooter shoot, boolean blueSide)
	{
		diameter = 6 + 1/8;
		circumference = diameter * Math.PI;
		
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
		
		this.blueSide = blueSide;
	}
	public Autonomous(Drive left, Drive right, Shooter shoot, boolean blueSide, Encoder lEnc, Encoder rEnc)
	{
		this(left, right, shoot, blueSide);
		this.lEnc = lEnc;
		this.rEnc = rEnc;
		runNum = 0;
	}
	
	
	//Different Autonomous Modes
	public void defaultGoToBaseLine()
	{
		if(runNum == 0)
			goDistance(93.5, .3);
	}
	
	public void Feeder()
	{
		if(runNum == 0)
			goDistance(75.10132, .3);
		if(runNum == 1)
			turn(-120);
		if(runNum == 2)
			goDistance(-71.9735, .3);
	}
	
	public void Airship()
	{
		if(runNum == 0)
			goDistance(93.5, .3);
	}
	
	public void Boiler()
	{
		if(runNum == 0)
			goDistance(63.03447, .3);
		if(runNum == 1)
			turn(120);
		if(runNum == 2)
			goDistance(-95.93105, .3);
	}
	
	public void ShootInBoiler()
	{
		if(runNum == 0)
			goDistance(93.5, .3);
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
			
			large = Math.abs(Math.max(lEnc.getRaw(), rEnc.getRaw()));
			
			if(large*circumference>dist)
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
		if(blueSide) angle = -angle;
		
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
		
		large = Math.abs(Math.max(lEnc.getRaw(), rEnc.getRaw()));
		double radius = ((30+1/4)*Math.PI);
		if(large*circumference>=radius*percent)
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
		if(blueSide) angle = -angle;
		
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
