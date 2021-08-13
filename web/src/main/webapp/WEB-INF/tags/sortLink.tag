<%@ tag trimDirectiveWhitespaces="true" %>
<%@ attribute name="sort" required="true" %>
<%@ attribute name="order" required="true" %>

<a href="?sortField=${sort}&sortOrder=${order}&searchQuery=${param.searchQuery}">${order == "asc" ? "&uarr;" : "&darr;"}</a>