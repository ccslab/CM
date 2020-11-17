import java.io.FileOutputStream;
import java.io.PrintWriter;

import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.event.CMSessionEvent;
import kr.ac.konkuk.ccslab.cm.event.handler.CMAppEventHandler;
import kr.ac.konkuk.ccslab.cm.event.mqttevent.CMMqttEvent;
import kr.ac.konkuk.ccslab.cm.event.mqttevent.CMMqttEventCONNACK;
import kr.ac.konkuk.ccslab.cm.event.mqttevent.CMMqttEventPUBACK;
import kr.ac.konkuk.ccslab.cm.event.mqttevent.CMMqttEventPUBCOMP;
import kr.ac.konkuk.ccslab.cm.event.mqttevent.CMMqttEventPUBLISH;
import kr.ac.konkuk.ccslab.cm.event.mqttevent.CMMqttEventPUBREC;
import kr.ac.konkuk.ccslab.cm.event.mqttevent.CMMqttEventPUBREL;
import kr.ac.konkuk.ccslab.cm.event.mqttevent.CMMqttEventSUBACK;
import kr.ac.konkuk.ccslab.cm.event.mqttevent.CMMqttEventUNSUBACK;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;
import kr.ac.konkuk.ccslab.cm.manager.CMMqttManager;
import kr.ac.konkuk.ccslab.cm.stub.CMClientStub;

public class QoS3PubEventHandler implements CMAppEventHandler{
	private CMClientStub m_clientStub;
	private long m_lDelaySum;	// for forwarding simulation
	private long m_lStartTime;	// for delay of SNS content downloading, distributed file processing
	private int m_nEstDelaySum;	// for SNS downloading simulation
	private int m_nSimNum;		// for simulation of multiple sns content downloading
	private FileOutputStream m_fos;	// for storing downloading delay of multiple SNS content
	private PrintWriter m_pw;		//
	private int m_nCurrentServerNum;	// for distributed file processing
	private int m_nRecvPieceNum;		// for distributed file processing
	private boolean m_bDistFileProc;	// for distributed file processing
	private String m_strExt;			// for distributed file processing
	private String[] m_filePieces;		// for distributed file processing
	private int m_nMinNumWaitedEvents;  // for checking the completion of asynchronous castrecv service
	private int m_nRecvReplyEvents;		// for checking the completion of asynchronous castrecv service
	
	long start;
	long end;
	long middle;
	TestTime time;
		
	public QoS3PubEventHandler(CMClientStub stub)
	{
		m_clientStub = stub;
		m_lDelaySum = 0;
		m_lStartTime = 0;
		m_nEstDelaySum = 0;
		m_nSimNum = 0;
		m_fos = null;
		m_pw = null;
		m_nCurrentServerNum = 0;
		m_nRecvPieceNum = 0;
		m_bDistFileProc = false;
		m_strExt = null;
		m_filePieces = null;
		
		time=new TestTime();
	}
	
	//////////////////////////////////////////////////////////////////////////////
	// get/set methods
	
	public void setStartTime(long time)
	{
		m_lStartTime = time;
	}
	
	public void setFileOutputStream(FileOutputStream fos)
	{
		m_fos = fos;
	}
	
	public FileOutputStream getFileOutputStream()
	{
		return m_fos;
	}
	
	public void setPrintWriter(PrintWriter pw)
	{
		m_pw = pw;
	}
	
	public PrintWriter getPrintWriter()
	{
		return m_pw;
	}
	
	public void setSimNum(int num)
	{
		m_nSimNum = num;
	}
	
	public int getSimNum()
	{
		return m_nSimNum;
	}
	
	public void setCurrentServerNum(int num)
	{
		m_nCurrentServerNum = num;
	}
	
	public int getCurrentServerNum()
	{
		return m_nCurrentServerNum;
	}
	
	public void setRecvPieceNum(int num)
	{
		m_nRecvPieceNum = num;
	}
	
	public int getRecvPieceNum()
	{
		return m_nRecvPieceNum;
	}
	
	public void setDistFileProc(boolean b)
	{
		m_bDistFileProc = b;
	}
	
	public boolean isDistFileProc()
	{
		return m_bDistFileProc;
	}
	
	public void setFileExtension(String ext)
	{
		m_strExt = ext;
	}
	
	public String getFileExtension()
	{
		return m_strExt;
	}
	
	public void setFilePieces(String[] pieces)
	{
		m_filePieces = pieces;
	}
	
	public String[] getFilePieces()
	{
		return m_filePieces;
	}
	
	public void setMinNumWaitedEvents(int num)
	{
		m_nMinNumWaitedEvents = num;
	}
	
	public int getMinNumWaitedEvents()
	{
		return m_nMinNumWaitedEvents;
	}
	
	public void setRecvReplyEvents(int num)
	{
		m_nRecvReplyEvents = num;
	}
	
	public int getRecvReplyEvents()
	{
		return m_nRecvReplyEvents;
	}
	
	//////////////////////////////////////////////////////////////////////////////
	
	@Override
	public void processEvent(CMEvent cme) {
		// TODO Auto-generated method stub
		//System.out.println("Client app receives CM event!!");
		switch(cme.getType())
		{
		case CMInfo.CM_SESSION_EVENT:
			processSessionEvent(cme);
			break;
		case CMInfo.CM_MQTT_EVENT:
			processMqttEvent(cme);
			break;
		default:
			return;
		}
	}
	
	private boolean processSessionEvent(CMEvent cme)
	{
		CMSessionEvent se = (CMSessionEvent)cme;
		switch(se.getID())
		{
		case CMSessionEvent.LOGIN_ACK:
			if(se.isValidUser() == 0)
			{
				System.err.println("This client fails authentication by the default server!");
			}
			else if(se.isValidUser() == -1)
			{
				System.err.println("This client is already in the login-user list!");
			}
			else
			{
				System.out.println("This client successfully logs in to the default server.");
			}
			//*********mqtt connect
			mqttConnect();
			return true;
		case CMSessionEvent.REGISTER_USER_ACK:
			if( se.getReturnCode() == 1 )
			{
				// user registration succeeded
				System.out.println("User["+se.getUserName()+"] successfully registered at time["
							+se.getCreationTime()+"].");
			}
			else
			{
				// user registration failed
				System.out.println("User["+se.getUserName()+"] failed to register!");
			}
			return true;
		case CMSessionEvent.DEREGISTER_USER_ACK:
			if( se.getReturnCode() == 1 )
			{
				// user deregistration succeeded
				System.out.println("User["+se.getUserName()+"] successfully deregistered.");
			}
			else
			{
				// user registration failed
				System.out.println("User["+se.getUserName()+"] failed to deregister!");
			}
			return true;
		case CMSessionEvent.FIND_REGISTERED_USER_ACK:
			if( se.getReturnCode() == 1 )
			{
				System.out.println("User profile search succeeded: user["+se.getUserName()
						+"], registration time["+se.getCreationTime()+"].");
			}
			else
			{
				System.out.println("User profile search failed: user["+se.getUserName()+"]!");
			}
			return true;
		case CMSessionEvent.UNEXPECTED_SERVER_DISCONNECTION:
			System.err.println("Unexpected disconnection from ["+se.getChannelName()
					+"] with key["+se.getChannelNum()+"]!");
			return true;
		case CMSessionEvent.INTENTIONALLY_DISCONNECT:
			System.err.println("Intentionally disconnected all channels from ["
					+se.getChannelName()+"]!");
			return true;
		default:
			return false;
		}
	}
	
	private void processMqttEvent(CMEvent cme)
	{
		switch(cme.getID())
		{
		case CMMqttEvent.CONNACK:
			CMMqttEventCONNACK conackEvent = (CMMqttEventCONNACK)cme;
			//System.out.println("received "+conackEvent);
			System.out.println("["+conackEvent.getSender()+"] sent CMMqttEvent.CONNACK, "
					+ "[return code: "+conackEvent.getReturnCode()+"]");
			//*************subscribe answer
			mqttSubscribe();
			break;
		case CMMqttEvent.PUBLISH:
			CMMqttEventPUBLISH pubEvent = (CMMqttEventPUBLISH)cme;
			//System.out.println("received "+pubEvent);
			System.out.println("["+pubEvent.getSender()+"] sent CMMqttEvent.PUBLISH, "
					+ "[packet ID: "+pubEvent.getPacketID()+"], [topic: "
					+pubEvent.getTopicName()+"], [msg: "+pubEvent.getAppMessage()
					+"], [QoS: "+pubEvent.getQoS()+"]");
//			end = System.currentTimeMillis(); //time_1
//			long time=end-start;
//			System.out.println("========결과: "+time+"ms");
			break;
		case CMMqttEvent.PUBACK:
			CMMqttEventPUBACK pubackEvent = (CMMqttEventPUBACK)cme;
			//System.out.println("received "+pubackEvent);
			System.out.println("["+pubackEvent.getSender()+"] sent CMMqttEvent.PUBACK, "
					+ "[packet ID: "+pubackEvent.getPacketID()+"]");
			break;
		case CMMqttEvent.PUBREC:
			CMMqttEventPUBREC pubrecEvent = (CMMqttEventPUBREC)cme;
			//System.out.println("received "+pubrecEvent);
			System.out.println("["+pubrecEvent.getSender()+"] sent CMMqttEvent.PUBREC, "
					+ "[packet ID: "+pubrecEvent.getPacketID()+"]");
			time.setPub_pubrec(System.currentTimeMillis());
//			middle = System.currentTimeMillis(); //time_2
//			long testtime=middle-start;
//			System.out.println("========pubrec 결과: "+(middle-start)+"ms");
			break;
		case CMMqttEvent.PUBREL:
			CMMqttEventPUBREL pubrelEvent = (CMMqttEventPUBREL)cme;
			//System.out.println("received "+pubrelEvent);
			System.out.println("["+pubrelEvent.getSender()+"] sent CMMqttEventPUBREL, "
					+ "[packet ID: "+pubrelEvent.getPacketID()+"]");
			break;
		case CMMqttEvent.PUBCOMP:
			CMMqttEventPUBCOMP pubcompEvent = (CMMqttEventPUBCOMP)cme;
			//System.out.println("received "+pubcompEvent);
			System.out.println("["+pubcompEvent.getSender()+"] sent CMMqttEvent.PUBCOMP, "
					+ "[packet ID: "+pubcompEvent.getPacketID()+"]");
			time.setPub_pubcomp(System.currentTimeMillis());
			printTime();
//			end = System.currentTimeMillis(); //time_2
//			long finaltime=end-start;
//			System.out.println("========pubcomp 결과: "+finaltime+"ms");
			break;
		case CMMqttEvent.SUBACK:
			CMMqttEventSUBACK subackEvent = (CMMqttEventSUBACK)cme;
			//System.out.println("received "+subackEvent);
			System.out.println("["+subackEvent.getSender()+"] sent CMMqttEvent.SUBACK, "
					+subackEvent.getReturnCodeList());
			//********test start
//			System.out.println("========start test");
//			time.setPub_publish(System.currentTimeMillis());
//			timeChk1_Qos3();
			break;
		case CMMqttEvent.UNSUBACK:
			CMMqttEventUNSUBACK unsubackEvent = (CMMqttEventUNSUBACK)cme;
			//System.out.println("received "+unsubackEvent);
			System.out.println("["+unsubackEvent.getSender()+"] sent CMMqttEvent.UNSUBACK");
			break;
		}
		
		return;
	}
	
	public void mqttConnect()
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
			return;
		}
		
		if(bDetail)
		{
			mqttManager.connect(strWillTopic, strWillMessage, bWillRetain, willQoS, bWillFlag, 
					bCleanSession);
		}
		else {
			mqttManager.connect();
		}

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
	
//	public void mqttPublish(byte qos, int nMinNumWaitedEvents)
//	{
//		System.out.println("========== MQTT publish");
//		
//		String strTopic = "3";
//		String strMessage = "message";
////		byte qos = (byte)3;
//		
//		boolean bDupFlag = false;
//		boolean bRetainFlag = false;
//		String strReceiver = "";
////		int nMinNumWaitedEvents = 1;
//		
//		CMMqttManager mqttManager = (CMMqttManager)m_clientStub.findServiceManager(CMInfo.CM_MQTT_MANAGER);
//		if(mqttManager == null)
//		{
//			System.err.println("CMMqttManager is null!");
//			return;
//		}
//		
//		if(qos==3) {
//			System.out.println("strTopic:"+strTopic+", strMessage:"+strMessage+", qos:"+qos+", bDupFlag:"+bDupFlag
//					+", bRetainFlag:"+bRetainFlag+", strReceiver:"+strReceiver+", nMinNumWaitedEvents:"+nMinNumWaitedEvents);
//			mqttManager.publish(strTopic, strMessage, qos, bDupFlag, bRetainFlag, strReceiver, nMinNumWaitedEvents);
//		}else {
//			mqttManager.publish(strTopic, strMessage, qos, bDupFlag, bRetainFlag);
//		}
//
//	}
//	
//	public void timeChk1_Qos3(){
//		int sub_num=1;
////		start = System.currentTimeMillis();
//		
////		for(int i=0;i<10;i++)
//			mqttPublish((byte)3, sub_num);
//		
////		long end = System.currentTimeMillis();
////		long time=end-start;
////		return time;
//	}
//	
//	public void timeChk1_Qos2(){
//		int sub_num=1;
//		start = System.currentTimeMillis();
//		
////		for(int i=0;i<10;i++)
//			mqttPublish((byte)2, sub_num);
//		
////		long end = System.currentTimeMillis();
////		long time=end-start;
////		return time;
//	}
	
	public void printTime() {
//		System.out.println("================= 결과 ================");
		System.out.println("================= pub ================");
		long p_all = time.getPub_pubcomp()-time.getPub_publish();
		System.out.println("전체시간=pubcomp-publish===="+p_all);
		time.setTime_sum(time.getTime_sum()+p_all);
		System.out.println("시간합=time_sum============"+time.getTime_sum());
	}
	
	
}
