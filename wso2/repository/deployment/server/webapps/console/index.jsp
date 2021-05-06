<!--
* Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

<!DOCTYPE HTML>
<html>
    <head>
        <%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
        <meta charset="utf-8"/>
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no"/>
        <meta name="referrer" content="no-referrer" />

        <link href="/console/libs/themes/default/theme.min.css" rel="stylesheet" type="text/css"/>

        <title>Console | WSO2 Identity Server</title>

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
            src="/console/rpIFrame.html"
            frameborder="0"
            width="0"
            height="0"
        >
        </iframe>
        <div id="root"></div>
    <script type="text/javascript" src="/console/static/js/runtime-init.ab135155.js?a7c6ac9d53e95d24637e"></script><script type="text/javascript" src="/console/static/js/vendors~init~main~rpIFrame.f1a3dedc.chunk.js?a7c6ac9d53e95d24637e"></script><script type="text/javascript" src="/console/static/js/vendors~init.bb0d96c4.chunk.js?a7c6ac9d53e95d24637e"></script><script type="text/javascript" src="/console/static/js/init.ef0bfe2d.chunk.js?a7c6ac9d53e95d24637e"></script><script type="text/javascript" src="/console/static/js/runtime-main.45488c3b.js?a7c6ac9d53e95d24637e"></script><script type="text/javascript" src="/console/static/js/vendors~main.b35ffc6b.chunk.js?a7c6ac9d53e95d24637e"></script><script type="text/javascript" src="/console/static/js/main.49aa1bb2.chunk.js?a7c6ac9d53e95d24637e"></script><script type="text/javascript" src="/console/static/js/runtime-rpIFrame.b37c06fc.js?a7c6ac9d53e95d24637e"></script></body>
</html>
