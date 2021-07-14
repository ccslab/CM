
public class QoS3TimeList {
	
	int nPacketID;
	String strSubscriber;
	
	long PUBLISHTime;
	long PUBRECTime;
	long pingpongTime;
	
	QoS3TimeList(int nPacketID,	String strSubscriber,long PUBLISHTime){
		this.nPacketID=nPacketID;
		this.strSubscriber=strSubscriber;
		this.PUBLISHTime=PUBLISHTime;
	}
	
	public int getPacketID() {
		return nPacketID;
	}
	public void setPacketID(int nPacketID) {
		this.nPacketID = nPacketID;
	}
	public String getSubscriber() {
		return strSubscriber;
	}
	public void setSubscriber(String strSubscriber) {
		this.strSubscriber = strSubscriber;
	}
	public long getPUBLISHTime() {
		return PUBLISHTime;
	}
	public void setPUBLISHTime(long pUBLISHTime) {
		PUBLISHTime = pUBLISHTime;
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
