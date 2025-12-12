
package es.onebox.fcb.datasources.peoplesoft.wsdl.tresoreria;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DadesCompteBancType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DadesCompteBancType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="idBanc"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="20"/&gt;
 *               &lt;whiteSpace value="preserve"/&gt;
 *               &lt;pattern value="([A-Z]|[0-9]|\p{Z}|\p{P}|\p{Lu})*"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="numCompteBanc"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="15"/&gt;
 *               &lt;whiteSpace value="preserve"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DadesCompteBancType", propOrder = {
    "idBanc",
    "numCompteBanc"
})
public class DadesCompteBancType {

    @XmlElement(required = true)
    protected String idBanc;
    @XmlElement(required = true)
    protected String numCompteBanc;

    /**
     * Gets the value of the idBanc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdBanc() {
        return idBanc;
    }

    /**
     * Sets the value of the idBanc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdBanc(String value) {
        this.idBanc = value;
    }

    /**
     * Gets the value of the numCompteBanc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumCompteBanc() {
        return numCompteBanc;
    }

    /**
     * Sets the value of the numCompteBanc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumCompteBanc(String value) {
        this.numCompteBanc = value;
    }

}
