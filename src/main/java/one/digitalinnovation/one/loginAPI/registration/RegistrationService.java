package one.digitalinnovation.one.loginAPI.registration;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    public String register(RegistrationRequest request){
        return "works";
    }

}
