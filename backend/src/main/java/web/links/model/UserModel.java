package web.links.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("users")
public record UserModel(
    @Id Integer id,
    @Column("user_id") String userId,
    String username,
    String password
) {
    public UserModel password(final String password) {
        return new UserModel(id, userId, username, password);
    }
}
