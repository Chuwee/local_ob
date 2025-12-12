
package es.onebox.fcb.datasources.peoplesoft.wsdl.clients;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PeticioAltaClient complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PeticioAltaClient"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="dadesClient" type="{http://fcbarcelona.cat/servei/Vendes/Clients/v01/types}DadesAltaClient"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PeticioAltaClient", propOrder = {
    "dadesClient"
})
public class PeticioAltaClient {

    @XmlElement(required = true)
    protected DadesAltaClient dadesClient;

    /**
     * Gets the value of the dadesClient property.
     * 
     * @return
     *     possible object is
     *     {@link DadesAltaClient }
     *     
     */
    public DadesAltaClient getDadesClient() {
        return dadesClient;
    }

    /**
     * Sets the value of the dadesClient property.
     * 
     * @param value
     *     allowed object is
     *     {@link DadesAltaClient }
     *     
     */
    public void setDadesClient(DadesAltaClient value) {
        this.dadesClient = value;
    }

}
