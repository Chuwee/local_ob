
package es.onebox.fcb.datasources.peoplesoft.wsdl.tresoreria;

import java.math.BigInteger;
import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DadesDipositType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DadesDipositType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="idDiposit"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="20"/&gt;
 *               &lt;whiteSpace value="preserve"/&gt;
 *               &lt;pattern value="([A-Z]|[0-9]|\p{Z}|\p{P}|\p{Lu})*"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="refConciliacio"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="20"/&gt;
 *               &lt;whiteSpace value="preserve"/&gt;
 *               &lt;pattern value="([A-Z]|[0-9]|\p{Z}|\p{P}|\p{Lu})*"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dataContable" type="{http://www.w3.org/2001/XMLSchema}date"/&gt;
 *         &lt;element name="dataRecepcio" type="{http://www.w3.org/2001/XMLSchema}date"/&gt;
 *         &lt;element name="totalLiniesDiposit" type="{http://www.w3.org/2001/XMLSchema}integer"/&gt;
 *         &lt;element name="dadesImportDiposit" type="{http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types}DadesImportDipositType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DadesDipositType", propOrder = {
    "idDiposit",
    "refConciliacio",
    "dataContable",
    "dataRecepcio",
    "totalLiniesDiposit",
    "dadesImportDiposit"
})
public class DadesDipositType {

    @XmlElement(required = true)
    protected String idDiposit;
    @XmlElement(required = true)
    protected String refConciliacio;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dataContable;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dataRecepcio;
    @XmlElement(required = true)
    protected BigInteger totalLiniesDiposit;
    @XmlElement(required = true)
    protected DadesImportDipositType dadesImportDiposit;

    /**
     * Gets the value of the idDiposit property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdDiposit() {
        return idDiposit;
    }

    /**
     * Sets the value of the idDiposit property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdDiposit(String value) {
        this.idDiposit = value;
    }

    /**
     * Gets the value of the refConciliacio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRefConciliacio() {
        return refConciliacio;
    }

    /**
     * Sets the value of the refConciliacio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRefConciliacio(String value) {
        this.refConciliacio = value;
    }

    /**
     * Gets the value of the dataContable property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataContable() {
        return dataContable;
    }

    /**
     * Sets the value of the dataContable property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataContable(XMLGregorianCalendar value) {
        this.dataContable = value;
    }

    /**
     * Gets the value of the dataRecepcio property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataRecepcio() {
        return dataRecepcio;
    }

    /**
     * Sets the value of the dataRecepcio property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataRecepcio(XMLGregorianCalendar value) {
        this.dataRecepcio = value;
    }

    /**
     * Gets the value of the totalLiniesDiposit property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getTotalLiniesDiposit() {
        return totalLiniesDiposit;
    }

    /**
     * Sets the value of the totalLiniesDiposit property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setTotalLiniesDiposit(BigInteger value) {
        this.totalLiniesDiposit = value;
    }

    /**
     * Gets the value of the dadesImportDiposit property.
     * 
     * @return
     *     possible object is
     *     {@link DadesImportDipositType }
     *     
     */
    public DadesImportDipositType getDadesImportDiposit() {
        return dadesImportDiposit;
    }

    /**
     * Sets the value of the dadesImportDiposit property.
     * 
     * @param value
     *     allowed object is
     *     {@link DadesImportDipositType }
     *     
     */
    public void setDadesImportDiposit(DadesImportDipositType value) {
        this.dadesImportDiposit = value;
    }

}
