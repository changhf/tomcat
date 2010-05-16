/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.catalina.core;

import javax.servlet.SessionCookieConfig;
import javax.servlet.http.Cookie;

import org.apache.catalina.Context;

public class ApplicationSessionCookieConfig implements SessionCookieConfig {

    private static final String DEFAULT_SESSION_COOKIE_NAME = "JSESSIONID";
    private static final String DEFAULT_SESSION_PARAMETER_NAME = "jsessionid";
    
    private boolean httpOnly;
    private boolean secure;
    private int maxAge = -1;
    private String comment;
    private String domain;
    private String name;
    private String path;
    
    @Override
    public String getComment() {
        return comment;
    }

    @Override
    public String getDomain() {
        return domain;
    }

    @Override
    public int getMaxAge() {
        return maxAge;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public boolean isHttpOnly() {
        return httpOnly;
    }

    @Override
    public boolean isSecure() {
        return secure;
    }

    @Override
    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Override
    public void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    @Override
    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    /**
     * Creates a new session cookie for the given session ID
     *
     * @param context     The Context for the web application
     * @param sessionId   The ID of the session for which the cookie will be
     *                    created
     * @param secure      Should session cookie be configured as secure
     */
    public static Cookie createSessionCookie(Context context,
            String sessionId, boolean secure) {

        SessionCookieConfig scc =
            context.getServletContext().getSessionCookieConfig();

        // NOTE: The priority order for session cookie configuration is:
        //       1. Context level configuration
        //       2. Values from SessionCookieConfig
        //       3. Defaults

        Cookie cookie = new Cookie(getSessionCookieName(context), sessionId);
       
        // Just apply the defaults.
        cookie.setMaxAge(scc.getMaxAge());
        cookie.setComment(scc.getComment());
       
        if (context.getSessionCookieDomain() == null) {
            // Avoid possible NPE
            if (scc.getDomain() != null) {
                cookie.setDomain(scc.getDomain());
            }
        } else {
            cookie.setDomain(context.getSessionCookieDomain());
        }

        // Always set secure if the request is secure
        if (scc.isSecure() || secure) {
            cookie.setSecure(true);
        }

        // Always set httpOnly if the context is configured for that
        if (scc.isHttpOnly() || context.getUseHttpOnly()) {
            cookie.setHttpOnly(true);
        }
       
        String contextPath = context.getSessionCookiePath();
        if (contextPath == null || contextPath.length() == 0) {
            contextPath = scc.getPath();
        }
        if (contextPath == null || contextPath.length() == 0) {
            contextPath = context.getEncodedPath();
        }
        cookie.setPath(contextPath);

        return cookie;
    }
    
    
    private static String getConfiguredSessionCookieName(Context context) {
        
        // Priority is:
        // 1. Cookie name defined in context
        // 2. Cookie name configured for app
        // 3. Default defined by spec
        if (context != null) {
            String cookieName = context.getSessionCookieName();
            if (cookieName != null && cookieName.length() > 0) {
                return cookieName;
            }
            
            SessionCookieConfig scc =
                context.getServletContext().getSessionCookieConfig();
            cookieName = scc.getName();
            if (cookieName != null && cookieName.length() > 0) {
                return cookieName;
            }
        }

        return null;
    }
    
    
    /**
     * Determine the name to use for the session cookie for the provided
     * context.
     * @param context
     */
    public static String getSessionCookieName(Context context) {
    
        String result = getConfiguredSessionCookieName(context);
        
        if (result == null) {
            result = DEFAULT_SESSION_COOKIE_NAME; 
        }
        
        return result; 
    }
    
    /**
     * Determine the name to use for the session cookie for the provided
     * context.
     * @param context
     */
    public static String getSessionUriParamName(Context context) {
        
        String result = getConfiguredSessionCookieName(context);
        
        if (result == null) {
            result = DEFAULT_SESSION_PARAMETER_NAME; 
        }
        
        return result; 
    }
}
