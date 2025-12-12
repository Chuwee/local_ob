
package es.onebox.fcb.datasources.peoplesoft.wsdl.tresoreria;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ResultatOperacioDipositsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ResultatOperacioDipositsType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="codiResultat" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="descripcioError" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResultatOperacioDipositsType", propOrder = {
    "codiResultat",
    "descripcioError"
})
public class ResultatOperacioDipositsType {

    @XmlElement(required = true)
    protected String codiResultat;
    @XmlElement(required = true)
    protected String descripcioError;

    /**
     * Gets the value of the codiResultat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodiResultat() {
        return codiResultat;
    }

    /**
     * Sets the value of the codiResultat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodiResultat(String value) {
        this.codiResultat = value;
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

}
