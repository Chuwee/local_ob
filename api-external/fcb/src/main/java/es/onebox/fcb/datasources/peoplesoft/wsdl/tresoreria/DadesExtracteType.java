
package es.onebox.fcb.datasources.peoplesoft.wsdl.tresoreria;

import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DadesExtracteType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DadesExtracteType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="codExtracteBancari"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="20"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="tipusDisponibilitatFons" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="saldoDisponible" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/&gt;
 *         &lt;element name="saldoNoDisponibleUnDia" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/&gt;
 *         &lt;element name="saldoNoDisponibleVarisDies" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/&gt;
 *         &lt;element name="codMoneda" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="3"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="dataPagament" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DadesExtracteType", propOrder = {
    "codExtracteBancari",
    "tipusDisponibilitatFons",
    "saldoDisponible",
    "saldoNoDisponibleUnDia",
    "saldoNoDisponibleVarisDies",
    "codMoneda",
    "dataPagament"
})
public class DadesExtracteType {

    @XmlElement(required = true)
    protected String codExtracteBancari;
    protected String tipusDisponibilitatFons;
    protected Double saldoDisponible;
    protected Double saldoNoDisponibleUnDia;
    protected Double saldoNoDisponibleVarisDies;
    protected String codMoneda;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dataPagament;

    /**
     * Gets the value of the codExtracteBancari property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodExtracteBancari() {
        return codExtracteBancari;
    }

    /**
     * Sets the value of the codExtracteBancari property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodExtracteBancari(String value) {
        this.codExtracteBancari = value;
    }

    /**
     * Gets the value of the tipusDisponibilitatFons property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipusDisponibilitatFons() {
        return tipusDisponibilitatFons;
    }

    /**
     * Sets the value of the tipusDisponibilitatFons property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipusDisponibilitatFons(String value) {
        this.tipusDisponibilitatFons = value;
    }

    /**
     * Gets the value of the saldoDisponible property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getSaldoDisponible() {
        return saldoDisponible;
    }

    /**
     * Sets the value of the saldoDisponible property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setSaldoDisponible(Double value) {
        this.saldoDisponible = value;
    }

    /**
     * Gets the value of the saldoNoDisponibleUnDia property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getSaldoNoDisponibleUnDia() {
        return saldoNoDisponibleUnDia;
    }

    /**
     * Sets the value of the saldoNoDisponibleUnDia property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setSaldoNoDisponibleUnDia(Double value) {
        this.saldoNoDisponibleUnDia = value;
    }

    /**
     * Gets the value of the saldoNoDisponibleVarisDies property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getSaldoNoDisponibleVarisDies() {
        return saldoNoDisponibleVarisDies;
    }

    /**
     * Sets the value of the saldoNoDisponibleVarisDies property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setSaldoNoDisponibleVarisDies(Double value) {
        this.saldoNoDisponibleVarisDies = value;
    }

    /**
     * Gets the value of the codMoneda property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodMoneda() {
        return codMoneda;
    }

    /**
     * Sets the value of the codMoneda property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodMoneda(String value) {
        this.codMoneda = value;
    }

    /**
     * Gets the value of the dataPagament property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataPagament() {
        return dataPagament;
    }

    /**
     * Sets the value of the dataPagament property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataPagament(XMLGregorianCalendar value) {
        this.dataPagament = value;
    }

}
