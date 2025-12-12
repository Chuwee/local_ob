
package es.onebox.fcb.datasources.peoplesoft.wsdl.tresoreria;

import java.math.BigInteger;
import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CapcaleraExtracteBancariType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CapcaleraExtracteBancariType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="idPeticio" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="10"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="codiResposta" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="2"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="idRemitent" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="20"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="idRecepcio" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="20"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dataProcesExtracte" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *         &lt;element name="dataHoraCarga" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="idUsuari" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="30"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dataCreacioArxiu" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *         &lt;element name="horaCreacioArxiu" type="{http://www.w3.org/2001/XMLSchema}time" minOccurs="0"/&gt;
 *         &lt;element name="idArxiu" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="30"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="descripcio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="idSeguimentEvent" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/&gt;
 *         &lt;element name="idArxiuImport" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CapcaleraExtracteBancariType", propOrder = {
    "idPeticio",
    "codiResposta",
    "idRemitent",
    "idRecepcio",
    "dataProcesExtracte",
    "dataHoraCarga",
    "idUsuari",
    "dataCreacioArxiu",
    "horaCreacioArxiu",
    "idArxiu",
    "descripcio",
    "idSeguimentEvent",
    "idArxiuImport"
})
public class CapcaleraExtracteBancariType {

    protected String idPeticio;
    protected String codiResposta;
    protected String idRemitent;
    protected String idRecepcio;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dataProcesExtracte;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dataHoraCarga;
    protected String idUsuari;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dataCreacioArxiu;
    @XmlSchemaType(name = "time")
    protected XMLGregorianCalendar horaCreacioArxiu;
    protected String idArxiu;
    protected String descripcio;
    protected BigInteger idSeguimentEvent;
    protected BigInteger idArxiuImport;

    /**
     * Gets the value of the idPeticio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdPeticio() {
        return idPeticio;
    }

    /**
     * Sets the value of the idPeticio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdPeticio(String value) {
        this.idPeticio = value;
    }

    /**
     * Gets the value of the codiResposta property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodiResposta() {
        return codiResposta;
    }

    /**
     * Sets the value of the codiResposta property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodiResposta(String value) {
        this.codiResposta = value;
    }

    /**
     * Gets the value of the idRemitent property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdRemitent() {
        return idRemitent;
    }

    /**
     * Sets the value of the idRemitent property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdRemitent(String value) {
        this.idRemitent = value;
    }

    /**
     * Gets the value of the idRecepcio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdRecepcio() {
        return idRecepcio;
    }

    /**
     * Sets the value of the idRecepcio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdRecepcio(String value) {
        this.idRecepcio = value;
    }

    /**
     * Gets the value of the dataProcesExtracte property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataProcesExtracte() {
        return dataProcesExtracte;
    }

    /**
     * Sets the value of the dataProcesExtracte property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataProcesExtracte(XMLGregorianCalendar value) {
        this.dataProcesExtracte = value;
    }

    /**
     * Gets the value of the dataHoraCarga property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataHoraCarga() {
        return dataHoraCarga;
    }

    /**
     * Sets the value of the dataHoraCarga property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataHoraCarga(XMLGregorianCalendar value) {
        this.dataHoraCarga = value;
    }

    /**
     * Gets the value of the idUsuari property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdUsuari() {
        return idUsuari;
    }

    /**
     * Sets the value of the idUsuari property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdUsuari(String value) {
        this.idUsuari = value;
    }

    /**
     * Gets the value of the dataCreacioArxiu property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataCreacioArxiu() {
        return dataCreacioArxiu;
    }

    /**
     * Sets the value of the dataCreacioArxiu property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataCreacioArxiu(XMLGregorianCalendar value) {
        this.dataCreacioArxiu = value;
    }

    /**
     * Gets the value of the horaCreacioArxiu property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getHoraCreacioArxiu() {
        return horaCreacioArxiu;
    }

    /**
     * Sets the value of the horaCreacioArxiu property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setHoraCreacioArxiu(XMLGregorianCalendar value) {
        this.horaCreacioArxiu = value;
    }

    /**
     * Gets the value of the idArxiu property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdArxiu() {
        return idArxiu;
    }

    /**
     * Sets the value of the idArxiu property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdArxiu(String value) {
        this.idArxiu = value;
    }

    /**
     * Gets the value of the descripcio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescripcio() {
        return descripcio;
    }

    /**
     * Sets the value of the descripcio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescripcio(String value) {
        this.descripcio = value;
    }

    /**
     * Gets the value of the idSeguimentEvent property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getIdSeguimentEvent() {
        return idSeguimentEvent;
    }

    /**
     * Sets the value of the idSeguimentEvent property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setIdSeguimentEvent(BigInteger value) {
        this.idSeguimentEvent = value;
    }

    /**
     * Gets the value of the idArxiuImport property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getIdArxiuImport() {
        return idArxiuImport;
    }

    /**
     * Sets the value of the idArxiuImport property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setIdArxiuImport(BigInteger value) {
        this.idArxiuImport = value;
    }

}
