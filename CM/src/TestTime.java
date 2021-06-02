
public class TestTime {
	long startTime;
	long endTime;
	
	long time_sum;
	
	// getter & setter
	public long getStartTime() {
		return startTime;
	}

	public void setStartTime() {
		startTime = System.currentTimeMillis();
		time_sum=startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime() {
		endTime = System.currentTimeMillis();
	}

	// time sum
	public void setTimeSum() {
		time_sum=endTime-startTime;
	}
	
	public void initializeTimeSum() {
		startTime = 0;
	}
	
	public void addTimeSum(long t) {
		time_sum = time_sum + t;
	}

	public void addTimeSum() {
		long thisTime=endTime-startTime;
		time_sum = time_sum + thisTime;
	}

	public long getTimeSum() {
		return time_sum;
	}
}
