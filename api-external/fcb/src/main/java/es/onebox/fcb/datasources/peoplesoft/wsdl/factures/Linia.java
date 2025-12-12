
package es.onebox.fcb.datasources.peoplesoft.wsdl.factures;

import java.math.BigDecimal;
import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;


/**
 * <p>Java class for Linia complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Linia"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="numLinia" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="tipusLinia" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="sistemaOrigen" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="clau" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="dataOperacio" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="idProducte" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="quantitat" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="preuVenda" type="{http://www.w3.org/2001/XMLSchema}decimal"/&gt;
 *         &lt;element name="idCollectiu" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="modeCobrament" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="idOperacio" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="descripcioTipusEntrada" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="canalVenda" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="codiPromocio" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="importPromocio" type="{http://www.w3.org/2001/XMLSchema}decimal"/&gt;
 *         &lt;element name="importComisio" type="{http://www.w3.org/2001/XMLSchema}decimal"/&gt;
 *         &lt;element name="autofactura" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="dataCobrament" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="idCobrament" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="localitzador" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ComplementAnalitic" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="SeientLliure" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Boca-Fila-Seient" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="importPagat" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Linia", propOrder = {
    "numLinia",
    "tipusLinia",
    "sistemaOrigen",
    "clau",
    "dataOperacio",
    "idProducte",
    "quantitat",
    "preuVenda",
    "idCollectiu",
    "modeCobrament",
    "idOperacio",
    "descripcioTipusEntrada",
    "canalVenda",
    "codiPromocio",
    "importPromocio",
    "importComisio",
    "autofactura",
    "dataCobrament",
    "idCobrament",
    "localitzador",
    "complementAnalitic",
    "seientLliure",
    "bocaFilaSeient",
    "importPagat"
})
public class Linia {

    protected int numLinia;
    @XmlElement(required = true)
    protected String tipusLinia;
    @XmlElement(required = true)
    protected String sistemaOrigen;
    @XmlElement(required = true)
    protected String clau;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dataOperacio;
    @XmlElement(required = true)
    protected String idProducte;
    protected int quantitat;
    @XmlElement(required = true)
    protected BigDecimal preuVenda;
    @XmlElement(required = true)
    protected String idCollectiu;
    @XmlElement(required = true)
    protected String modeCobrament;
    @XmlElement(required = true)
    protected String idOperacio;
    @XmlElement(required = true)
    protected String descripcioTipusEntrada;
    @XmlElement(required = true)
    protected String canalVenda;
    @XmlElement(required = true)
    protected String codiPromocio;
    @XmlElement(required = true)
    protected BigDecimal importPromocio;
    @XmlElement(required = true)
    protected BigDecimal importComisio;
    protected boolean autofactura;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dataCobrament;
    protected String idCobrament;
    protected String localitzador;
    @XmlElement(name = "ComplementAnalitic")
    protected String complementAnalitic;
    @XmlElement(name = "SeientLliure")
    protected String seientLliure;
    @XmlElement(name = "Boca-Fila-Seient")
    protected String bocaFilaSeient;
    protected BigDecimal importPagat;

    /**
     * Gets the value of the numLinia property.
     * 
     */
    public int getNumLinia() {
        return numLinia;
    }

    /**
     * Sets the value of the numLinia property.
     * 
     */
    public void setNumLinia(int value) {
        this.numLinia = value;
    }

    /**
     * Gets the value of the tipusLinia property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipusLinia() {
        return tipusLinia;
    }

    /**
     * Sets the value of the tipusLinia property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipusLinia(String value) {
        this.tipusLinia = value;
    }

    /**
     * Gets the value of the sistemaOrigen property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSistemaOrigen() {
        return sistemaOrigen;
    }

    /**
     * Sets the value of the sistemaOrigen property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSistemaOrigen(String value) {
        this.sistemaOrigen = value;
    }

    /**
     * Gets the value of the clau property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClau() {
        return clau;
    }

    /**
     * Sets the value of the clau property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClau(String value) {
        this.clau = value;
    }

    /**
     * Gets the value of the dataOperacio property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataOperacio() {
        return dataOperacio;
    }

    /**
     * Sets the value of the dataOperacio property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataOperacio(XMLGregorianCalendar value) {
        this.dataOperacio = value;
    }

    /**
     * Gets the value of the idProducte property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdProducte() {
        return idProducte;
    }

    /**
     * Sets the value of the idProducte property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdProducte(String value) {
        this.idProducte = value;
    }

    /**
     * Gets the value of the quantitat property.
     * 
     */
    public int getQuantitat() {
        return quantitat;
    }

    /**
     * Sets the value of the quantitat property.
     * 
     */
    public void setQuantitat(int value) {
        this.quantitat = value;
    }

    /**
     * Gets the value of the preuVenda property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getPreuVenda() {
        return preuVenda;
    }

    /**
     * Sets the value of the preuVenda property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setPreuVenda(BigDecimal value) {
        this.preuVenda = value;
    }

    /**
     * Gets the value of the idCollectiu property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdCollectiu() {
        return idCollectiu;
    }

    /**
     * Sets the value of the idCollectiu property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdCollectiu(String value) {
        this.idCollectiu = value;
    }

    /**
     * Gets the value of the modeCobrament property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getModeCobrament() {
        return modeCobrament;
    }

    /**
     * Sets the value of the modeCobrament property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModeCobrament(String value) {
        this.modeCobrament = value;
    }

    /**
     * Gets the value of the idOperacio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdOperacio() {
        return idOperacio;
    }

    /**
     * Sets the value of the idOperacio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdOperacio(String value) {
        this.idOperacio = value;
    }

    /**
     * Gets the value of the descripcioTipusEntrada property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescripcioTipusEntrada() {
        return descripcioTipusEntrada;
    }

    /**
     * Sets the value of the descripcioTipusEntrada property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescripcioTipusEntrada(String value) {
        this.descripcioTipusEntrada = value;
    }

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
     * Gets the value of the codiPromocio property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodiPromocio() {
        return codiPromocio;
    }

    /**
     * Sets the value of the codiPromocio property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodiPromocio(String value) {
        this.codiPromocio = value;
    }

    /**
     * Gets the value of the importPromocio property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getImportPromocio() {
        return importPromocio;
    }

    /**
     * Sets the value of the importPromocio property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setImportPromocio(BigDecimal value) {
        this.importPromocio = value;
    }

    /**
     * Gets the value of the importComisio property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getImportComisio() {
        return importComisio;
    }

    /**
     * Sets the value of the importComisio property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setImportComisio(BigDecimal value) {
        this.importComisio = value;
    }

    /**
     * Gets the value of the autofactura property.
     * 
     */
    public boolean isAutofactura() {
        return autofactura;
    }

    /**
     * Sets the value of the autofactura property.
     * 
     */
    public void setAutofactura(boolean value) {
        this.autofactura = value;
    }

    /**
     * Gets the value of the dataCobrament property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDataCobrament() {
        return dataCobrament;
    }

    /**
     * Sets the value of the dataCobrament property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDataCobrament(XMLGregorianCalendar value) {
        this.dataCobrament = value;
    }

    /**
     * Gets the value of the idCobrament property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdCobrament() {
        return idCobrament;
    }

    /**
     * Sets the value of the idCobrament property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdCobrament(String value) {
        this.idCobrament = value;
    }

    /**
     * Gets the value of the localitzador property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocalitzador() {
        return localitzador;
    }

    /**
     * Sets the value of the localitzador property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocalitzador(String value) {
        this.localitzador = value;
    }

    /**
     * Gets the value of the complementAnalitic property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComplementAnalitic() {
        return complementAnalitic;
    }

    /**
     * Sets the value of the complementAnalitic property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComplementAnalitic(String value) {
        this.complementAnalitic = value;
    }

    /**
     * Gets the value of the seientLliure property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSeientLliure() {
        return seientLliure;
    }

    /**
     * Sets the value of the seientLliure property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSeientLliure(String value) {
        this.seientLliure = value;
    }

    /**
     * Gets the value of the bocaFilaSeient property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBocaFilaSeient() {
        return bocaFilaSeient;
    }

    /**
     * Sets the value of the bocaFilaSeient property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBocaFilaSeient(String value) {
        this.bocaFilaSeient = value;
    }

    /**
     * Gets the value of the importPagat property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getImportPagat() {
        return importPagat;
    }

    /**
     * Sets the value of the importPagat property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setImportPagat(BigDecimal value) {
        this.importPagat = value;
    }

    @Override //TODO pipe - dont merge this
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

}
