package es.onebox.exchange.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;

public class ExchangeResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

     private String source;
     private HashMap<String, Double> quotes;

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
}
