
package es.onebox.fcb.datasources.peoplesoft.wsdl.factures;

import java.math.BigDecimal;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
 * <p>Java class for PeticioFacturar complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PeticioFacturar"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="totalEntrades" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="generarFactura" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="importTotal" type="{http://www.w3.org/2001/XMLSchema}decimal"/&gt;
 *         &lt;element name="sistemaOrigen" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="messageId" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="linies" type="{http://fcbarcelona.cat/servei/Finances/Factures/v01/types}ArrayOfLinia"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PeticioFacturar", propOrder = {
    "totalEntrades",
    "generarFactura",
    "importTotal",
    "sistemaOrigen",
    "messageId",
    "linies"
})
public class PeticioFacturar {

    protected int totalEntrades;
    @XmlElement(required = true)
    protected String generarFactura;
    @XmlElement(required = true)
    protected BigDecimal importTotal;
    @XmlElement(required = true)
    protected String sistemaOrigen;
    @XmlElement(required = true)
    protected String messageId;
    @XmlElement(required = true)
    protected ArrayOfLinia linies;

    /**
     * Gets the value of the totalEntrades property.
     * 
     */
    public int getTotalEntrades() {
        return totalEntrades;
    }

    /**
     * Sets the value of the totalEntrades property.
     * 
     */
    public void setTotalEntrades(int value) {
        this.totalEntrades = value;
    }

    /**
     * Gets the value of the generarFactura property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGenerarFactura() {
        return generarFactura;
    }

    /**
     * Sets the value of the generarFactura property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGenerarFactura(String value) {
        this.generarFactura = value;
    }

    /**
     * Gets the value of the importTotal property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getImportTotal() {
        return importTotal;
    }

    /**
     * Sets the value of the importTotal property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setImportTotal(BigDecimal value) {
        this.importTotal = value;
    }

    /**
     * Gets the value of the sistemaOrigen property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSistemaOrigen() {
        return sistemaOrigen;
    }

    /**
     * Sets the value of the sistemaOrigen property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSistemaOrigen(String value) {
        this.sistemaOrigen = value;
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

    /**
     * Gets the value of the linies property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfLinia }
     *     
     */
    public ArrayOfLinia getLinies() {
        return linies;
    }

    /**
     * Sets the value of the linies property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfLinia }
     *     
     */
    public void setLinies(ArrayOfLinia value) {
        this.linies = value;
    }


    @Override //TODO pipe - dont merge this
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
