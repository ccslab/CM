package kr.ac.konkuk.ccslab.cm.event.mqttevent;

import java.nio.ByteBuffer;

import kr.ac.konkuk.ccslab.cm.event.CMEvent;
import kr.ac.konkuk.ccslab.cm.info.CMInfo;

/**
 * This class represents CM events that belong to the MQTT control packets.
 * @author CCSLab, Konkuk University
 *
 */
public abstract class CMMqttEvent extends CMEvent {

	// definition of MQTT event IDs
	
	/**
	 * Event ID of the CMMqttEventCONNECT class.
	 */
	public static final int CONNECT = 1;
	
	/**
	 * Event ID of the CMMqttEventCONNACK class.
	 */
	public static final int CONNACK = 2;
	
	/**
	 * Event ID of the CMMqttEventPUBLISH class.
	 */
	public static final int PUBLISH = 3;
	
	/**
	 * Event ID of the CMMqttEventPUBACK class.
	 */
	public static final int PUBACK = 4;
	
	/**
	 * Event ID of the CMMqttEventPUBREC class.
	 */
	public static final int PUBREC = 5;
	
	/**
	 * Event ID of the CMMqttEventPUBREL class.
	 */
	public static final int PUBREL = 6;
	
	/**
	 * Event ID of the CMMqttEventPUBCOMP class.
	 */
	public static final int PUBCOMP = 7;
	
	/**
	 * Event ID of the CMMqttEventSUBSCRIBE class.
	 */
	public static final int SUBSCRIBE = 8;
	
	/**
	 * Event ID of the CMMqttEventSUBACK class.
	 */
	public static final int SUBACK = 9;
	
	/**
	 * Event ID of the CMMqttEventUNSUBSCRIBE class.
	 */
	public static final int UNSUBSCRIBE = 10;
	
	/**
	 * Event ID of the CMMqttEventUNSUBACK class.
	 */
	public static final int UNSUBACK = 11;
	
	/**
	 * Event ID of the CMMqttEventPINGREQ class.
	 */
	public static final int PINGREQ = 12;
	
	/**
	 * Event ID of the CMMqttEventPINGRESP class.
	 */
	public static final int PINGRESP = 13;
	
	/**
	 * Event ID of the CMMqttEventDISCONNECT class.
	 */
	public static final int DISCONNECT = 14;
	
	public String m_strMqttReceiver;	// for qos 3
	public String m_strMqttSender;	// for qos 3
	
	// abstract methods
	protected abstract int getFixedHeaderByteNum();
	protected abstract void marshallFixedHeader();
	protected abstract void unmarshallFixedHeader(ByteBuffer buf);
	protected abstract int getVarHeaderByteNum();
	protected abstract void marshallVarHeader();
	protected abstract void unmarshallVarHeader(ByteBuffer buf);
	protected abstract int getPayloadByteNum();
	protected abstract void marshallPayload();
	protected abstract void unmarshallPayload(ByteBuffer buf);
	
	public CMMqttEvent()
	{
		m_nType = CMInfo.CM_MQTT_EVENT;
//		m_strMqttReceiver = "";
//		m_strMqttSender = "";
	}
	
	public CMMqttEvent(ByteBuffer msg)
	{
		this();
		unmarshall(msg);
	}
		
	/**
	 * returns the string representation of this CMMqttEvent object.
	 * @return string of this object.
	 */
	@Override
	public String toString()
	{
		return super.toString();
	}
	
	@Override
	protected int getByteNum()
	{
		int nByteNum = 0;
		int nFixedHeaderByteNum = 0;
		int nVarHeaderByteNum = 0;
		int nPayloadByteNum = 0;
		int nCMEventHeaderByteNum = 0;
		
		nCMEventHeaderByteNum = super.getByteNum();
		nVarHeaderByteNum = getVarHeaderByteNum();
		nPayloadByteNum = getPayloadByteNum();
		nByteNum += nCMEventHeaderByteNum + nVarHeaderByteNum + nPayloadByteNum;
//		nByteNum += CMInfo.STRING_LEN_BYTES_LEN + m_strMqttReceiver.getBytes().length;
//		nByteNum += CMInfo.STRING_LEN_BYTES_LEN + m_strMqttSender.getBytes().length;

		// m_nRemainLength of the fixed header is determined after getVarHeaderByteNum() and 
		// getPayloadByteNum() are completed.
		nFixedHeaderByteNum = getFixedHeaderByteNum();
		nByteNum += nFixedHeaderByteNum;

		if(CMInfo._CM_DEBUG_2)
		{
			System.out.println("CMMqttEvent.getByteNum(): cm header("+nCMEventHeaderByteNum
					+") + fixed header("+nFixedHeaderByteNum+") + var header("+nVarHeaderByteNum
					+") + payload("+nPayloadByteNum+") = "+nByteNum);
		}
		
		return nByteNum;
	}
	
	public int getByteNumTest() //for qos3 test******************8
	{
		return getByteNum();
	}
	
	@Override
	protected void marshallBody() 
	{
		// TODO Auto-generated method stub
		//여기 sender&receiver 마샬/언마샬하는 코드 추가하기
		marshallFixedHeader();
		marshallVarHeader();
		marshallPayload();
//		putStringToByteBuffer(m_strMqttReceiver);
//		putStringToByteBuffer(m_strMqttSender);
		
		return;
	}

	@Override
	protected void unmarshallBody(ByteBuffer buf) 
	{
		// TODO Auto-generated method stub
		unmarshallFixedHeader(buf);
		unmarshallVarHeader(buf);
		unmarshallPayload(buf);
//		m_strMqttReceiver = getStringFromByteBuffer(buf);
//		m_strMqttSender = getStringFromByteBuffer(buf);
		
		return;
	}
	
	public int getPacketID()
	{
		return -1;
	}
	
	public void setMqttReceiver(String uName)
	{
		if(uName != null)
			m_strMqttReceiver = uName;
	}
	
	/**
	 * Returns the receiver name of a file.
	 * @return receiver name
	 */
	public String getMqttReceiver()
	{
		return m_strMqttReceiver;
	}
	
	public void setMqttSender(String sName)
	{
		if(sName != null)
			m_strMqttSender = sName;
	}
	
	/**
	 * Returns the sender name of a file.
	 * @return sender name
	 */
	public String getMqttSender()
	{
		return m_strMqttSender;
	}
	
	@Override
	public boolean equals(Object obj) //super.equal==true면 packetid가 같은지 한번 더 체크.
	{
		boolean bRet = super.equals(obj);
		
		if(bRet) {
			CMMqttEvent cme = (CMMqttEvent) obj;
			if(cme.getPacketID() == getPacketID())
				return true;
		}
		
		
		return false;
	}

	
}
