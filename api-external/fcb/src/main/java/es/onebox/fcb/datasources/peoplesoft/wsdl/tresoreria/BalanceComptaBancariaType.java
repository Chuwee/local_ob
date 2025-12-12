
package es.onebox.fcb.datasources.peoplesoft.wsdl.tresoreria;

import java.math.BigInteger;
import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BalanceComptaBancariaType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BalanceComptaBancariaType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="saldo" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/&gt;
 *         &lt;element name="numArticles" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/&gt;
 *         &lt;element name="dataExtracte" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *         &lt;element name="dataProcesExtracte" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *         &lt;element name="dadesExtracte" type="{http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types}DadesExtracteType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BalanceComptaBancariaType", propOrder = {
    "saldo",
    "numArticles",
    "dataExtracte",
    "dataProcesExtracte",
    "dadesExtracte"
})
public class BalanceComptaBancariaType {

    protected Double saldo;
    protected BigInteger numArticles;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dataExtracte;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dataProcesExtracte;
    @XmlElement(required = true)
    protected DadesExtracteType dadesExtracte;

    /**
     * Gets the value of the saldo property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getSaldo() {
        return saldo;
    }

    /**
     * Sets the value of the saldo property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setSaldo(Double value) {
        this.saldo = value;
    }

    /**
     * Gets the value of the numArticles property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getNumArticles() {
        return numArticles;
    }

    /**
     * Sets the value of the numArticles property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setNumArticles(BigInteger value) {
        this.numArticles = value;
    }

    /**
     * Gets the value of the dataExtracte property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataExtracte() {
        return dataExtracte;
    }

    /**
     * Sets the value of the dataExtracte property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataExtracte(XMLGregorianCalendar value) {
        this.dataExtracte = value;
    }

    /**
     * Gets the value of the dataProcesExtracte property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataProcesExtracte() {
        return dataProcesExtracte;
    }

    /**
     * Sets the value of the dataProcesExtracte property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataProcesExtracte(XMLGregorianCalendar value) {
        this.dataProcesExtracte = value;
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
