package com.radcortez.gwt.rest.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import org.apache.commons.beanutils.PropertyUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class RestSupportServlet extends RemoteServiceServlet {
    @Override
    protected void service(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getMethod();
        if ("POST".equals(method)) {
            String contentType = req.getContentType();
            if (contentType.equals("text/x-gwt-rpc")) {
                doPost(req, resp);
            } else if (contentType.equals("application/json")) {
                serviceRestEndpoint(req, resp);
            }
        } else {
            super.service(req, resp);
        }
    }

    private void serviceRestEndpoint(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        try {
            Method method = getMethod(path);

            if (method.getParameters().length == 0) {
                Object result = method.invoke(this);
                ObjectMapper objectMapper = new ObjectMapper();
                resp.setStatus(200);
                resp.getWriter().write(objectMapper.writeValueAsString(result));

            } else if (method.isAnnotationPresent(Payload.class)) {
                ObjectMapper objectMapper = new ObjectMapper();
                Object payload = objectMapper.readValue(req.getInputStream(), method.getAnnotation(Payload.class).value());

                List<Object> arguments = new ArrayList<>();
                for (Parameter parameter : method.getParameters()) {
                    String objectPath = parameter.getAnnotation(PayloadPath.class).value();
                    arguments.add(PropertyUtils.getProperty(payload, objectPath));
                }

                Object result = method.invoke(this, arguments.toArray(new Object[0]));
                resp.setStatus(200);
                resp.getWriter().write(objectMapper.writeValueAsString(result));
            } else if (method.getParameters().length == 1) {
                ObjectMapper objectMapper = new ObjectMapper();
                Object arg = objectMapper.readValue(req.getInputStream(), method.getParameters()[0].getType());
                Object result = method.invoke(this, arg);
                resp.setStatus(200);
                resp.getWriter().write(objectMapper.writeValueAsString(result));
            } else {
                throw new RuntimeException();
            }

        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private Method getMethod(final String name) {
        List<Method> candidates =
            Arrays.stream(this.getClass().getDeclaredMethods())
                  .filter(method -> method.getName().equals(name))
                  .collect(toList());

        if (candidates.size() == 1) {
            return candidates.get(0);
        }

        throw new RuntimeException();
    }
}
