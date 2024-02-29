package io.kamsan.secureinvoices.dtos;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDTO {
	
	private Long userId;
	private String firstName;
	private String lastName;
	private String email;
	private String address;
	private String phone;
	private String title;
	private String bio;
	private String imageUrl;
	private boolean enabled;
	private boolean isNotLocked;
	private boolean isUsingMfa;
	private LocalDateTime createdAt;
	private String roleName;
	private String permissions;

}
