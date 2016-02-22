package core.data;

public enum UserFields {	
	GENDER_MALE, GENDER_FEMALE,
	AGE_BELOW_25, AGE_25_TO_34, AGE_35_TO_44, AGE_45_TO_54, AGE_ABOVE_54,
	INCOME_LOW, INCOME_MEDIUM, INCOME_HIGH,
	CONTEXT_NEWS, CONTEXT_SHOPPING, CONTEXT_SOCIAL_MEDIA, CONTEXT_BLOG, CONTEXT_HOBBIES, CONTEXT_TRAVEL;
	
	
	// ==== Constants ====
	
	public final int mask;
	
	
	// ==== Constructor ====
	
	UserFields() {
		mask = 1 << ordinal();
	}	
}