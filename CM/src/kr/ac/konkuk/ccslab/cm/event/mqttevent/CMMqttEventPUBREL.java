package kr.ac.konkuk.ccslab.cm.event.mqttevent;

import java.nio.ByteBuffer;

import kr.ac.konkuk.ccslab.cm.info.CMInfo;

/**
 * This class represents a CM event that is the variable header and payload of 
 * MQTT PUBREL packet.
 * @author CCSLab, Konkuk University
 * @see <a href="http://docs.oasis-open.org/mqtt/mqtt/v3.1.1/os/mqtt-v3.1.1-os.html#_Toc398718053">
 * http://docs.oasis-open.org/mqtt/mqtt/v3.1.1/os/mqtt-v3.1.1-os.html#_Toc398718053</a>
 */
public class CMMqttEventPUBREL extends CMMqttEventFixedHeader {

	//////////////////////////////////////////////////
	// member variables (variable header)
	int m_nPacketID;
	byte m_qos;	

	//////////////////////////////////////////////////
	// constructors

	/**
	 * Creates an instance of the CMMqttEventPUBREL class.
	 */
	public CMMqttEventPUBREL()
	{
		// initialize CM event ID
		m_nID = CMMqttEvent.PUBREL;
		// initialize fixed header
		m_packetType = CMMqttEvent.PUBREL;
		m_flag = 2;
		// initialize variable header
		m_nPacketID = 0;
		m_qos = 0;
		m_strMqttReceiver = "";
		m_strMqttSender = "";
	}
	
	public CMMqttEventPUBREL(ByteBuffer msg)
	{
		this();
		unmarshall(msg);
	}

	//////////////////////////////////////////////////
	// setter/getter (variable header)

	/**
	 * sets MQTT Packet ID.
	 * @param nID - Packet ID.
	 */
	public void setPacketID(int nID)
	{
		m_nPacketID = nID;
	}
	
	/**
	 * gets MQTT Packet ID.
	 * @return Packet ID.
	 */
	@Override
	public int getPacketID()
	{
		return m_nPacketID;
	}
	
	public byte getQos() {
		return m_qos;
	}

	public void setQos(byte m_qos) {
		this.m_qos = m_qos;
	}

	//////////////////////////////////////////////////
	// overridden methods (variable header)
	
	@Override
	protected int getVarHeaderByteNum()
	{
//		return 2;	// packet ID
		int nByteNum = 0;
		nByteNum += 1;  // qos
		nByteNum += 2;	// packet identifier
		nByteNum += CMInfo.STRING_LEN_BYTES_LEN + m_strMqttReceiver.getBytes().length;
		nByteNum += CMInfo.STRING_LEN_BYTES_LEN + m_strMqttSender.getBytes().length;

		return nByteNum;
	}

	@Override
	protected void marshallVarHeader()
	{
		putInt2BytesToByteBuffer(m_nPacketID);
		m_bytes.put(m_qos);
		putStringToByteBuffer(m_strMqttReceiver);
		putStringToByteBuffer(m_strMqttSender);
	}

	@Override
	protected void unmarshallVarHeader(ByteBuffer buf)
	{
		m_nPacketID = getInt2BytesFromByteBuffer(buf);
		m_qos = buf.get();
		m_strMqttReceiver = getStringFromByteBuffer(buf);
		m_strMqttSender = getStringFromByteBuffer(buf);
	}

	//////////////////////////////////////////////////
	// overridden methods (payload)

	// No payload in this packet

	@Override
	protected int getPayloadByteNum()
	{
		return 0;
	}

	@Override
	protected void marshallPayload(){}

	@Override
	protected void unmarshallPayload(ByteBuffer buf){}

	//////////////////////////////////////////////////
	// overridden methods
	
	public String toString()
	{
		StringBuffer strBufVarHeader = new StringBuffer();
		strBufVarHeader.append("CMMqttEventPUBREL {");
		strBufVarHeader.append(super.toString()+", ");
		strBufVarHeader.append("\"packetID\": "+m_nPacketID);
		strBufVarHeader.append("}");
		
		return strBufVarHeader.toString();
	}

}
