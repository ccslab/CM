public class QoS3PubrecList {
	
	String strSubscriber;
	
	long PUBRECTime;
	long pingpongTime;
	
	QoS3PubrecList(String strSubscriber, long PUBRECTime, long pingpongTime){
		this.strSubscriber=strSubscriber;
		this.PUBRECTime=PUBRECTime;
		this.pingpongTime=pingpongTime;
	}

	public String getSubscriber() {
		return strSubscriber;
	}
	public void setSubscriber(String strSubscriber) {
		this.strSubscriber = strSubscriber;
	}
	public long getPUBRECTime() {
		return PUBRECTime;
	}
	public void setPUBRECTime(long pUBRECTime) {
		PUBRECTime = pUBRECTime;
	}
	public long getPingpongTime() {
		return pingpongTime;
	}
	public void setPingpongTime(long pingpongTime) {
		this.pingpongTime = pingpongTime;
	}
}
