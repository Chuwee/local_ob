package es.onebox.mgmt.datasources.ms.crm.dto;

public enum SubscripcionListStatus {
	BORRADA((byte)0),
	ACTIVA((byte)1);

	private byte estado;

	SubscripcionListStatus(byte estado) {
		this.estado = estado;
	}

	public byte getEstado() {
		return estado;
	}

}
