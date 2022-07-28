package web.links.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class ExtendedUser extends User implements ExtendedUserDetails {
    private final String userId;

    public ExtendedUser(final String username, final String password, final Collection<? extends GrantedAuthority> authorities, final String userId) {
        super(username, password, authorities);
        this.userId = userId;
    }

    public ExtendedUser(final String username, final String password, final boolean enabled, final boolean accountNonExpired, final boolean credentialsNonExpired, final boolean accountNonLocked, final Collection<? extends GrantedAuthority> authorities, final String userId) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.userId = userId;
    }

    @Override
    public String getUserId() {
        return userId;
    }
}
