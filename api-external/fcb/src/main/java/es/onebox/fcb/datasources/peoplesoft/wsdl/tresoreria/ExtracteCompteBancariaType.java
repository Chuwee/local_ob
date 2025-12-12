
package es.onebox.fcb.datasources.peoplesoft.wsdl.tresoreria;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ExtracteCompteBancariaType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ExtracteCompteBancariaType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="capcaleraExtracte" type="{http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types}CapcaleraExtracteBancariType"/&gt;
 *         &lt;element name="diaAnterior" maxOccurs="unbounded" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="capcalera" type="{http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types}CapcaleraComptaBancariaType"/&gt;
 *                   &lt;element name="balanc" type="{http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types}BalanceComptaBancariaType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *                   &lt;element name="transaccioPrevia" maxOccurs="unbounded" minOccurs="0"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                           &lt;sequence&gt;
 *                             &lt;element name="transaccio" type="{http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types}TransaccioComptaBancariaType"/&gt;
 *                             &lt;element name="remitent" type="{http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types}RemitentExtracteType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *                           &lt;/sequence&gt;
 *                         &lt;/restriction&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="diaActual" maxOccurs="unbounded" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="capcalera" type="{http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types}CapcaleraComptaBancariaType"/&gt;
 *                   &lt;element name="balanc" type="{http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types}BalanceComptaBancariaType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *                   &lt;element name="transaccio" type="{http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types}TransaccioComptaBancariaType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExtracteCompteBancariaType", propOrder = {
    "capcaleraExtracte",
    "diaAnterior",
    "diaActual"
})
public class ExtracteCompteBancariaType {

    @XmlElement(required = true)
    protected CapcaleraExtracteBancariType capcaleraExtracte;
    protected List<ExtracteCompteBancariaType.DiaAnterior> diaAnterior;
    protected List<ExtracteCompteBancariaType.DiaActual> diaActual;

    /**
     * Gets the value of the capcaleraExtracte property.
     * 
     * @return
     *     possible object is
     *     {@link CapcaleraExtracteBancariType }
     *     
     */
    public CapcaleraExtracteBancariType getCapcaleraExtracte() {
        return capcaleraExtracte;
    }

    /**
     * Sets the value of the capcaleraExtracte property.
     * 
     * @param value
     *     allowed object is
     *     {@link CapcaleraExtracteBancariType }
     *     
     */
    public void setCapcaleraExtracte(CapcaleraExtracteBancariType value) {
        this.capcaleraExtracte = value;
    }

    /**
     * Gets the value of the diaAnterior property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the diaAnterior property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDiaAnterior().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ExtracteCompteBancariaType.DiaAnterior }
     * 
     * 
     */
    public List<ExtracteCompteBancariaType.DiaAnterior> getDiaAnterior() {
        if (diaAnterior == null) {
            diaAnterior = new ArrayList<ExtracteCompteBancariaType.DiaAnterior>();
        }
        return this.diaAnterior;
    }

    /**
     * Gets the value of the diaActual property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the diaActual property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDiaActual().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ExtracteCompteBancariaType.DiaActual }
     * 
     * 
     */
    public List<ExtracteCompteBancariaType.DiaActual> getDiaActual() {
        if (diaActual == null) {
            diaActual = new ArrayList<ExtracteCompteBancariaType.DiaActual>();
        }
        return this.diaActual;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element name="capcalera" type="{http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types}CapcaleraComptaBancariaType"/&gt;
     *         &lt;element name="balanc" type="{http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types}BalanceComptaBancariaType" maxOccurs="unbounded" minOccurs="0"/&gt;
     *         &lt;element name="transaccio" type="{http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types}TransaccioComptaBancariaType" maxOccurs="unbounded" minOccurs="0"/&gt;
     *       &lt;/sequence&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "capcalera",
        "balanc",
        "transaccio"
    })
    public static class DiaActual {

        @XmlElement(required = true)
        protected CapcaleraComptaBancariaType capcalera;
        protected List<BalanceComptaBancariaType> balanc;
        protected List<TransaccioComptaBancariaType> transaccio;

        /**
         * Gets the value of the capcalera property.
         * 
         * @return
         *     possible object is
         *     {@link CapcaleraComptaBancariaType }
         *     
         */
        public CapcaleraComptaBancariaType getCapcalera() {
            return capcalera;
        }

        /**
         * Sets the value of the capcalera property.
         * 
         * @param value
         *     allowed object is
         *     {@link CapcaleraComptaBancariaType }
         *     
         */
        public void setCapcalera(CapcaleraComptaBancariaType value) {
            this.capcalera = value;
        }

        /**
         * Gets the value of the balanc property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the Jakarta XML Binding object.
         * This is why there is not a <CODE>set</CODE> method for the balanc property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getBalanc().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link BalanceComptaBancariaType }
         * 
         * 
         */
        public List<BalanceComptaBancariaType> getBalanc() {
            if (balanc == null) {
                balanc = new ArrayList<BalanceComptaBancariaType>();
            }
            return this.balanc;
        }

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
         * {@link TransaccioComptaBancariaType }
         * 
         * 
         */
        public List<TransaccioComptaBancariaType> getTransaccio() {
            if (transaccio == null) {
                transaccio = new ArrayList<TransaccioComptaBancariaType>();
            }
            return this.transaccio;
        }

    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element name="capcalera" type="{http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types}CapcaleraComptaBancariaType"/&gt;
     *         &lt;element name="balanc" type="{http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types}BalanceComptaBancariaType" maxOccurs="unbounded" minOccurs="0"/&gt;
     *         &lt;element name="transaccioPrevia" maxOccurs="unbounded" minOccurs="0"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *                 &lt;sequence&gt;
     *                   &lt;element name="transaccio" type="{http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types}TransaccioComptaBancariaType"/&gt;
     *                   &lt;element name="remitent" type="{http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types}RemitentExtracteType" maxOccurs="unbounded" minOccurs="0"/&gt;
     *                 &lt;/sequence&gt;
     *               &lt;/restriction&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *       &lt;/sequence&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "capcalera",
        "balanc",
        "transaccioPrevia"
    })
    public static class DiaAnterior {

        @XmlElement(required = true)
        protected CapcaleraComptaBancariaType capcalera;
        protected List<BalanceComptaBancariaType> balanc;
        protected List<ExtracteCompteBancariaType.DiaAnterior.TransaccioPrevia> transaccioPrevia;

        /**
         * Gets the value of the capcalera property.
         * 
         * @return
         *     possible object is
         *     {@link CapcaleraComptaBancariaType }
         *     
         */
        public CapcaleraComptaBancariaType getCapcalera() {
            return capcalera;
        }

        /**
         * Sets the value of the capcalera property.
         * 
         * @param value
         *     allowed object is
         *     {@link CapcaleraComptaBancariaType }
         *     
         */
        public void setCapcalera(CapcaleraComptaBancariaType value) {
            this.capcalera = value;
        }

        /**
         * Gets the value of the balanc property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the Jakarta XML Binding object.
         * This is why there is not a <CODE>set</CODE> method for the balanc property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getBalanc().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link BalanceComptaBancariaType }
         * 
         * 
         */
        public List<BalanceComptaBancariaType> getBalanc() {
            if (balanc == null) {
                balanc = new ArrayList<BalanceComptaBancariaType>();
            }
            return this.balanc;
        }

        /**
         * Gets the value of the transaccioPrevia property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the Jakarta XML Binding object.
         * This is why there is not a <CODE>set</CODE> method for the transaccioPrevia property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getTransaccioPrevia().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ExtracteCompteBancariaType.DiaAnterior.TransaccioPrevia }
         * 
         * 
         */
        public List<ExtracteCompteBancariaType.DiaAnterior.TransaccioPrevia> getTransaccioPrevia() {
            if (transaccioPrevia == null) {
                transaccioPrevia = new ArrayList<ExtracteCompteBancariaType.DiaAnterior.TransaccioPrevia>();
            }
            return this.transaccioPrevia;
        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType&gt;
         *   &lt;complexContent&gt;
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
         *       &lt;sequence&gt;
         *         &lt;element name="transaccio" type="{http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types}TransaccioComptaBancariaType"/&gt;
         *         &lt;element name="remitent" type="{http://fcbarcelona.cat/servei/Finances/ServeiTresoreria/v01/types}RemitentExtracteType" maxOccurs="unbounded" minOccurs="0"/&gt;
         *       &lt;/sequence&gt;
         *     &lt;/restriction&gt;
         *   &lt;/complexContent&gt;
         * &lt;/complexType&gt;
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "transaccio",
            "remitent"
        })
        public static class TransaccioPrevia {

            @XmlElement(required = true)
            protected TransaccioComptaBancariaType transaccio;
            protected List<RemitentExtracteType> remitent;

            /**
             * Gets the value of the transaccio property.
             * 
             * @return
             *     possible object is
             *     {@link TransaccioComptaBancariaType }
             *     
             */
            public TransaccioComptaBancariaType getTransaccio() {
                return transaccio;
            }

            /**
             * Sets the value of the transaccio property.
             * 
             * @param value
             *     allowed object is
             *     {@link TransaccioComptaBancariaType }
             *     
             */
            public void setTransaccio(TransaccioComptaBancariaType value) {
                this.transaccio = value;
            }

            /**
             * Gets the value of the remitent property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the Jakarta XML Binding object.
             * This is why there is not a <CODE>set</CODE> method for the remitent property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getRemitent().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link RemitentExtracteType }
             * 
             * 
             */
            public List<RemitentExtracteType> getRemitent() {
                if (remitent == null) {
                    remitent = new ArrayList<RemitentExtracteType>();
                }
                return this.remitent;
            }

        }

    }

}
