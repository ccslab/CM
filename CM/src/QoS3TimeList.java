import kr.ac.konkuk.ccslab.cm.entity.CMList;

public class QoS3TimeList {
	
	int nPacketID;
	
	long PUBLISHTime;
	CMList<QoS3PubrecList> pubrecList;
	
	QoS3TimeList(int nPacketID,	long PUBLISHTime){
		this.nPacketID=nPacketID;
		this.PUBLISHTime=PUBLISHTime;
		pubrecList=new CMList<QoS3PubrecList>();
	}
	
	public int getPacketID() {
		return nPacketID;
	}
	public void setPacketID(int nPacketID) {
		this.nPacketID = nPacketID;
	}
	public long getPUBLISHTime() {
		return PUBLISHTime;
	}
	public void setPUBLISHTime(long pUBLISHTime) {
		PUBLISHTime = pUBLISHTime;
	}
	
	public boolean add(String strSubscriber, long PUBRECTime, long pingpongTime)
	{
		QoS3PubrecList newEvent = new QoS3PubrecList(strSubscriber, PUBRECTime, pingpongTime);
		if(newEvent != null)
		{
			System.err.println("test.addtestTime3List(), the same packet ID ("+nPacketID+"-"+strSubscriber+") already exists!");
			System.err.println(newEvent.toString());
			return false;
		}
		return pubrecList.addElement(newEvent);
	}
	
	public QoS3PubrecList find(String strSubscriber)
	{
		int nID = -1;
		String strReceiver = "";
		for(QoS3PubrecList newEvent : pubrecList.getList())
		{
			strReceiver = newEvent.getSubscriber();
			if(strReceiver.equals(strSubscriber)) {
				return newEvent;
			}
		}
		return null;
	}
	
	public boolean remove(String strSubscriber)
	{
		QoS3PubrecList unackEvent = find(strSubscriber);
		if(unackEvent == null)
			return false;

		return pubrecList.removeElement(unackEvent);
	}
	
	public void removeAll()
	{
		pubrecList.removeAllElements();
		return;
	}

}
