package web.links.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import web.links.model.UserModel;

@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class UserDto {
    private String id;
    private String username;

    private UserDto(final String id, final String username) {
        this.id = id;
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public static UserDto fromModel(final UserModel model) {
        return new UserDto(model.userId(), model.username());
    }
}
