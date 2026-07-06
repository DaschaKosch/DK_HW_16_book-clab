package models.update_user;

import java.util.List;

public record InvalidPartialUpdateUserResponseBodyModel(List<String> username, List<String> firstName,
                                                        List<String>lastName, List<String>email, List<String> remoteAddr) {}

