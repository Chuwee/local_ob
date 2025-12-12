
package es.onebox.fcb.datasources.peoplesoft.wsdl.clients;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PeticioModificacioClient complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PeticioModificacioClient"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="dadesClient" type="{http://fcbarcelona.cat/servei/Vendes/Clients/v01/types}DadesModificarClient"/&gt;
 *         &lt;element name="modeModificacio" type="{http://fcbarcelona.cat/servei/Vendes/Clients/v01/types}ModeModificacio"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PeticioModificacioClient", propOrder = {
    "dadesClient",
    "modeModificacio"
})
public class PeticioModificacioClient {

    @XmlElement(required = true)
    protected DadesModificarClient dadesClient;
    protected int modeModificacio;

    /**
     * Gets the value of the dadesClient property.
     * 
     * @return
     *     possible object is
     *     {@link DadesModificarClient }
     *     
     */
    public DadesModificarClient getDadesClient() {
        return dadesClient;
    }

    /**
     * Sets the value of the dadesClient property.
     * 
     * @param value
     *     allowed object is
     *     {@link DadesModificarClient }
     *     
     */
    public void setDadesClient(DadesModificarClient value) {
        this.dadesClient = value;
    }

    /**
     * Gets the value of the modeModificacio property.
     * 
     */
    public int getModeModificacio() {
        return modeModificacio;
    }

    /**
     * Sets the value of the modeModificacio property.
     * 
     */
    public void setModeModificacio(int value) {
        this.modeModificacio = value;
    }

}
