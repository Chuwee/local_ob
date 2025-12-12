
package es.onebox.fcb.datasources.peoplesoft.wsdl.tresoreria;

import java.math.BigDecimal;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ReferenciaType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReferenciaType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="codCalificacio" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="ordreCompra"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="20"/&gt;
 *               &lt;whiteSpace value="preserve"/&gt;
 *               &lt;pattern value="([A-Z]|[0-9]|\p{Z}|\p{P}|\p{Lu})*"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="importUsuari"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}decimal"&gt;
 *               &lt;fractionDigits value="3"/&gt;
 *               &lt;totalDigits value="28"/&gt;
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
@XmlType(name = "ReferenciaType", propOrder = {
    "codCalificacio",
    "ordreCompra",
    "importUsuari"
})
public class ReferenciaType {

    @XmlElement(required = true)
    protected String codCalificacio;
    @XmlElement(required = true)
    protected String ordreCompra;
    @XmlElement(required = true)
    protected BigDecimal importUsuari;

    /**
     * Gets the value of the codCalificacio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodCalificacio() {
        return codCalificacio;
    }

    /**
     * Sets the value of the codCalificacio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodCalificacio(String value) {
        this.codCalificacio = value;
    }

    /**
     * Gets the value of the ordreCompra property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrdreCompra() {
        return ordreCompra;
    }

    /**
     * Sets the value of the ordreCompra property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrdreCompra(String value) {
        this.ordreCompra = value;
    }

    /**
     * Gets the value of the importUsuari property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getImportUsuari() {
        return importUsuari;
    }

    /**
     * Sets the value of the importUsuari property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setImportUsuari(BigDecimal value) {
        this.importUsuari = value;
    }

}
