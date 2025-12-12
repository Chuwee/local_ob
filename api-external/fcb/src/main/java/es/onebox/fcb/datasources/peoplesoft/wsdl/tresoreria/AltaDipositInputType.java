
package es.onebox.fcb.datasources.peoplesoft.wsdl.tresoreria;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AltaDipositInputType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AltaDipositInputType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="compteBancDiposit" type="{http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types}DadesCompteBancDipositType"/&gt;
 *         &lt;element name="sistemesExterns" type="{http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types}SistemesExternsType"/&gt;
 *         &lt;element name="diposit" type="{http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types}DadesDipositType"/&gt;
 *         &lt;element name="cobraments"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="cobrament" type="{http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types}CobramentType" maxOccurs="unbounded"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
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
@XmlType(name = "AltaDipositInputType", propOrder = {
    "compteBancDiposit",
    "sistemesExterns",
    "diposit",
    "cobraments"
})
public class AltaDipositInputType {

    @XmlElement(required = true)
    protected DadesCompteBancDipositType compteBancDiposit;
    @XmlElement(required = true)
    protected SistemesExternsType sistemesExterns;
    @XmlElement(required = true)
    protected DadesDipositType diposit;
    @XmlElement(required = true)
    protected AltaDipositInputType.Cobraments cobraments;

    /**
     * Gets the value of the compteBancDiposit property.
     * 
     * @return
     *     possible object is
     *     {@link DadesCompteBancDipositType }
     *     
     */
    public DadesCompteBancDipositType getCompteBancDiposit() {
        return compteBancDiposit;
    }

    /**
     * Sets the value of the compteBancDiposit property.
     * 
     * @param value
     *     allowed object is
     *     {@link DadesCompteBancDipositType }
     *     
     */
    public void setCompteBancDiposit(DadesCompteBancDipositType value) {
        this.compteBancDiposit = value;
    }

    /**
     * Gets the value of the sistemesExterns property.
     * 
     * @return
     *     possible object is
     *     {@link SistemesExternsType }
     *     
     */
    public SistemesExternsType getSistemesExterns() {
        return sistemesExterns;
    }

    /**
     * Sets the value of the sistemesExterns property.
     * 
     * @param value
     *     allowed object is
     *     {@link SistemesExternsType }
     *     
     */
    public void setSistemesExterns(SistemesExternsType value) {
        this.sistemesExterns = value;
    }

    /**
     * Gets the value of the diposit property.
     * 
     * @return
     *     possible object is
     *     {@link DadesDipositType }
     *     
     */
    public DadesDipositType getDiposit() {
        return diposit;
    }

    /**
     * Sets the value of the diposit property.
     * 
     * @param value
     *     allowed object is
     *     {@link DadesDipositType }
     *     
     */
    public void setDiposit(DadesDipositType value) {
        this.diposit = value;
    }

    /**
     * Gets the value of the cobraments property.
     * 
     * @return
     *     possible object is
     *     {@link AltaDipositInputType.Cobraments }
     *     
     */
    public AltaDipositInputType.Cobraments getCobraments() {
        return cobraments;
    }

    /**
     * Sets the value of the cobraments property.
     * 
     * @param value
     *     allowed object is
     *     {@link AltaDipositInputType.Cobraments }
     *     
     */
    public void setCobraments(AltaDipositInputType.Cobraments value) {
        this.cobraments = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element name="cobrament" type="{http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types}CobramentType" maxOccurs="unbounded"/&gt;
     *       &lt;/sequence&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "cobrament"
    })
    public static class Cobraments {

        @XmlElement(required = true)
        protected List<CobramentType> cobrament;

        /**
         * Gets the value of the cobrament property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the Jakarta XML Binding object.
         * This is why there is not a <CODE>set</CODE> method for the cobrament property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getCobrament().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link CobramentType }
         * 
         * 
         */
        public List<CobramentType> getCobrament() {
            if (cobrament == null) {
                cobrament = new ArrayList<CobramentType>();
            }
            return this.cobrament;
        }

    }

}
