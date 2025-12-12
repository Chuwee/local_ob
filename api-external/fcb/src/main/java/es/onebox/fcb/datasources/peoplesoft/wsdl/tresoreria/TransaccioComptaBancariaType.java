
package es.onebox.fcb.datasources.peoplesoft.wsdl.tresoreria;

import java.math.BigInteger;
import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TransaccioComptaBancariaType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TransaccioComptaBancariaType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="numSecuenciaRegistre" type="{http://www.w3.org/2001/XMLSchema}integer"/&gt;
 *         &lt;element name="codTransaccioBancaria" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="codTransaccio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="importTransaccio" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/&gt;
 *         &lt;element name="idRefConciliacio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="idRefExtern" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="tipusActivitatExtracte" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="descripcio" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="dataTransaccio" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *         &lt;element name="dadesExtracte" type="{http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types}DadesExtracteType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransaccioComptaBancariaType", propOrder = {
    "numSecuenciaRegistre",
    "codTransaccioBancaria",
    "codTransaccio",
    "importTransaccio",
    "idRefConciliacio",
    "idRefExtern",
    "tipusActivitatExtracte",
    "descripcio",
    "dataTransaccio",
    "dadesExtracte"
})
public class TransaccioComptaBancariaType {

    @XmlElement(required = true)
    protected BigInteger numSecuenciaRegistre;
    protected String codTransaccioBancaria;
    protected String codTransaccio;
    protected Double importTransaccio;
    protected String idRefConciliacio;
    protected String idRefExtern;
    protected String tipusActivitatExtracte;
    protected String descripcio;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dataTransaccio;
    protected DadesExtracteType dadesExtracte;

    /**
     * Gets the value of the numSecuenciaRegistre property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getNumSecuenciaRegistre() {
        return numSecuenciaRegistre;
    }

    /**
     * Sets the value of the numSecuenciaRegistre property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setNumSecuenciaRegistre(BigInteger value) {
        this.numSecuenciaRegistre = value;
    }

    /**
     * Gets the value of the codTransaccioBancaria property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodTransaccioBancaria() {
        return codTransaccioBancaria;
    }

    /**
     * Sets the value of the codTransaccioBancaria property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodTransaccioBancaria(String value) {
        this.codTransaccioBancaria = value;
    }

    /**
     * Gets the value of the codTransaccio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodTransaccio() {
        return codTransaccio;
    }

    /**
     * Sets the value of the codTransaccio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodTransaccio(String value) {
        this.codTransaccio = value;
    }

    /**
     * Gets the value of the importTransaccio property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getImportTransaccio() {
        return importTransaccio;
    }

    /**
     * Sets the value of the importTransaccio property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setImportTransaccio(Double value) {
        this.importTransaccio = value;
    }

    /**
     * Gets the value of the idRefConciliacio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdRefConciliacio() {
        return idRefConciliacio;
    }

    /**
     * Sets the value of the idRefConciliacio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdRefConciliacio(String value) {
        this.idRefConciliacio = value;
    }

    /**
     * Gets the value of the idRefExtern property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdRefExtern() {
        return idRefExtern;
    }

    /**
     * Sets the value of the idRefExtern property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdRefExtern(String value) {
        this.idRefExtern = value;
    }

    /**
     * Gets the value of the tipusActivitatExtracte property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipusActivitatExtracte() {
        return tipusActivitatExtracte;
    }

    /**
     * Sets the value of the tipusActivitatExtracte property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipusActivitatExtracte(String value) {
        this.tipusActivitatExtracte = value;
    }

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
     * Gets the value of the dataTransaccio property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataTransaccio() {
        return dataTransaccio;
    }

    /**
     * Sets the value of the dataTransaccio property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataTransaccio(XMLGregorianCalendar value) {
        this.dataTransaccio = value;
    }

    /**
     * Gets the value of the dadesExtracte property.
     * 
     * @return
     *     possible object is
     *     {@link DadesExtracteType }
     *     
     */
    public DadesExtracteType getDadesExtracte() {
        return dadesExtracte;
    }

    /**
     * Sets the value of the dadesExtracte property.
     * 
     * @param value
     *     allowed object is
     *     {@link DadesExtracteType }
     *     
     */
    public void setDadesExtracte(DadesExtracteType value) {
        this.dadesExtracte = value;
    }

}
