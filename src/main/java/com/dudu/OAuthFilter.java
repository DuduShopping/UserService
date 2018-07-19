package com.dudu;

import com.dudu.common.LoggedUser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

@Component
public class OAuthFilter extends GenericFilterBean {
    public static final String LOGGED_USER = "LOGGED_USER";
    private static final Logger logger = LogManager.getLogger(OAuthFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        logger.info("Oauth filter");
        LoggedUser user = new LoggedUser();
        user.setUserId(1);
        user.setScopes(new String[]{"jack"});
        request.setAttribute(LOGGED_USER, user);
        chain.doFilter(request, response);
    }
}
