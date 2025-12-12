
package es.onebox.fcb.datasources.peoplesoft.wsdl.tresoreria;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CargaExtracteBancariInputType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CargaExtracteBancariInputType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="transaccio" type="{http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types}ExtracteCompteBancariaType" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CargaExtracteBancariInputType", propOrder = {
    "transaccio"
})
public class CargaExtracteBancariInputType {

    @XmlElement(required = true)
    protected List<ExtracteCompteBancariaType> transaccio;

    /**
     * Gets the value of the transaccio property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the transaccio property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTransaccio().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ExtracteCompteBancariaType }
     * 
     * 
     */
    public List<ExtracteCompteBancariaType> getTransaccio() {
        if (transaccio == null) {
            transaccio = new ArrayList<ExtracteCompteBancariaType>();
        }
        return this.transaccio;
    }

}
