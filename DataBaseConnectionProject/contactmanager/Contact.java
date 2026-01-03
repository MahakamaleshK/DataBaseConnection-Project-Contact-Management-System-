package contactmanager;

public class Contact {
	
	public int contactId;
    public int userId;
    public String firstName;
    public String lastName;
    public String mobile;
    public String email;
    public String dob;
    public String secondNumber;
    public String instagram;
    public String memory;
    public String status;
    public boolean isEmergency;
    public String group;

    public Contact(int userId, String firstName, String lastName, String mobile,
                   String email, String dob, String secondNumber,
                   String instagram, String memory, String status,
                   boolean isEmergency, String group) {

        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobile = mobile;
        this.email = email;
        this.dob = dob;
        this.secondNumber = secondNumber;
        this.instagram = instagram;
        this.memory = memory;
        this.status = status == null ? "active" : status;
        this.isEmergency = isEmergency;
        this.group = group;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName + " | " + mobile + " | " + email + " | " + dob + " | " + isEmergency + " | " + group + " | Status: " + status;
    }
}
