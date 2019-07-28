package net.simplifiedcoding.bottomnavigationexample;

class CartData {
    String SubjectName;
    String Link;
    String Image;
    Integer Id;
    Integer Price;
    Integer Qty;
    public CartData(Integer id, String subjectName, String image, Integer price, Integer qty) {
        this.Id = id;
        this.SubjectName = subjectName;
        this.Image = image;
        this.Price = price;
        this.Qty = qty;
    }
}
