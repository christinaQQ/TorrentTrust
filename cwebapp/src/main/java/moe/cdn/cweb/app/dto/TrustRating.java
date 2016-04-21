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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((algorithmName == null) ? 0 : algorithmName.hashCode());
        long temp;
        temp = Double.doubleToLongBits(rating);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TrustRating other = (TrustRating) obj;
        if (algorithmName == null) {
            if (other.algorithmName != null)
                return false;
        } else if (!algorithmName.equals(other.algorithmName))
            return false;
        if (Double.doubleToLongBits(rating) != Double.doubleToLongBits(other.rating))
            return false;
        return true;
    }
    
}
