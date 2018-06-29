package recommendation.data;

public class InputRating {
    private Integer userId;
    private Integer productId;
    private Integer rating;

    public InputRating(Integer userId, Integer productId, Integer rating) {
        this.userId = userId;
        this.productId = productId;
        this.rating = rating;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }
}
