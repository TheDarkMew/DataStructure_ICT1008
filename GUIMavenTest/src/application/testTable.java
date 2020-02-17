package application;
import javafx.beans.property.SimpleStringProperty;

public class testTable {
	private SimpleStringProperty firstName, lastName, birthday, gender, profession;
	
	
	public testTable(String firstName, String lastName, String birthday,String gender, String profession) {
		this.firstName = new SimpleStringProperty(firstName);
		this.lastName = new SimpleStringProperty(lastName);
		this.birthday = new SimpleStringProperty(birthday);
		this.gender = new SimpleStringProperty(gender);
		this.profession = new SimpleStringProperty(profession);
		
	}
	
	public String getFirstName() {
		return firstName.get();
	}
	
	public void setFirstName(SimpleStringProperty firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return lastName.get();
	}
	
	public void setLastName(SimpleStringProperty lastName) {
		this.lastName = lastName;
	}
	
	public String getBirthday() {
		return birthday.get();
	}
	
	public void setBirthday(SimpleStringProperty birthday) {
		this.birthday = birthday;
	}
	
	public String getGender() {
		return gender.get();
	}
	
	public void setGender(SimpleStringProperty gender) {
		this.gender = gender;
	}
	
	public String getProfession() {
		return profession.get();
	}
	
	public void setProfession(SimpleStringProperty profession) {
		this.profession = profession;
	}
	

	
	
}
