package es.onebox.common.datasources.catalog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ChannelEventImages implements Serializable {

    @Serial
    private static final long serialVersionUID = 1057847720363912269L;

    private Map<String, String> main;
    private Map<String, String> secondary;
    private List<Map<String, String>> landscape;
    @JsonProperty("banner_promoter")
    private Map<String, String> bannerPromoter;
    @JsonProperty("banner_channel")
    private Map<String, String> bannerChannel;
    @JsonProperty("banner_header")
    private Map<String, String> bannerHeader;

    public Map<String, String> getMain() {
        return main;
    }

    public void setMain(Map<String, String> main) {
        this.main = main;
    }

    public Map<String, String> getSecondary() {
        return secondary;
    }

    public void setSecondary(Map<String, String> secondary) {
        this.secondary = secondary;
    }

    public List<Map<String, String>> getLandscape() {
        return landscape;
    }

    public void setLandscape(List<Map<String, String>> landscape) {
        this.landscape = landscape;
    }

    public Map<String, String> getBannerPromoter() {
        return bannerPromoter;
    }

    public void setBannerPromoter(Map<String, String> bannerPromoter) {
        this.bannerPromoter = bannerPromoter;
    }

    public Map<String, String> getBannerChannel() {
        return bannerChannel;
    }

    public void setBannerChannel(Map<String, String> bannerChannel) {
        this.bannerChannel = bannerChannel;
    }

    public Map<String, String> getBannerHeader() {
        return bannerHeader;
    }

    public void setBannerHeader(Map<String, String> bannerHeader) {
        this.bannerHeader = bannerHeader;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
