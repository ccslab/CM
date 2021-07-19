import java.util.Random;

import kr.ac.konkuk.ccslab.cm.entity.CMList;


public class QoS3Util {
	String strUserName;
	String usernum;
	CMList<QoS3TimeList> testTime3List;
	
	QoS3Util(){
		testTime3List=new CMList<QoS3TimeList>();
	}
	
	public String randomUserName() {
		usernum=numberGen(4,1);
		strUserName="user"+usernum;
//		usernum++;
		return strUserName;
	}
	
	/**
     * 전달된 파라미터에 맞게 난수를 생성한다
     * @param len : 생성할 난수의 길이
     * @param dupCd : 중복 허용 여부 (1: 중복허용, 2:중복제거)
     */
	public static String numberGen(int len, int dupCd) {

		Random rand = new Random();
		String numStr = ""; // 난수가 저장될 변수

		for (int i = 0; i < len; i++) {

			// 0~9 까지 난수 생성
			String ran = Integer.toString(rand.nextInt(10));

			if (dupCd == 1) {
				// 중복 허용시 numStr에 append
				numStr += ran;
			} else if (dupCd == 2) {
				// 중복을 허용하지 않을시 중복된 값이 있는지 검사한다
				if (!numStr.contains(ran)) {
					// 중복된 값이 없으면 numStr에 append
					numStr += ran;
				} else {
					// 생성된 난수가 중복되면 루틴을 다시 실행한다
					i -= 1;
				}
			}
		}
		return numStr;
	}
	
	
	public boolean add(int nPacketID, long PUBLISHTime)
	{
		QoS3TimeList newEvent = new QoS3TimeList(nPacketID, PUBLISHTime);
		if(newEvent != null)
		{
			System.err.println("test.addtestTime3List(), the same packet ID ("+nPacketID+") already exists!");
			System.err.println(newEvent.toString());
			return false;
		}
		return testTime3List.addElement(newEvent);
	}
	
	public boolean addElement(int nPacketID, String strSubscriber, long PUBRECTime)
	{
		QoS3TimeList timeEvent = find(nPacketID);
		if(timeEvent == null)
			return false;
		
		long pingpongTime = PUBRECTime - timeEvent.getPUBLISHTime();
		
		timeEvent.add(strSubscriber, PUBRECTime, pingpongTime);
		
		return false;
	}
	
	public QoS3TimeList find(int nPacketID)
	{
		int nID = -1;
		for(QoS3TimeList newEvent : testTime3List.getList())
		{
			nID = newEvent.getPacketID();
			if(nID == nPacketID) {
				return newEvent;
			}
		}
		return null;
	}
	
	public boolean remove(int nPacketID)
	{
		QoS3TimeList timeEvent = find(nPacketID);
		if(timeEvent == null)
			return false;
		
		timeEvent.removeAll();
		
		return testTime3List.removeElement(timeEvent);
	}
	
	public void removeAll()
	{
		testTime3List.removeAllElements();
		return;
	}

}
