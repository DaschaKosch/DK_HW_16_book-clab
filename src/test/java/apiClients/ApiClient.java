package apiClients;

import apiClients.auth.LoginApiClient;
import apiClients.auth.LogoutApiClient;
import apiClients.club.ClubApiClient;
import apiClients.users.RegistrationApiClient;
import apiClients.users.UpdateUserApiClient;


 //Общий API-клиент — единая точка доступа к клиентам эндпоинтов.


public class ApiClient {

    public final RegistrationApiClient registration = new RegistrationApiClient();
    public final LoginApiClient login = new LoginApiClient();
    public final LogoutApiClient logout = new LogoutApiClient();
    public final UpdateUserApiClient updateUser = new UpdateUserApiClient();
    public final ClubApiClient club = new ClubApiClient();
}
