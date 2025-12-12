
package es.onebox.fcb.datasources.peoplesoft.wsdl.tresoreria;

import java.math.BigInteger;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RemitentExtracteType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RemitentExtracteType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="idBancExtern" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/&gt;
 *         &lt;element name="idSucursal" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="numCompteBancaria" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="numSequenciaRegistre" type="{http://www.w3.org/2001/XMLSchema}integer"/&gt;
 *         &lt;element name="importPagament" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/&gt;
 *         &lt;element name="descompteAplicat" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/&gt;
 *         &lt;element name="importAjust" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/&gt;
 *         &lt;element name="motiuAjust" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="codClasificacioReferencia" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="referencia" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="rangValorReferencia" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="idClient" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="idMICR" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RemitentExtracteType", propOrder = {
    "idBancExtern",
    "idSucursal",
    "numCompteBancaria",
    "numSequenciaRegistre",
    "importPagament",
    "descompteAplicat",
    "importAjust",
    "motiuAjust",
    "codClasificacioReferencia",
    "referencia",
    "rangValorReferencia",
    "idClient",
    "idMICR"
})
public class RemitentExtracteType {

    protected BigInteger idBancExtern;
    protected String idSucursal;
    protected String numCompteBancaria;
    @XmlElement(required = true)
    protected BigInteger numSequenciaRegistre;
    protected Double importPagament;
    protected Double descompteAplicat;
    protected Double importAjust;
    protected String motiuAjust;
    protected String codClasificacioReferencia;
    protected String referencia;
    protected String rangValorReferencia;
    protected String idClient;
    protected String idMICR;

    /**
     * Gets the value of the idBancExtern property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getIdBancExtern() {
        return idBancExtern;
    }

    /**
     * Sets the value of the idBancExtern property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setIdBancExtern(BigInteger value) {
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
     * Gets the value of the numSequenciaRegistre property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getNumSequenciaRegistre() {
        return numSequenciaRegistre;
    }

    /**
     * Sets the value of the numSequenciaRegistre property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setNumSequenciaRegistre(BigInteger value) {
        this.numSequenciaRegistre = value;
    }

    /**
     * Gets the value of the importPagament property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getImportPagament() {
        return importPagament;
    }

    /**
     * Sets the value of the importPagament property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setImportPagament(Double value) {
        this.importPagament = value;
    }

    /**
     * Gets the value of the descompteAplicat property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getDescompteAplicat() {
        return descompteAplicat;
    }

    /**
     * Sets the value of the descompteAplicat property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setDescompteAplicat(Double value) {
        this.descompteAplicat = value;
    }

    /**
     * Gets the value of the importAjust property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getImportAjust() {
        return importAjust;
    }

    /**
     * Sets the value of the importAjust property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setImportAjust(Double value) {
        this.importAjust = value;
    }

    /**
     * Gets the value of the motiuAjust property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMotiuAjust() {
        return motiuAjust;
    }

    /**
     * Sets the value of the motiuAjust property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMotiuAjust(String value) {
        this.motiuAjust = value;
    }

    /**
     * Gets the value of the codClasificacioReferencia property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodClasificacioReferencia() {
        return codClasificacioReferencia;
    }

    /**
     * Sets the value of the codClasificacioReferencia property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodClasificacioReferencia(String value) {
        this.codClasificacioReferencia = value;
    }

    /**
     * Gets the value of the referencia property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReferencia() {
        return referencia;
    }

    /**
     * Sets the value of the referencia property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReferencia(String value) {
        this.referencia = value;
    }

    /**
     * Gets the value of the rangValorReferencia property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRangValorReferencia() {
        return rangValorReferencia;
    }

    /**
     * Sets the value of the rangValorReferencia property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRangValorReferencia(String value) {
        this.rangValorReferencia = value;
    }

    /**
     * Gets the value of the idClient property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdClient() {
        return idClient;
    }

    /**
     * Sets the value of the idClient property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdClient(String value) {
        this.idClient = value;
    }

    /**
     * Gets the value of the idMICR property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdMICR() {
        return idMICR;
    }

    /**
     * Sets the value of the idMICR property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdMICR(String value) {
        this.idMICR = value;
    }

}
