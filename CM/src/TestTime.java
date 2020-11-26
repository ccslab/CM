
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
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime() {
		endTime = System.currentTimeMillis();
	}

	// time sum
	public void setTimeSum() {
		startTime=endTime-startTime;
	}
	
	public void initializeTimeSum() {
		startTime = 0;
	}
	
	public void addTimeSum(long t) {
		startTime = startTime + t;
	}

	public void addTimeSum() {
		long thisTime=endTime-startTime;
		startTime = startTime + thisTime;
	}

	public long getTimeSum() {
		return startTime;
	}
}
