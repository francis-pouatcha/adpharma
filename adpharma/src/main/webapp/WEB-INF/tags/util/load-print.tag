<jsp:root xmlns:c="http://java.sun.com/jsp/jstl/core" xmlns:fn="http://java.sun.com/jsp/jstl/functions" xmlns:jsp="http://java.sun.com/JSP/Page" xmlns:spring="http://www.springframework.org/tags" version="2.0">

  <jsp:output omit-xml-declaration="yes" />

   <spring:url value="/resources/js/print.js" var="printjs_url" />

  <script src="${printjs_url}" type="text/javascript"><!-- required for FF3 and Opera --></script>

</jsp:root>