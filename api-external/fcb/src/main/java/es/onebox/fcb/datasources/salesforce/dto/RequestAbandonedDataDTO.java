package es.onebox.fcb.datasources.salesforce.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;


public class RequestAbandonedDataDTO {

    @JsonProperty("Email")
    private String email;

    @JsonProperty("Nombre")
    private String nombre;

    @JsonProperty("Apellidos")
    private String apellidos;

    @JsonProperty("Pais")
    private String pais;

    @JsonProperty("Categoria")
    private String categoria;

    @JsonProperty("Producto")
    private String producto;

    @JsonProperty("VIP")
    private String vip;

    @JsonProperty("Idioma")
    private String idioma;

    @JsonProperty("URL")
    private String url;

    @JsonProperty("Fecha")
    private String fecha;

    @JsonProperty("Fecha_OK")
    private LocalDateTime fechaOk;

    @JsonProperty("IDCliente")
    private String idCliente;

    @JsonProperty("PoliticaPrivacidad")
    private String politicaPrivacidad;

    @JsonProperty("BarcaFansGDPR")
    private String barcaFansGDPR;

    @JsonProperty("ProductsGDPR")
    private String productsGDPR;

    @JsonProperty("PartnersGDPR")
    private String partnersGDPR;

    @JsonProperty("TemporadaAVET")
    private String temporadaAVET;

    @JsonProperty("AforoAVET")
    private String aforoAVET;

    @JsonProperty("PartidoAVET")
    private String partidoAVET;

    @JsonProperty("CantidadEntradas")
    private String cantidadEntradas;

    @JsonProperty("ImporteTotal")
    private String importeTotal;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public String getVip() {
        return vip;
    }

    public void setVip(String vip) {
        this.vip = vip;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public LocalDateTime getFechaOk() {
        return fechaOk;
    }

    public void setFechaOk(LocalDateTime fechaOk) {
        this.fechaOk = fechaOk;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public String getPoliticaPrivacidad() {
        return politicaPrivacidad;
    }

    public void setPoliticaPrivacidad(String politicaPrivacidad) {
        this.politicaPrivacidad = politicaPrivacidad;
    }

    public String getBarcaFansGDPR() {
        return barcaFansGDPR;
    }

    public void setBarcaFansGDPR(String barcaFansGDPR) {
        this.barcaFansGDPR = barcaFansGDPR;
    }

    public String getProductsGDPR() {
        return productsGDPR;
    }

    public void setProductsGDPR(String productsGDPR) {
        this.productsGDPR = productsGDPR;
    }

    public String getPartnersGDPR() {
        return partnersGDPR;
    }

    public void setPartnersGDPR(String partnersGDPR) {
        this.partnersGDPR = partnersGDPR;
    }

    public String getTemporadaAVET() {
        return temporadaAVET;
    }

    public void setTemporadaAVET(String temporadaAVET) {
        this.temporadaAVET = temporadaAVET;
    }

    public String getAforoAVET() {
        return aforoAVET;
    }

    public void setAforoAVET(String aforoAVET) {
        this.aforoAVET = aforoAVET;
    }

    public String getPartidoAVET() {
        return partidoAVET;
    }

    public void setPartidoAVET(String partidoAVET) {
        this.partidoAVET = partidoAVET;
    }

    public String getCantidadEntradas() {
        return cantidadEntradas;
    }

    public void setCantidadEntradas(String cantidadEntradas) {
        this.cantidadEntradas = cantidadEntradas;
    }

    public String getImporteTotal() {
        return importeTotal;
    }

    public void setImporteTotal(String importeTotal) {
        this.importeTotal = importeTotal;
    }
}
