
package es.onebox.fcb.datasources.peoplesoft.wsdl.tresoreria;

import java.math.BigInteger;
import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CapcaleraComptaBancariaBalType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CapcaleraComptaBancariaBalType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="idBancExtern" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="idSucursal" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="numCompteBancaria" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="codExtracteBancari" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="saldo" type="{http://www.w3.org/2001/XMLSchema}double"/&gt;
 *         &lt;element name="numArticles" type="{http://www.w3.org/2001/XMLSchema}integer"/&gt;
 *         &lt;element name="tipusDisponibilitatFonds" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="saldoDisponible" type="{http://www.w3.org/2001/XMLSchema}double"/&gt;
 *         &lt;element name="saldoNoDisponible1Dia" type="{http://www.w3.org/2001/XMLSchema}double"/&gt;
 *         &lt;element name="saldoNoDisponibleVariosDias" type="{http://www.w3.org/2001/XMLSchema}double"/&gt;
 *         &lt;element name="dataExtracte" type="{http://www.w3.org/2001/XMLSchema}date"/&gt;
 *         &lt;element name="codMoneda" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="dataPagament" type="{http://www.w3.org/2001/XMLSchema}date"/&gt;
 *         &lt;element name="dataProcesExtracte" type="{http://www.w3.org/2001/XMLSchema}date"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CapcaleraComptaBancariaBalType", propOrder = {
    "idBancExtern",
    "idSucursal",
    "numCompteBancaria",
    "codExtracteBancari",
    "saldo",
    "numArticles",
    "tipusDisponibilitatFonds",
    "saldoDisponible",
    "saldoNoDisponible1Dia",
    "saldoNoDisponibleVariosDias",
    "dataExtracte",
    "codMoneda",
    "dataPagament",
    "dataProcesExtracte"
})
public class CapcaleraComptaBancariaBalType {

    @XmlElement(required = true)
    protected String idBancExtern;
    @XmlElement(required = true)
    protected String idSucursal;
    @XmlElement(required = true)
    protected String numCompteBancaria;
    @XmlElement(required = true)
    protected String codExtracteBancari;
    protected double saldo;
    @XmlElement(required = true)
    protected BigInteger numArticles;
    @XmlElement(required = true)
    protected String tipusDisponibilitatFonds;
    protected double saldoDisponible;
    protected double saldoNoDisponible1Dia;
    protected double saldoNoDisponibleVariosDias;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dataExtracte;
    @XmlElement(required = true)
    protected String codMoneda;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dataPagament;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dataProcesExtracte;

    /**
     * Gets the value of the idBancExtern property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdBancExtern() {
        return idBancExtern;
    }

    /**
     * Sets the value of the idBancExtern property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdBancExtern(String value) {
        this.idBancExtern = value;
    }

    /**
     * Gets the value of the idSucursal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdSucursal() {
        return idSucursal;
    }

    /**
     * Sets the value of the idSucursal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdSucursal(String value) {
        this.idSucursal = value;
    }

    /**
     * Gets the value of the numCompteBancaria property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumCompteBancaria() {
        return numCompteBancaria;
    }

    /**
     * Sets the value of the numCompteBancaria property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumCompteBancaria(String value) {
        this.numCompteBancaria = value;
    }

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
     * Gets the value of the saldo property.
     * 
     */
    public double getSaldo() {
        return saldo;
    }

    /**
     * Sets the value of the saldo property.
     * 
     */
    public void setSaldo(double value) {
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
     * Gets the value of the tipusDisponibilitatFonds property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipusDisponibilitatFonds() {
        return tipusDisponibilitatFonds;
    }

    /**
     * Sets the value of the tipusDisponibilitatFonds property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipusDisponibilitatFonds(String value) {
        this.tipusDisponibilitatFonds = value;
    }

    /**
     * Gets the value of the saldoDisponible property.
     * 
     */
    public double getSaldoDisponible() {
        return saldoDisponible;
    }

    /**
     * Sets the value of the saldoDisponible property.
     * 
     */
    public void setSaldoDisponible(double value) {
        this.saldoDisponible = value;
    }

    /**
     * Gets the value of the saldoNoDisponible1Dia property.
     * 
     */
    public double getSaldoNoDisponible1Dia() {
        return saldoNoDisponible1Dia;
    }

    /**
     * Sets the value of the saldoNoDisponible1Dia property.
     * 
     */
    public void setSaldoNoDisponible1Dia(double value) {
        this.saldoNoDisponible1Dia = value;
    }

    /**
     * Gets the value of the saldoNoDisponibleVariosDias property.
     * 
     */
    public double getSaldoNoDisponibleVariosDias() {
        return saldoNoDisponibleVariosDias;
    }

    /**
     * Sets the value of the saldoNoDisponibleVariosDias property.
     * 
     */
    public void setSaldoNoDisponibleVariosDias(double value) {
        this.saldoNoDisponibleVariosDias = value;
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

}
