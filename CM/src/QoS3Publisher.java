import java.io.BufferedReader;
import java.io.Console;
import java.io.InputStreamReader;
import java.util.Scanner;

import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.manager.CMMqttManager;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;

public class QoS3Publisher {
	private CMClientStub m_clientStub;
	private QoS3PubEventHandler m_eventHandler;
	private boolean m_bRun;
	private Scanner m_scan = null;
	private QoS3Util util;
	
	public QoS3Publisher()
	{
		m_clientStub = new CMClientStub();
		m_eventHandler = new QoS3PubEventHandler(m_clientStub);
		m_bRun = true;
		util=new QoS3Util();
		util.usernum=0;
	}
	
	public CMClientStub getClientStub()
	{
		return m_clientStub;
	}
	
	public QoS3PubEventHandler getClientEventHandler()
	{
		return m_eventHandler;
	}
	
	///////////////////////////////////////////////////////////////
	// test methods
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		QoS3Publisher client = new QoS3Publisher();
		CMClientStub cmStub = client.getClientStub();
		cmStub.setAppEventHandler(client.getClientEventHandler());
		client.testStartCM();
		
		System.out.println("Client application is terminated.");
	}
	
	public void testStartCM()
	{
		// get current server info from the server configuration file
		String strCurServerAddress = null;
		int nCurServerPort = -1;
		String strNewServerAddress = null;
		String strNewServerPort = null;
		
		strCurServerAddress = m_clientStub.getServerAddress();
		nCurServerPort = m_clientStub.getServerPort();
		
		// ask the user if he/she would like to change the server info
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("========== start CM");
		System.out.println("current server address: "+strCurServerAddress);
		System.out.println("current server port: "+nCurServerPort);
		
		boolean bRet = m_clientStub.startCM();
		if(!bRet)
		{
			System.err.println("CM initialization error!");
			return;
		}
		
		bRet=ploginDS();
		if(!bRet) {
			System.err.println("Login failed!");
			return;
		}
		Scanner m_scan = new Scanner(System.in);
		int num;
		boolean tf=true;
		long average_time;
		while(tf) {
//			System.out.println(">");
			num=m_scan.nextInt();
			switch(num) {
			case 0:
				tf=false;
				continue;
			case 12: //test 1, qos 2
				m_eventHandler.time.setTime_sum(0);
				System.out.println("========start test");
				m_eventHandler.time.setPub_publish(System.currentTimeMillis());
				timeChk1_Qos2();
				average_time=m_eventHandler.time.getTime_sum()/20;
//				average_time=m_eventHandler.time.getTime_sum();
				System.out.println("=============qos2 test end============");
				System.out.println("average time========"+average_time);
				break;
			case 13: //test 1, qos 3
				m_eventHandler.time.setTime_sum(0);
				System.out.println("========start test");
				m_eventHandler.time.setPub_publish(System.currentTimeMillis());
				timeChk1_Qos3();
				average_time=m_eventHandler.time.getTime_sum()/20;
//				average_time=m_eventHandler.time.getTime_sum();
				System.out.println("=============qos3 test end============");
				System.out.println("average time========"+average_time);
				break;
				
			}
		}
//		bRet=mqttConnect();
//		System.out.println("========start test");
//		m_eventHandler.time.setPub_publish(System.currentTimeMillis());
//		timeChk1_Qos3();
	}
	
	public boolean ploginDS()
	{
		String strUserName = null;
		String strPassword = null;
		boolean bRequestResult = false;
		Console console = System.console();
		if(console == null)
		{
			System.err.println("Unable to obtain console.");
		}
		
		System.out.println("====== login to default server");
		
		strUserName="pub"+util.randomUserName();
		strPassword="0000";
		
		bRequestResult = m_clientStub.loginCM(strUserName, strPassword);
		if(bRequestResult) {
			System.out.println("successfully sent the login request.");
			System.out.println("======");
			return true;
		}
		else {
			System.err.println("failed the login request!");
			System.out.println("======");
			return false;
		}
	}
	
	
//	public void startTest()
//	{
//		System.out.println("client application starts.");
//		int publisher_num=0;
//		boolean keepGoing=ploginDS(publisher_num);
//		publisher_num++;
//		while(keepGoing) {
//			mqttConnect();
//			System.out.println("keepGoing(y/n):");
//			String strDetail = m_scan.nextLine().trim();
//			if(strDetail.contentEquals("n"))
//				keepGoing = false;
//		}
//	}
	
	public boolean mqttConnect()
	{
		System.out.println("========== MQTT connect");

		String strWillTopic = null;
		String strWillMessage = null;
		boolean bWillRetain = false;
		byte willQoS = (byte)0;
		boolean bWillFlag = false;
		boolean bCleanSession = false;
		
		boolean bDetail = false;
		
		CMMqttManager mqttManager = (CMMqttManager) m_clientStub.findServiceManager(CMInfo.CM_MQTT_MANAGER);
		if(mqttManager == null)
		{
			System.err.println("CMMqttManager is null!");
			return false;
		}
		
		if(bDetail)
		{
			mqttManager.connect(strWillTopic, strWillMessage, bWillRetain, willQoS, bWillFlag, 
					bCleanSession);
		}
		else {
			mqttManager.connect();
		}
		return true;

	}
	
	public void mqttSubscribe()
	{
		System.out.println("========== MQTT subscribe");
		String strTopicFilter = "answer";
		byte qos = (byte)3;
		
		CMMqttManager mqttManager = (CMMqttManager)m_clientStub.findServiceManager(CMInfo.CM_MQTT_MANAGER);
		if(mqttManager == null)
		{
			System.err.println("CMMqttManager is null!");
			return;
		}
		mqttManager.subscribe(strTopicFilter, qos);

	}
	
	public void mqttPublish(byte qos, int nMinNumWaitedEvents)
	{
		System.out.println("========== MQTT publish");
		
		String strTopic = "3";
		String strMessage = "message";
//		byte qos = (byte)3;
		
		boolean bDupFlag = false;
		boolean bRetainFlag = false;
		String strReceiver = "";
//		int nMinNumWaitedEvents = 1;
		
		CMMqttManager mqttManager = (CMMqttManager)m_clientStub.findServiceManager(CMInfo.CM_MQTT_MANAGER);
		if(mqttManager == null)
		{
			System.err.println("CMMqttManager is null!");
			return;
		}
		
		if(qos==3) {
			System.out.println("strTopic:"+strTopic+", strMessage:"+strMessage+", qos:"+qos+", bDupFlag:"+bDupFlag
					+", bRetainFlag:"+bRetainFlag+", strReceiver:"+strReceiver+", nMinNumWaitedEvents:"+nMinNumWaitedEvents);
			mqttManager.publish(strTopic, strMessage, qos, bDupFlag, bRetainFlag, strReceiver, nMinNumWaitedEvents);
		}else {
			mqttManager.publish(strTopic, strMessage, qos, bDupFlag, bRetainFlag);
		}

	}
	
	public void timeChk1_Qos3(){
		int sub_num=1;
//		start = System.currentTimeMillis();
		
		for(int i=0;i<20;i++)
			mqttPublish((byte)3, sub_num);
		
//		long end = System.currentTimeMillis();
//		long time=end-start;
//		return time;
	}
	
	public void timeChk1_Qos2(){
		int sub_num=1;
//		start = System.currentTimeMillis();
		
		for(int i=0;i<20;i++)
			mqttPublish((byte)2, sub_num);
		
//		long end = System.currentTimeMillis();
//		long time=end-start;
//		return time;
	}
}
