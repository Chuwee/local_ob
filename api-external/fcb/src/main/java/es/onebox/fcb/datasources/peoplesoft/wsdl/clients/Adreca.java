
package es.onebox.fcb.datasources.peoplesoft.wsdl.clients;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Adreca complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Adreca"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="descripcio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="correspondencia" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="idioma" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="pais" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="carrer" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="dadesAddicionals" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="planta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="porta" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="numero" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="escala" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="estatProvincia" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ciutat" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="codiPostal" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Adreca", propOrder = {
    "descripcio",
    "correspondencia",
    "idioma",
    "pais",
    "carrer",
    "dadesAddicionals",
    "planta",
    "porta",
    "numero",
    "escala",
    "estatProvincia",
    "ciutat",
    "codiPostal"
})
public class Adreca {

    protected String descripcio;
    protected boolean correspondencia;
    protected String idioma;
    protected String pais;
    protected String carrer;
    protected String dadesAddicionals;
    protected String planta;
    protected String porta;
    protected String numero;
    protected String escala;
    protected String estatProvincia;
    protected String ciutat;
    protected String codiPostal;

    /**
     * Gets the value of the descripcio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescripcio() {
        return descripcio;
    }

    /**
     * Sets the value of the descripcio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescripcio(String value) {
        this.descripcio = value;
    }

    /**
     * Gets the value of the correspondencia property.
     * 
     */
    public boolean isCorrespondencia() {
        return correspondencia;
    }

    /**
     * Sets the value of the correspondencia property.
     * 
     */
    public void setCorrespondencia(boolean value) {
        this.correspondencia = value;
    }

    /**
     * Gets the value of the idioma property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdioma() {
        return idioma;
    }

    /**
     * Sets the value of the idioma property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdioma(String value) {
        this.idioma = value;
    }

    /**
     * Gets the value of the pais property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPais() {
        return pais;
    }

    /**
     * Sets the value of the pais property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPais(String value) {
        this.pais = value;
    }

    /**
     * Gets the value of the carrer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCarrer() {
        return carrer;
    }

    /**
     * Sets the value of the carrer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCarrer(String value) {
        this.carrer = value;
    }

    /**
     * Gets the value of the dadesAddicionals property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDadesAddicionals() {
        return dadesAddicionals;
    }

    /**
     * Sets the value of the dadesAddicionals property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDadesAddicionals(String value) {
        this.dadesAddicionals = value;
    }

    /**
     * Gets the value of the planta property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPlanta() {
        return planta;
    }

    /**
     * Sets the value of the planta property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPlanta(String value) {
        this.planta = value;
    }

    /**
     * Gets the value of the porta property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPorta() {
        return porta;
    }

    /**
     * Sets the value of the porta property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPorta(String value) {
        this.porta = value;
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
     * Gets the value of the escala property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEscala() {
        return escala;
    }

    /**
     * Sets the value of the escala property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEscala(String value) {
        this.escala = value;
    }

    /**
     * Gets the value of the estatProvincia property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEstatProvincia() {
        return estatProvincia;
    }

    /**
     * Sets the value of the estatProvincia property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEstatProvincia(String value) {
        this.estatProvincia = value;
    }

    /**
     * Gets the value of the ciutat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCiutat() {
        return ciutat;
    }

    /**
     * Sets the value of the ciutat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCiutat(String value) {
        this.ciutat = value;
    }

    /**
     * Gets the value of the codiPostal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodiPostal() {
        return codiPostal;
    }

    /**
     * Sets the value of the codiPostal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodiPostal(String value) {
        this.codiPostal = value;
    }

}
