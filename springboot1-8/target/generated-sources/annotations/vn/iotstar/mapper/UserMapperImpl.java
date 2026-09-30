package vn.iotstar.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import vn.iotstar.dto.UserDTO;
import vn.iotstar.entity.Role;
import vn.iotstar.entity.User;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-30T15:45:59+0700",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.0.v20260528-0407, environment: Java 25.0.3 (Eclipse Adoptium)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDTO toDto(User entity) {
        if ( entity == null ) {
            return null;
        }

        UserDTO userDTO = new UserDTO();

        userDTO.setRoleId( entityRoleId( entity ) );
        userDTO.setRoleName( entityRoleName( entity ) );
        userDTO.setCreatedAt( entity.getCreatedAt() );
        userDTO.setEmail( entity.getEmail() );
        userDTO.setEnabled( entity.isEnabled() );
        userDTO.setFullName( entity.getFullName() );
        userDTO.setId( entity.getId() );

        return userDTO;
    }

    @Override
    public User toEntity(UserDTO dto) {
        if ( dto == null ) {
            return null;
        }

        User user = new User();

        user.setCreatedAt( dto.getCreatedAt() );
        user.setEmail( dto.getEmail() );
        user.setEnabled( dto.isEnabled() );
        user.setFullName( dto.getFullName() );
        user.setId( dto.getId() );

        return user;
    }

    private Long entityRoleId(User user) {
        Role role = user.getRole();
        if ( role == null ) {
            return null;
        }
        return role.getId();
    }

    private String entityRoleName(User user) {
        Role role = user.getRole();
        if ( role == null ) {
            return null;
        }
        return role.getName();
    }
}
