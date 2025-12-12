
package es.onebox.fcb.datasources.peoplesoft.wsdl.clients;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DadesTargeta complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DadesTargeta"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="tipus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="numero" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="nomTitular" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="cognomTitular" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="mesExpiracio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="anyExpiracio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DadesTargeta", propOrder = {
    "tipus",
    "numero",
    "nomTitular",
    "cognomTitular",
    "mesExpiracio",
    "anyExpiracio"
})
public class DadesTargeta {

    protected String tipus;
    protected String numero;
    protected String nomTitular;
    protected String cognomTitular;
    protected String mesExpiracio;
    protected String anyExpiracio;

    /**
     * Gets the value of the tipus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipus() {
        return tipus;
    }

    /**
     * Sets the value of the tipus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipus(String value) {
        this.tipus = value;
    }

    /**
     * Gets the value of the numero property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumero() {
        return numero;
    }

    /**
     * Sets the value of the numero property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumero(String value) {
        this.numero = value;
    }

    /**
     * Gets the value of the nomTitular property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNomTitular() {
        return nomTitular;
    }

    /**
     * Sets the value of the nomTitular property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNomTitular(String value) {
        this.nomTitular = value;
    }

    /**
     * Gets the value of the cognomTitular property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCognomTitular() {
        return cognomTitular;
    }

    /**
     * Sets the value of the cognomTitular property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCognomTitular(String value) {
        this.cognomTitular = value;
    }

    /**
     * Gets the value of the mesExpiracio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMesExpiracio() {
        return mesExpiracio;
    }

    /**
     * Sets the value of the mesExpiracio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMesExpiracio(String value) {
        this.mesExpiracio = value;
    }

    /**
     * Gets the value of the anyExpiracio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAnyExpiracio() {
        return anyExpiracio;
    }

    /**
     * Sets the value of the anyExpiracio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAnyExpiracio(String value) {
        this.anyExpiracio = value;
    }

}
