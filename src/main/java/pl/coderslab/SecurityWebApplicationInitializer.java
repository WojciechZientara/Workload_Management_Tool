package pl.coderslab;

import org.springframework.context.annotation.Bean;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

import javax.servlet.SessionTrackingMode;
import javax.servlet.http.HttpSessionListener;
import java.util.Set;

public class SecurityWebApplicationInitializer extends AbstractSecurityWebApplicationInitializer {

    @Override
    protected boolean enableHttpSessionEventPublisher() {
        return true;
    }

}
