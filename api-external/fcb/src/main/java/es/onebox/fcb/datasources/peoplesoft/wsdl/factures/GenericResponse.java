
package es.onebox.fcb.datasources.peoplesoft.wsdl.factures;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GenericResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GenericResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="estat" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="descripcioEstat" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="messageId" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GenericResponse", propOrder = {
    "estat",
    "descripcioEstat",
    "messageId"
})
@XmlSeeAlso({
    RespostaFacturar.class
})
public class GenericResponse {

    @XmlElement(required = true)
    protected String estat;
    @XmlElement(required = true)
    protected String descripcioEstat;
    @XmlElement(required = true)
    protected String messageId;

    /**
     * Gets the value of the estat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEstat() {
        return estat;
    }

    /**
     * Sets the value of the estat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEstat(String value) {
        this.estat = value;
    }

    /**
     * Gets the value of the descripcioEstat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescripcioEstat() {
        return descripcioEstat;
    }

    /**
     * Sets the value of the descripcioEstat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescripcioEstat(String value) {
        this.descripcioEstat = value;
    }

    /**
     * Gets the value of the messageId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * Sets the value of the messageId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessageId(String value) {
        this.messageId = value;
    }

}
