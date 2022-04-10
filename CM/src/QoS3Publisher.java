
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
		
		printList();
		
		Scanner m_scan = new Scanner(System.in);
		int num;
		boolean tf=true;
		while(tf) {
			num=m_scan.nextInt();
			switch(num) {
			case 0:
				tf=false;
				continue;
			case 1: 
				printList();
				break;
			case 12: //test 1, qos 2
				m_eventHandler.setCommandNum(1);
				timeChk1_Qos2();
				break;
			case 131: //test 1, qos 3, async
				m_eventHandler.setCommandNum(1);
				timeChk1_Qos3();
				break;
			case 132: //test 1, qos 3, sync
				m_eventHandler.setCommandNum(1);
				timeChk1_Qos3_sync();
				break;
			case 22: //test 2, qos 2
				m_eventHandler.setCommandNum(2);
				timeChk2_Qos2();
				break;
			case 231: //test 2, qos 3, async
				m_eventHandler.setCommandNum(2);
				timeChk2_Qos3();
				break;
			case 232: //test 2, qos 3, sync
				m_eventHandler.setCommandNum(2);
				timeChk2_Qos3_sync();
				break;
			case 32: //test 3, qos 2
				m_eventHandler.setCommandNum(3);
				timeChk3_Qos2();
				break;
			case 331: //test 3, qos 3, async
				m_eventHandler.setCommandNum(3);
				timeChk3_Qos3();
				break;
			case 332: //test 3, qos 3, sync
				m_eventHandler.setCommandNum(3);
				timeChk3_Qos3_sync();
				break;
			case 40:
				m_eventHandler.printTime_1();
				break;
			case 41:
				m_eventHandler.printTime_2();
				break;
			case 97: //test1 print time
				m_eventHandler.printTime_1();
				break;
			case 98: //test2 print time
				m_eventHandler.printTime_2();
				break;
			case 99: //test 3, print result
				printBrokerResult(); 
				break;
			case 11: //test 3, set packetnum (broker count)
				setBrokerPacketNum(); 
				break;
			case 77: //all tests, set sub numbers
				System.out.print("enter subscriber number: ");
				int sn = m_scan.nextInt();
				m_eventHandler.SUBNUM = sn;
				sub_num = sn;
				System.out.println("=================================");
				System.out.println("number of subscribers : " + sn);
				System.out.println("=================================");
				break;
			}
		}
	}
	
	public void printList()
	{
		System.out.println("===================================================");
		System.out.println("enter 0 for exit.");
		System.out.println("enter 1 for this message.");
		System.out.println("=====================time test 1===================");
		System.out.println("12: test 1 qos 2");
		System.out.println("131: test 1 qos 3 async");
		System.out.println("132: test 1 qos 3 sync");
		System.out.println("97: print result - publisher console");
		System.out.println("=====================time test 2===================");
		System.out.println("22: test 2 qos 2");
		System.out.println("231: test 2 qos 3 async");
		System.out.println("232: test 2 qos 3 sync");
		System.out.println("98: print result - publisher console");
		System.out.println("=====================time test 3===================");
		System.out.println("33: test 3 qos 2");
		System.out.println("331: test 3 qos 3 async");
		System.out.println("332: test 3 qos 3 sync");
		System.out.println("99: print result - broker console");
		System.out.println("=======================byte test====================");
		System.out.println("40: byte check on");
		System.out.println("41: byte check off");
		System.out.println("===================================================");
		
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
					+", bRetainFlag:"+bRetainFlag+", strReceiver:"+strReceiver+", nMinNumWaitedEvents:"+nMinNumWaitedEvents);
			mqttManager.publish(strTopic, strMessage, qos, bDupFlag, bRetainFlag, strReceiver, nMinNumWaitedEvents);
		}else {
			mqttManager.publish(strTopic, strMessage, qos, bDupFlag, bRetainFlag);
		}

	}
	
	public void mqttSyncPublish(byte qos, int nMinNumWaitedEvents, String strMessage)
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
					+", bRetainFlag:"+bRetainFlag+", strReceiver:"+strReceiver+", nMinNumWaitedEvents:"+nMinNumWaitedEvents);
			try {
				mqttManager.publish(strTopic, strMessage, qos, bDupFlag, bRetainFlag, strReceiver, nMinNumWaitedEvents);
			} catch (Exception e) {
				System.err.println("QoS3Publisher.mqttSyncPublish exception");
			}
		}else {
			mqttManager.publish(strTopic, strMessage, qos, bDupFlag, bRetainFlag);
		}

	}
	
	public void setBrokerPacketNum()
	{
		System.out.println("========== MQTT publish");
		
		String strTopic = "SETPCKNUM";
		byte qos = (byte)0;
		String strMessage = "" + (m_eventHandler.PACKETNUM * m_eventHandler.SUBNUM);
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
		
		mqttManager.publish(strTopic, strMessage, qos, bDupFlag, bRetainFlag);

	}
	
	public void printBrokerResult()
	{
		System.out.println("========== MQTT publish");
		
		String strTopic = "COMMANDS";
		byte qos = (byte)0;
		String strMessage = "print";
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
		
		mqttManager.publish(strTopic, strMessage, qos, bDupFlag, bRetainFlag);

	}
	
	public void initPublisher(){
		m_eventHandler.time1.initializeTimeSum();
		m_eventHandler.time2.initializeTimeSum();
	}
	
	public void timeChk1_Qos3(){
		int packetnum=m_eventHandler.PACKETNUM;
		String strMessage = "return";
		
		m_eventHandler.time1.initializeTimeSum();
		m_eventHandler.count1=m_eventHandler.PACKETNUM*sub_num;
		
		System.out.println("=========== start_time1_qos3 ===========");
		m_eventHandler.time1.setStartTime();
		
		for(int i=0;i<packetnum;i++)
			mqttPublish((byte)3, -1, strMessage);
	}
	
	public void timeChk1_Qos3_sync(){
		int packetnum=m_eventHandler.PACKETNUM;
		String strMessage = "return";
		
		m_eventHandler.time1.initializeTimeSum();
		m_eventHandler.count1=m_eventHandler.PACKETNUM*sub_num;
		
		System.out.println("=========== start_time1_qos3 ===========");
		m_eventHandler.time1.setStartTime();
		
		for(int i=0;i<packetnum;i++)
			mqttSyncPublish((byte)3, 1, strMessage);
	}
	
	public void timeChk1_Qos2(){
		int packetnum=m_eventHandler.PACKETNUM;
		String strMessage = "return";
		
		m_eventHandler.time1.initializeTimeSum();
		m_eventHandler.count1=m_eventHandler.PACKETNUM;
		
		System.out.println("=========== start_time1_qos2 ===========");
		m_eventHandler.time1.setStartTime();
		
		for(int i=0;i<packetnum;i++) {
			mqttPublish((byte)2, 0, strMessage);
		}
	}
	
	public boolean timeChk2_Qos3(){
		int packetnum=m_eventHandler.PACKETNUM;
		String strMessage = "message";
		
		m_eventHandler.time2.initializeTimeSum();
		m_eventHandler.count2=m_eventHandler.PACKETNUM*sub_num;
		
		System.out.println("=========== start_time2_qos3 ===========");
		m_eventHandler.time2.setStartTime();
		
		for(int i=0;i<packetnum;i++) {
			mqttPublish((byte)3, -1, strMessage);
		}
		
		return true;
	}
	
	public boolean timeChk2_Qos3_sync(){
		int packetnum=m_eventHandler.PACKETNUM;
		String strMessage = "message";
		
		m_eventHandler.time2.initializeTimeSum();
		m_eventHandler.count2=m_eventHandler.PACKETNUM*sub_num;
		
		System.out.println("=========== start_time2_qos3 ===========");
		m_eventHandler.time2.setStartTime();
		
		for(int i=0;i<packetnum;i++) {
			mqttSyncPublish((byte)3, 1, strMessage);
		}
		
		return true;
	}
	
	public boolean timeChk2_Qos2(){
		int packetnum=m_eventHandler.PACKETNUM;
		String strMessage = "message";
		
		m_eventHandler.time2.initializeTimeSum();
		m_eventHandler.count2=m_eventHandler.PACKETNUM;
		
		System.out.println("=========== start_time2_qos2 ===========");
		m_eventHandler.time2.setStartTime();
		
		for(int i=0;i<packetnum;i++)
			mqttPublish((byte)2, 0, strMessage);
		
		return true;
	}
	
	public boolean timeChk3_Qos2(){
		m_eventHandler.count3 = (m_eventHandler.PACKETNUM);
		m_eventHandler.test3Qos = 2;
		m_eventHandler.test3MinNumWatedEvents = 0;
		m_eventHandler.isSync = false;
		
		mqttPublish((byte)2, 0, "test3");
		
		return true;
	}
	
	public boolean timeChk3_Qos3(){
		m_eventHandler.count3 = (m_eventHandler.PACKETNUM);
		m_eventHandler.test3Qos = 3;
		m_eventHandler.test3MinNumWatedEvents = 0;
		m_eventHandler.isSync = false;
		mqttPublish((byte)3, 0, "test3");
		
		return true;
	}
	
	public boolean timeChk3_Qos3_sync(){
		m_eventHandler.count3=m_eventHandler.PACKETNUM;
		m_eventHandler.test3Qos = 3;
		m_eventHandler.test3MinNumWatedEvents = 1;
		m_eventHandler.isSync = true;
		while(m_eventHandler.count3>0) {
			mqttSyncPublish((byte)3, 1, "test3");
			m_eventHandler.count3-=1;
			System.out.println("=====count: "+m_eventHandler.count3+"========");
		}
		
		return true;
	}
}
