<!--
* Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
* WSO2 Inc. licenses this file to you under the Apache License,
* Version 2.0 (the "License"); you may not use this file except
* in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
-->

<%@ page import="static org.wso2.carbon.identity.core.util.IdentityUtil.getServerURL" %>
<%@ page import="static org.wso2.carbon.utils.multitenancy.MultitenantConstants.TENANT_AWARE_URL_PREFIX"%>
<%@ page import="static org.wso2.carbon.utils.multitenancy.MultitenantConstants.SUPER_TENANT_DOMAIN_NAME"%>

<jsp:scriptlet>
    session.setAttribute("authCode",request.getParameter("code"));
    session.setAttribute("sessionState", request.getParameter("session_state"));
</jsp:scriptlet>

<!DOCTYPE html>
<html>
    <head>
        <%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
        <meta charset="utf-8"/>
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no"/>
        <meta name="referrer" content="no-referrer" />

        <link href="/myaccount/libs/themes/default/theme.min.css" rel="stylesheet" type="text/css"/>

        <title>My Account | WSO2 Identity Server</title>

        <script>
            var serverOriginGlobal = "<%=getServerURL("", true, true)%>";
            var superTenantGlobal = "<%=SUPER_TENANT_DOMAIN_NAME%>";
            var tenantPrefixGlobal = "<%=TENANT_AWARE_URL_PREFIX%>";
        </script>
    </head>
    <body>
        <noscript>
            You need to enable JavaScript to run this app.
        </noscript>
        <iframe
            id="rpIFrame"
            style="display: block"
            src="/myaccount/rpIFrame.html"
            frameborder="0"
            width="0"
            height="0"
        >
        </iframe>
        <div id="root"></div>
    <script type="text/javascript" src="/myaccount/static/js/runtime-init.fef310d6.js?91950b1efa290504a85b"></script><script type="text/javascript" src="/myaccount/static/js/vendors~init~main~rpIFrame.beddffeb.chunk.js?91950b1efa290504a85b"></script><script type="text/javascript" src="/myaccount/static/js/vendors~init.bf44e185.chunk.js?91950b1efa290504a85b"></script><script type="text/javascript" src="/myaccount/static/js/init.2184c34f.chunk.js?91950b1efa290504a85b"></script><script type="text/javascript" src="/myaccount/static/js/runtime-main.4fb773c1.js?91950b1efa290504a85b"></script><script type="text/javascript" src="/myaccount/static/js/vendors~main.4c903b89.chunk.js?91950b1efa290504a85b"></script><script type="text/javascript" src="/myaccount/static/js/main.8b8134e6.chunk.js?91950b1efa290504a85b"></script><script type="text/javascript" src="/myaccount/static/js/runtime-rpIFrame.9b819b1b.js?91950b1efa290504a85b"></script></body>
</html>
