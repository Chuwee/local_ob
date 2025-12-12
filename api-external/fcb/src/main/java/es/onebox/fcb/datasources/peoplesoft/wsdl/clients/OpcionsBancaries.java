
package es.onebox.fcb.datasources.peoplesoft.wsdl.clients;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for OpcionsBancaries complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OpcionsBancaries"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="cobrador" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="analistaCredit" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="especialistaCobrament" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="metodePagament" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="condicionsPagament" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="idBanc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="sucursal" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="codiVerificacio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="compte" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="IBAN" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="bicSwift" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OpcionsBancaries", propOrder = {
    "cobrador",
    "analistaCredit",
    "especialistaCobrament",
    "metodePagament",
    "condicionsPagament",
    "idBanc",
    "sucursal",
    "codiVerificacio",
    "compte",
    "iban",
    "bicSwift"
})
public class OpcionsBancaries {

    protected String cobrador;
    protected String analistaCredit;
    protected String especialistaCobrament;
    protected String metodePagament;
    protected String condicionsPagament;
    protected String idBanc;
    protected String sucursal;
    protected String codiVerificacio;
    protected String compte;
    @XmlElement(name = "IBAN")
    protected String iban;
    protected String bicSwift;

    /**
     * Gets the value of the cobrador property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCobrador() {
        return cobrador;
    }

    /**
     * Sets the value of the cobrador property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCobrador(String value) {
        this.cobrador = value;
    }

    /**
     * Gets the value of the analistaCredit property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAnalistaCredit() {
        return analistaCredit;
    }

    /**
     * Sets the value of the analistaCredit property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAnalistaCredit(String value) {
        this.analistaCredit = value;
    }

    /**
     * Gets the value of the especialistaCobrament property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEspecialistaCobrament() {
        return especialistaCobrament;
    }

    /**
     * Sets the value of the especialistaCobrament property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEspecialistaCobrament(String value) {
        this.especialistaCobrament = value;
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
     * Gets the value of the condicionsPagament property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCondicionsPagament() {
        return condicionsPagament;
    }

    /**
     * Sets the value of the condicionsPagament property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCondicionsPagament(String value) {
        this.condicionsPagament = value;
    }

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
     * Gets the value of the sucursal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSucursal() {
        return sucursal;
    }

    /**
     * Sets the value of the sucursal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSucursal(String value) {
        this.sucursal = value;
    }

    /**
     * Gets the value of the codiVerificacio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodiVerificacio() {
        return codiVerificacio;
    }

    /**
     * Sets the value of the codiVerificacio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodiVerificacio(String value) {
        this.codiVerificacio = value;
    }

    /**
     * Gets the value of the compte property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCompte() {
        return compte;
    }

    /**
     * Sets the value of the compte property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCompte(String value) {
        this.compte = value;
    }

    /**
     * Gets the value of the iban property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIBAN() {
        return iban;
    }

    /**
     * Sets the value of the iban property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIBAN(String value) {
        this.iban = value;
    }

    /**
     * Gets the value of the bicSwift property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBicSwift() {
        return bicSwift;
    }

    /**
     * Sets the value of the bicSwift property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBicSwift(String value) {
        this.bicSwift = value;
    }

}
