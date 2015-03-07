package model;

public enum Role {
	ADMIN,
	AUTHOR,
	SPC,
	PC,
	REVIEWER;
	@Override
	  public String toString() {
		String role = "Error";
	       switch (this) {
	         case ADMIN:
	              role = "SUPER ADMIN";
	              break;
	         case AUTHOR:
	              role = "Author";
	              break;
	         case SPC:
	              role = "Sub-program chair";
	              break;
	         case REVIEWER:
	              role = "Reviewer";
	         case PC:
	              role = "Program Chair";
	        }
	  return role;
	 }
}
