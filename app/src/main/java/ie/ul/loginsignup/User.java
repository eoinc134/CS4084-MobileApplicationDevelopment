package ie.ul.loginsignup;

public class User {

    public String fullName, email, gender, yearOfStudy;

    public User(){

    }

    public User(String fullName, String email) {
        this.fullName = fullName;
        this.email = email;
    }

    public User(String fullName, String email, String yearOfStudy) {
        this.fullName = fullName;
        this.email = email;
        this.yearOfStudy = yearOfStudy;
    }

    public User(String fullName, String email, String gender, String yearOfStudy) {
        this.fullName = fullName;
        this.email = email;
        this.gender = gender;
        this.yearOfStudy = yearOfStudy;
    }
}
