
package es.onebox.fcb.datasources.peoplesoft.wsdl.clients;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DadesAltaClient complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DadesAltaClient"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="canalVenda" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="tipus" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="idOrigen" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="nom" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="nomComercial" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="codiMoneda" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="tipusCanviMoneda" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="clientEnviament" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="clientFacturacio" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="clientVenda" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="idOperadorOrigen" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="dadesContacte" type="{http://fcbarcelona.cat/servei/Vendes/Clients/v01/types}DadesContacte"/&gt;
 *         &lt;element name="dadesTargeta" type="{http://fcbarcelona.cat/servei/Vendes/Clients/v01/types}DadesTargeta"/&gt;
 *         &lt;element name="adreca" type="{http://fcbarcelona.cat/servei/Vendes/Clients/v01/types}Adreca"/&gt;
 *         &lt;element name="opcionsBancaries" type="{http://fcbarcelona.cat/servei/Vendes/Clients/v01/types}OpcionsBancaries" minOccurs="0"/&gt;
 *         &lt;element name="dadesIVA" type="{http://fcbarcelona.cat/servei/Vendes/Clients/v01/types}DadesIVA" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DadesAltaClient", propOrder = {
    "canalVenda",
    "tipus",
    "idOrigen",
    "nom",
    "nomComercial",
    "codiMoneda",
    "tipusCanviMoneda",
    "clientEnviament",
    "clientFacturacio",
    "clientVenda",
    "idOperadorOrigen",
    "dadesContacte",
    "dadesTargeta",
    "adreca",
    "opcionsBancaries",
    "dadesIVA"
})
public class DadesAltaClient {

    @XmlElement(required = true)
    protected String canalVenda;
    @XmlElement(required = true)
    protected String tipus;
    @XmlElement(required = true)
    protected String idOrigen;
    @XmlElement(required = true)
    protected String nom;
    protected String nomComercial;
    protected String codiMoneda;
    protected String tipusCanviMoneda;
    protected boolean clientEnviament;
    protected boolean clientFacturacio;
    protected boolean clientVenda;
    @XmlElement(required = true)
    protected String idOperadorOrigen;
    @XmlElement(required = true)
    protected DadesContacte dadesContacte;
    @XmlElement(required = true)
    protected DadesTargeta dadesTargeta;
    @XmlElement(required = true)
    protected Adreca adreca;
    protected OpcionsBancaries opcionsBancaries;
    protected DadesIVA dadesIVA;

    /**
     * Gets the value of the canalVenda property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCanalVenda() {
        return canalVenda;
    }

    /**
     * Sets the value of the canalVenda property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCanalVenda(String value) {
        this.canalVenda = value;
    }

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
     * Gets the value of the idOrigen property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdOrigen() {
        return idOrigen;
    }

    /**
     * Sets the value of the idOrigen property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdOrigen(String value) {
        this.idOrigen = value;
    }

    /**
     * Gets the value of the nom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNom() {
        return nom;
    }

    /**
     * Sets the value of the nom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNom(String value) {
        this.nom = value;
    }

    /**
     * Gets the value of the nomComercial property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNomComercial() {
        return nomComercial;
    }

    /**
     * Sets the value of the nomComercial property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNomComercial(String value) {
        this.nomComercial = value;
    }

    /**
     * Gets the value of the codiMoneda property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodiMoneda() {
        return codiMoneda;
    }

    /**
     * Sets the value of the codiMoneda property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodiMoneda(String value) {
        this.codiMoneda = value;
    }

    /**
     * Gets the value of the tipusCanviMoneda property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipusCanviMoneda() {
        return tipusCanviMoneda;
    }

    /**
     * Sets the value of the tipusCanviMoneda property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipusCanviMoneda(String value) {
        this.tipusCanviMoneda = value;
    }

    /**
     * Gets the value of the clientEnviament property.
     * 
     */
    public boolean isClientEnviament() {
        return clientEnviament;
    }

    /**
     * Sets the value of the clientEnviament property.
     * 
     */
    public void setClientEnviament(boolean value) {
        this.clientEnviament = value;
    }

    /**
     * Gets the value of the clientFacturacio property.
     * 
     */
    public boolean isClientFacturacio() {
        return clientFacturacio;
    }

    /**
     * Sets the value of the clientFacturacio property.
     * 
     */
    public void setClientFacturacio(boolean value) {
        this.clientFacturacio = value;
    }

    /**
     * Gets the value of the clientVenda property.
     * 
     */
    public boolean isClientVenda() {
        return clientVenda;
    }

    /**
     * Sets the value of the clientVenda property.
     * 
     */
    public void setClientVenda(boolean value) {
        this.clientVenda = value;
    }

    /**
     * Gets the value of the idOperadorOrigen property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdOperadorOrigen() {
        return idOperadorOrigen;
    }

    /**
     * Sets the value of the idOperadorOrigen property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdOperadorOrigen(String value) {
        this.idOperadorOrigen = value;
    }

    /**
     * Gets the value of the dadesContacte property.
     * 
     * @return
     *     possible object is
     *     {@link DadesContacte }
     *     
     */
    public DadesContacte getDadesContacte() {
        return dadesContacte;
    }

    /**
     * Sets the value of the dadesContacte property.
     * 
     * @param value
     *     allowed object is
     *     {@link DadesContacte }
     *     
     */
    public void setDadesContacte(DadesContacte value) {
        this.dadesContacte = value;
    }

    /**
     * Gets the value of the dadesTargeta property.
     * 
     * @return
     *     possible object is
     *     {@link DadesTargeta }
     *     
     */
    public DadesTargeta getDadesTargeta() {
        return dadesTargeta;
    }

    /**
     * Sets the value of the dadesTargeta property.
     * 
     * @param value
     *     allowed object is
     *     {@link DadesTargeta }
     *     
     */
    public void setDadesTargeta(DadesTargeta value) {
        this.dadesTargeta = value;
    }

    /**
     * Gets the value of the adreca property.
     * 
     * @return
     *     possible object is
     *     {@link Adreca }
     *     
     */
    public Adreca getAdreca() {
        return adreca;
    }

    /**
     * Sets the value of the adreca property.
     * 
     * @param value
     *     allowed object is
     *     {@link Adreca }
     *     
     */
    public void setAdreca(Adreca value) {
        this.adreca = value;
    }

    /**
     * Gets the value of the opcionsBancaries property.
     * 
     * @return
     *     possible object is
     *     {@link OpcionsBancaries }
     *     
     */
    public OpcionsBancaries getOpcionsBancaries() {
        return opcionsBancaries;
    }

    /**
     * Sets the value of the opcionsBancaries property.
     * 
     * @param value
     *     allowed object is
     *     {@link OpcionsBancaries }
     *     
     */
    public void setOpcionsBancaries(OpcionsBancaries value) {
        this.opcionsBancaries = value;
    }

    /**
     * Gets the value of the dadesIVA property.
     * 
     * @return
     *     possible object is
     *     {@link DadesIVA }
     *     
     */
    public DadesIVA getDadesIVA() {
        return dadesIVA;
    }

    /**
     * Sets the value of the dadesIVA property.
     * 
     * @param value
     *     allowed object is
     *     {@link DadesIVA }
     *     
     */
    public void setDadesIVA(DadesIVA value) {
        this.dadesIVA = value;
    }

}
