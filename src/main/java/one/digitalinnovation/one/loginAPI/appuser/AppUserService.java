package one.digitalinnovation.one.loginAPI.appuser;

import lombok.AllArgsConstructor;
import one.digitalinnovation.one.loginAPI.registration.token.ConfirmationToken;
import one.digitalinnovation.one.loginAPI.registration.token.ConfirmationTokenService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final static String USER_NOT_FOUND = "User with %s not found";
    private final AppUserRepository appUserRepository;
    private final ConfirmationTokenService confirmationTokenService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return appUserRepository.findByEmail ( email )
                .orElseThrow ( () -> new UsernameNotFoundException ( String.format ( USER_NOT_FOUND, email ) ) );
    }

    public String signUpUser(AppUser appUser) {

        Boolean userExist = appUserRepository.findByEmail ( appUser.getEmail () ).isPresent ();

        if (userExist) {
            throw new IllegalStateException ( "Email in use" );
        }
        String encodePassword = bCryptPasswordEncoder.encode ( appUser.getPassword () );


        appUser.setPassword ( encodePassword );

        appUserRepository.save ( appUser );

        String token = UUID.randomUUID ().toString ();

        ConfirmationToken confirmationToken = new ConfirmationToken (
                token,
                LocalDateTime.now (),
                LocalDateTime.now ().plusMinutes ( 15 ),
                appUser
        );

        confirmationTokenService.saveConfirmationToken ( confirmationToken );


        return token;
    }
    public int enableAppUser(String email) {
        return appUserRepository.enableAppUser(email);
    }

}