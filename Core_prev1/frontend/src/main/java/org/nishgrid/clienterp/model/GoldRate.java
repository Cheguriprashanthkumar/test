package org.nishgrid.clienterp.model;

import java.time.LocalDateTime;

public class GoldRate {
    private LocalDateTime date;
    private String rate24k;
    private String rate22k;
    private String rate18k;
    private String rate14k;
    private String rate12k;
    private String rate10k;
    private String rate09k;
    private String fineSilver;
    private String sterlingSilver;
    private String coinSilver;

    public GoldRate(LocalDateTime date, String rate24k, String rate22k, String rate18k,
                    String rate14k, String rate12k, String rate10k, String rate09k,
                    String fineSilver, String sterlingSilver, String coinSilver) {
        this.date = date;
        this.rate24k = rate24k;
        this.rate22k = rate22k;
        this.rate18k = rate18k;
        this.rate14k = rate14k;
        this.rate12k = rate12k;
        this.rate10k = rate10k;
        this.rate09k = rate09k;
        this.fineSilver = fineSilver;
        this.sterlingSilver = sterlingSilver;
        this.coinSilver = coinSilver;
    }

    public LocalDateTime getDate() { return date; }
    public String getRate24k() { return rate24k; }
    public String getRate22k() { return rate22k; }
    public String getRate18k() { return rate18k; }
    public String getRate14k() { return rate14k; }
    public String getRate12k() { return rate12k; }
    public String getRate10k() { return rate10k; }
    public String getRate09k() { return rate09k; }
    public String getFineSilver() { return fineSilver; }
    public String getSterlingSilver() { return sterlingSilver; }
    public String getCoinSilver() { return coinSilver; }
}
