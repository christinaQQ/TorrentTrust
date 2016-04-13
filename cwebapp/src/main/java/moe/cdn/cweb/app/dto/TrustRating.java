package moe.cdn.cweb.app.dto;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author davix
 */
@XmlRootElement
public class TrustRating {
    private double rating;
    private String algorithmName;

    public TrustRating() {
    }

    public TrustRating(double rating, String algorithmName) {
        this.rating = rating;
        this.algorithmName = algorithmName;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public void setAlgorithmName(String algorithmName) {
        this.algorithmName = algorithmName;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
