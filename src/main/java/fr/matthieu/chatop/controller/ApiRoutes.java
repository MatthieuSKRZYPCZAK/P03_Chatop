package fr.matthieu.chatop.controller;

public class ApiRoutes {


	public static final String BASE_URL = "/api";


	/* Auth URL */
	public static final String AUTH_URL = BASE_URL+"/auth";

	public static final String LOGIN_URL = AUTH_URL+"/login";

	public static final String REGISTER_URL = AUTH_URL+"/register";

	public static final String ME_URL = AUTH_URL+"/me";

	/* Rentals URL */
	public static final String RENTALS_URL = BASE_URL+"/rentals";

	public static final String RENTAL_ID_URL = BASE_URL+"/{id}";

	/* Message URL */
	public static final String MESSAGES_URL = BASE_URL+"/messages";



}
