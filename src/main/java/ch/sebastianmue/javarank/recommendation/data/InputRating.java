package ch.sebastianmue.javarank.recommendation.data;

public class InputRating {
    private int userId;
    private int productId;
    private double rating;

    public InputRating(int userId, int productId, double rating) {
        this.userId = userId;
        this.productId = productId;
        this.rating = rating;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
