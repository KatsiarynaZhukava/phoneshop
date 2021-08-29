<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ attribute name="fieldName" required="true" %>
<%@ attribute name="labelValue" required="true" %>

<div class="form-group">
    <div class="row">
        <label for="${fieldName}">${labelValue}</label>
        <form:input path="${fieldName}" autocomplete="off"  class="form-control form-field "/>
    </div>
    <div class="row">
        <form:errors path="${fieldName}" class="message form-field error-field"/>
    </div>
</div>
