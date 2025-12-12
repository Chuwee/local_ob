
package es.onebox.fcb.datasources.peoplesoft.wsdl.tresoreria;

import java.math.BigDecimal;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CobramentType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CobramentType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="idCobrament"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="20"/&gt;
 *               &lt;whiteSpace value="preserve"/&gt;
 *               &lt;pattern value="([A-Z]|[0-9]|\p{Z}|\p{P}|\p{Lu})*"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="importCobrament"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}decimal"&gt;
 *               &lt;fractionDigits value="3"/&gt;
 *               &lt;totalDigits value="28"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="metodePagament"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="20"/&gt;
 *               &lt;whiteSpace value="preserve"/&gt;
 *               &lt;pattern value="([A-Z]|[0-9]|\p{Z}|\p{P}|\p{Lu})*"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="clientsSistemaOrigen" type="{http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types}ClientsSistemaOrigenType" minOccurs="0"/&gt;
 *         &lt;element name="referencies" type="{http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types}ReferenciesType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CobramentType", propOrder = {
    "idCobrament",
    "importCobrament",
    "metodePagament",
    "clientsSistemaOrigen",
    "referencies"
})
public class CobramentType {

    @XmlElement(required = true)
    protected String idCobrament;
    @XmlElement(required = true)
    protected BigDecimal importCobrament;
    @XmlElement(required = true)
    protected String metodePagament;
    protected ClientsSistemaOrigenType clientsSistemaOrigen;
    protected ReferenciesType referencies;

    /**
     * Gets the value of the idCobrament property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdCobrament() {
        return idCobrament;
    }

    /**
     * Sets the value of the idCobrament property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdCobrament(String value) {
        this.idCobrament = value;
    }

    /**
     * Gets the value of the importCobrament property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getImportCobrament() {
        return importCobrament;
    }

    /**
     * Sets the value of the importCobrament property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setImportCobrament(BigDecimal value) {
        this.importCobrament = value;
    }

    /**
     * Gets the value of the metodePagament property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMetodePagament() {
        return metodePagament;
    }

    /**
     * Sets the value of the metodePagament property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMetodePagament(String value) {
        this.metodePagament = value;
    }

    /**
     * Gets the value of the clientsSistemaOrigen property.
     * 
     * @return
     *     possible object is
     *     {@link ClientsSistemaOrigenType }
     *     
     */
    public ClientsSistemaOrigenType getClientsSistemaOrigen() {
        return clientsSistemaOrigen;
    }

    /**
     * Sets the value of the clientsSistemaOrigen property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClientsSistemaOrigenType }
     *     
     */
    public void setClientsSistemaOrigen(ClientsSistemaOrigenType value) {
        this.clientsSistemaOrigen = value;
    }

    /**
     * Gets the value of the referencies property.
     * 
     * @return
     *     possible object is
     *     {@link ReferenciesType }
     *     
     */
    public ReferenciesType getReferencies() {
        return referencies;
    }

    /**
     * Sets the value of the referencies property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReferenciesType }
     *     
     */
    public void setReferencies(ReferenciesType value) {
        this.referencies = value;
    }

}
