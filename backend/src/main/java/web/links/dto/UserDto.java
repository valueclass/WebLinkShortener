package web.links.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import web.links.model.UserModel;

@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class UserDto {
    private String id;
    private String username;
    private String password;

    private UserDto(String id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static UserDto fromModel(UserModel model) {
        return new UserDto(model.userId(), model.username(), model.password());
    }
}
