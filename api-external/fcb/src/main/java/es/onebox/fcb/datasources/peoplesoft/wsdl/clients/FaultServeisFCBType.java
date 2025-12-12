
package es.onebox.fcb.datasources.peoplesoft.wsdl.clients;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FaultServeisFCBType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="FaultServeisFCBType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="codiError" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="descripcioError" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="sistema" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="causa" type="{http://fcbarcelona.cat/esquema/Fault/v01}FaultServeisFCBType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FaultServeisFCBType", namespace = "http://fcbarcelona.cat/esquema/Fault/v01", propOrder = {
    "codiError",
    "descripcioError",
    "sistema",
    "causa"
})
public class FaultServeisFCBType {

    @XmlElement(required = true)
    protected String codiError;
    @XmlElement(required = true)
    protected String descripcioError;
    @XmlElement(required = true)
    protected String sistema;
    protected FaultServeisFCBType causa;

    /**
     * Gets the value of the codiError property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodiError() {
        return codiError;
    }

    /**
     * Sets the value of the codiError property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodiError(String value) {
        this.codiError = value;
    }

    /**
     * Gets the value of the descripcioError property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescripcioError() {
        return descripcioError;
    }

    /**
     * Sets the value of the descripcioError property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescripcioError(String value) {
        this.descripcioError = value;
    }

    /**
     * Gets the value of the sistema property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSistema() {
        return sistema;
    }

    /**
     * Sets the value of the sistema property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSistema(String value) {
        this.sistema = value;
    }

    /**
     * Gets the value of the causa property.
     * 
     * @return
     *     possible object is
     *     {@link FaultServeisFCBType }
     *     
     */
    public FaultServeisFCBType getCausa() {
        return causa;
    }

    /**
     * Sets the value of the causa property.
     * 
     * @param value
     *     allowed object is
     *     {@link FaultServeisFCBType }
     *     
     */
    public void setCausa(FaultServeisFCBType value) {
        this.causa = value;
    }

}
