package es.onebox.exchange.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;

public class ExchangeResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

     private Boolean success;
     private Long timestamp;
     private String source;
     private HashMap<String, Double> quotes;
     private ExchangeError error;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public HashMap<String, Double> getQuotes() {
        return quotes;
    }

    public void setQuotes(HashMap<String, Double> quotes) {
        this.quotes = quotes;
    }

    public ExchangeError getError() {
        return error;
    }

    public void setError(ExchangeError error) {
        this.error = error;
    }
}
