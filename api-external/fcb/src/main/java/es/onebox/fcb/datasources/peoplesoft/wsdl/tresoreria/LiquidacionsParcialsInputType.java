
package es.onebox.fcb.datasources.peoplesoft.wsdl.tresoreria;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for LiquidacionsParcialsInputType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="LiquidacionsParcialsInputType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="compteBancOrigen" type="{http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types}DadesCompteBancType"/&gt;
 *         &lt;element name="compteBancDesti" type="{http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types}DadesCompteBancType"/&gt;
 *         &lt;element name="transaccio" type="{http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types}DadesTransaccioType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LiquidacionsParcialsInputType", propOrder = {
    "compteBancOrigen",
    "compteBancDesti",
    "transaccio"
})
public class LiquidacionsParcialsInputType {

    @XmlElement(required = true)
    protected DadesCompteBancType compteBancOrigen;
    @XmlElement(required = true)
    protected DadesCompteBancType compteBancDesti;
    @XmlElement(required = true)
    protected DadesTransaccioType transaccio;

    /**
     * Gets the value of the compteBancOrigen property.
     * 
     * @return
     *     possible object is
     *     {@link DadesCompteBancType }
     *     
     */
    public DadesCompteBancType getCompteBancOrigen() {
        return compteBancOrigen;
    }

    /**
     * Sets the value of the compteBancOrigen property.
     * 
     * @param value
     *     allowed object is
     *     {@link DadesCompteBancType }
     *     
     */
    public void setCompteBancOrigen(DadesCompteBancType value) {
        this.compteBancOrigen = value;
    }

    /**
     * Gets the value of the compteBancDesti property.
     * 
     * @return
     *     possible object is
     *     {@link DadesCompteBancType }
     *     
     */
    public DadesCompteBancType getCompteBancDesti() {
        return compteBancDesti;
    }

    /**
     * Sets the value of the compteBancDesti property.
     * 
     * @param value
     *     allowed object is
     *     {@link DadesCompteBancType }
     *     
     */
    public void setCompteBancDesti(DadesCompteBancType value) {
        this.compteBancDesti = value;
    }

    /**
     * Gets the value of the transaccio property.
     * 
     * @return
     *     possible object is
     *     {@link DadesTransaccioType }
     *     
     */
    public DadesTransaccioType getTransaccio() {
        return transaccio;
    }

    /**
     * Sets the value of the transaccio property.
     * 
     * @param value
     *     allowed object is
     *     {@link DadesTransaccioType }
     *     
     */
    public void setTransaccio(DadesTransaccioType value) {
        this.transaccio = value;
    }

}
