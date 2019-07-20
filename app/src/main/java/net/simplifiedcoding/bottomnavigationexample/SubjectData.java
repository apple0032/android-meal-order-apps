package net.simplifiedcoding.bottomnavigationexample;

class SubjectData {
    String SubjectName;
    String Link;
    String Image;
    Integer Id;
    Integer Price;
    public SubjectData(Integer id,String subjectName, String image, Integer price) {
        this.Id = id;
        this.SubjectName = subjectName;
        this.Image = image;
        this.Price = price;
    }
}
