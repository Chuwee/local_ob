
package es.onebox.fcb.datasources.peoplesoft.wsdl.tresoreria;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for OberturaCaixaInputType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OberturaCaixaInputType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="compteBanc" type="{http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types}DadesCompteBancType"/&gt;
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
@XmlType(name = "OberturaCaixaInputType", propOrder = {
    "compteBanc",
    "transaccio"
})
public class OberturaCaixaInputType {

    @XmlElement(required = true)
    protected DadesCompteBancType compteBanc;
    @XmlElement(required = true)
    protected DadesTransaccioType transaccio;

    /**
     * Gets the value of the compteBanc property.
     * 
     * @return
     *     possible object is
     *     {@link DadesCompteBancType }
     *     
     */
    public DadesCompteBancType getCompteBanc() {
        return compteBanc;
    }

    /**
     * Sets the value of the compteBanc property.
     * 
     * @param value
     *     allowed object is
     *     {@link DadesCompteBancType }
     *     
     */
    public void setCompteBanc(DadesCompteBancType value) {
        this.compteBanc = value;
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
