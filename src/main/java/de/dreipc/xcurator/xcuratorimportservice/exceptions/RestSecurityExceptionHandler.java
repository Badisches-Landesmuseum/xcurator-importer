package de.dreipc.xcurator.xcuratorimportservice.exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
public class RestSecurityExceptionHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final ObjectMapper jsonMapper;

    public RestSecurityExceptionHandler(ObjectMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        var errorBodyModel = handleException(request, exception, HttpStatus.UNAUTHORIZED);
        var jsonBody = jsonMapper.writeValueAsString(errorBodyModel);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.getWriter().write(jsonBody);

        ServerHttpResponse outputMessage = new ServletServerHttpResponse(response);
        outputMessage.setStatusCode(HttpStatus.UNAUTHORIZED);
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ServerHttpResponse outputMessage = new ServletServerHttpResponse(response);
        outputMessage.setStatusCode(HttpStatus.FORBIDDEN);
        response.setContentType(APPLICATION_JSON_VALUE);
        var errorBodyModel = handleException(request, accessDeniedException, HttpStatus.FORBIDDEN);

        var jsonBody = jsonMapper.writeValueAsString(errorBodyModel);
        response.getOutputStream().print(jsonBody);
    }

    private Map<String, Object> handleException(HttpServletRequest request, Exception exception, HttpStatus httpStatus) {
        var errorModel = new HashMap<String, Object>();
        errorModel.put("timestamp", new Date());
        errorModel.put("message", exception.getMessage());
        errorModel.put("code", httpStatus.name());
        errorModel.put("status", httpStatus.value());
        errorModel.put("path", request.getRequestURI());
        return errorModel;
    }
}
