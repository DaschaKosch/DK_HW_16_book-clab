package models.update_user;


public record SuccessfulUpdateUserPutResponseModel(int id, String username, String firstName,
                                                   String lastName, String email, String remoteAddr ) {}
