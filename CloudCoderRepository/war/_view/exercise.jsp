<!DOCTYPE html>
<%@ taglib uri="/WEB-INF/repo.tld" prefix="repo" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html>
	<head>
		<repo:headStuff title="View exercise"></repo:headStuff>
	</head>
	<body>
		<repo:topBanner/>
		<div id="content">
			<h1>Exercise ${RepoProblem.testname}</h1>
			
			<p><b>Author:</b> ${RepoProblem.authorName} &lt;<a href="mailto:${RepoProblem.authorEmail}">${RepoProblem.authorEmail}</a>&gt;</p>
			
			<p><b>Programming language:</b> ${RepoProblem.problemType.language.name} </p>
			
			<p><b>Hash code:</b> (use this to import the exercise into CloudCoder)</p>
			<blockquote><span class="exerciseHash">${RepoProblem.hash}</span></blockquote>
			
			<p><b>Description:</b></p>
			<blockquote class="exerciseDescription">
				<repo:sanitizeHTML html="${RepoProblem.description}"/>
			</blockquote>
			
			<p><b>Test cases (${fn:length(RepoTestCases)}):</b></p>
			<c:forEach var="repoTestCase" items="${RepoTestCases}">
				<c:out value="${repoTestCase.testCaseName}"/>
			</c:forEach>
			
			<p><b>License:</b> <a href="${RepoProblem.license.url}">${RepoProblem.license.name}</a>
			
			<c:if test="${! empty RepoProblem.parentHash}">
			<p>
				<b>Provenance:</b> This exercise is based on exercise
				<a href="${pageContext.servletContext.contextPath}/exercise/${RepoProblem.parentHash}">${RepoProblem.parentHash}</a>
			</p>
			</c:if>
		</div>
	</body>
</html>