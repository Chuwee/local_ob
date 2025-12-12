
package es.onebox.fcb.datasources.peoplesoft.wsdl.tresoreria;

import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CapcaleraComptaBancariaType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CapcaleraComptaBancariaType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="idBancExtern"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="20"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="idSucursal"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="10"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="numCompteBancaria"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="35"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dataRef" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *         &lt;element name="horaRef" type="{http://www.w3.org/2001/XMLSchema}time" minOccurs="0"/&gt;
 *         &lt;element name="codMoneda" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CapcaleraComptaBancariaType", propOrder = {
    "idBancExtern",
    "idSucursal",
    "numCompteBancaria",
    "dataRef",
    "horaRef",
    "codMoneda"
})
public class CapcaleraComptaBancariaType {

    @XmlElement(required = true)
    protected String idBancExtern;
    @XmlElement(required = true)
    protected String idSucursal;
    @XmlElement(required = true)
    protected String numCompteBancaria;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dataRef;
    @XmlSchemaType(name = "time")
    protected XMLGregorianCalendar horaRef;
    protected String codMoneda;

    /**
     * Gets the value of the idBancExtern property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdBancExtern() {
        return idBancExtern;
    }

    /**
     * Sets the value of the idBancExtern property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdBancExtern(String value) {
        this.idBancExtern = value;
    }

    /**
     * Gets the value of the idSucursal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdSucursal() {
        return idSucursal;
    }

    /**
     * Sets the value of the idSucursal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdSucursal(String value) {
        this.idSucursal = value;
    }

    /**
     * Gets the value of the numCompteBancaria property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumCompteBancaria() {
        return numCompteBancaria;
    }

    /**
     * Sets the value of the numCompteBancaria property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumCompteBancaria(String value) {
        this.numCompteBancaria = value;
    }

    /**
     * Gets the value of the dataRef property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataRef() {
        return dataRef;
    }

    /**
     * Sets the value of the dataRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataRef(XMLGregorianCalendar value) {
        this.dataRef = value;
    }

    /**
     * Gets the value of the horaRef property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getHoraRef() {
        return horaRef;
    }

    /**
     * Sets the value of the horaRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setHoraRef(XMLGregorianCalendar value) {
        this.horaRef = value;
    }

    /**
     * Gets the value of the codMoneda property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodMoneda() {
        return codMoneda;
    }

    /**
     * Sets the value of the codMoneda property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodMoneda(String value) {
        this.codMoneda = value;
    }

}
