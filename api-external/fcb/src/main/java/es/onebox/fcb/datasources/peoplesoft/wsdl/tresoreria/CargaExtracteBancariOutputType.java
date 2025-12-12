
package es.onebox.fcb.datasources.peoplesoft.wsdl.tresoreria;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CargaExtracteBancariOutputType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CargaExtracteBancariOutputType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="resultat" type="{http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types}ResultatOperacioCargaExtracteBancariType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CargaExtracteBancariOutputType", propOrder = {
    "resultat"
})
public class CargaExtracteBancariOutputType {

    @XmlElement(required = true)
    protected ResultatOperacioCargaExtracteBancariType resultat;

    /**
     * Gets the value of the resultat property.
     * 
     * @return
     *     possible object is
     *     {@link ResultatOperacioCargaExtracteBancariType }
     *     
     */
    public ResultatOperacioCargaExtracteBancariType getResultat() {
        return resultat;
    }

    /**
     * Sets the value of the resultat property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResultatOperacioCargaExtracteBancariType }
     *     
     */
    public void setResultat(ResultatOperacioCargaExtracteBancariType value) {
        this.resultat = value;
    }

}
