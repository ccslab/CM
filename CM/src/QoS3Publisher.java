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
	int sub_num;
	
	public QoS3Publisher()
	{
		m_clientStub = new CMClientStub();
		m_eventHandler = new QoS3PubEventHandler(m_clientStub);
		m_bRun = true;
		util=new QoS3Util();
		sub_num=m_eventHandler.SUBNUM;
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
		while(tf) {
//			System.out.println(">");
			num=m_scan.nextInt();
			switch(num) {
			case 0:
				tf=false;
				continue;
			case 12: //test 1, qos 2
				timeChk1_Qos2();
				break;
			case 13: //test 1, qos 3
				timeChk1_Qos3();
				break;
			case 22: //test 2, qos 2
				timeChk2_Qos2();
				break;
			case 23: //test 2, qos 3
				timeChk2_Qos3();
				break;
			}
		}
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
	
	public void mqttPublish(byte qos, int nMinNumWaitedEvents, String strMessage)
	{
		System.out.println("========== MQTT publish");
		
		String strTopic = "3";
//		String strMessage = "message";
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
					+", bRetainFlag:"+bRetainFlag+", strReceiver:"+strReceiver+", nMinNumWaitedEvents:"+1);
			mqttManager.publish(strTopic, strMessage, qos, bDupFlag, bRetainFlag, strReceiver, 1);
		}else {
			mqttManager.publish(strTopic, strMessage, qos, bDupFlag, bRetainFlag);
		}

	}
	
	public void timeChk1_Qos3(){
		int packetnum=m_eventHandler.PACKETNUM;
//		int sub_num=1;
		String strMessage = "message";
		
		m_eventHandler.time1.initializeTimeSum();
		m_eventHandler.count1=m_eventHandler.PACKETNUM;
		
		System.out.println("=========== start_time1_qos3 ===========");
		m_eventHandler.time1.setStartTime();
		
		for(int i=0;i<packetnum;i++)
			mqttPublish((byte)3, sub_num, strMessage);
	}
	
	public void timeChk1_Qos2(){
		int packetnum=m_eventHandler.PACKETNUM;
//		int sub_num=1;
		String strMessage = "message";
		
		m_eventHandler.time1.initializeTimeSum();
		m_eventHandler.count1=m_eventHandler.PACKETNUM;
		
		System.out.println("=========== start_time1_qos2 ===========");
		m_eventHandler.time1.setStartTime();
		
		for(int i=0;i<packetnum;i++)
			mqttPublish((byte)2, sub_num, strMessage);
	}
	
	public void timeChk2_Qos3(){
		int packetnum=m_eventHandler.PACKETNUM;
//		int sub_num=1;
		String strMessage = "nruter";
		
		m_eventHandler.time2.initializeTimeSum();
		m_eventHandler.count2=m_eventHandler.PACKETNUM*sub_num;
		
		System.out.println("=========== start_time2_qos3 ===========");
		m_eventHandler.time2.setStartTime();
		
		for(int i=0;i<packetnum;i++)
			mqttPublish((byte)3, sub_num, strMessage);
	}
	
	public void timeChk2_Qos2(){
		int packetnum=m_eventHandler.PACKETNUM;
//		int sub_num=1;
		String strMessage = "return";
		
		m_eventHandler.time2.initializeTimeSum();
		m_eventHandler.count2=m_eventHandler.PACKETNUM*sub_num;
		
		System.out.println("=========== start_time2_qos2 ===========");
		m_eventHandler.time2.setStartTime();
		
		for(int i=0;i<packetnum;i++)
			mqttPublish((byte)2, sub_num, strMessage);
	}
	
}
