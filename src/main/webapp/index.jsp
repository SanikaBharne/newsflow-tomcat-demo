<%@ page import="com.vit.newsflow.*" %>
<!DOCTYPE html>
<html>
<head>
    <title>Newsflow CI/CD Demo</title>
</head>
<body>
    <h1>News Publishing Pipeline Successful!</h1>
    <h2>Application deployed on Apache Tomcat.</h2>
<%
    Article article = new Article(
        "Council approves metro line", "council-approves-metro", "Asha Rao",
        "The city council approved the new metro line on Monday after months of "
        + "debate, promising faster commutes and lower emissions for residents "
        + "across the western suburbs.",
        "https://example.com/metro.jpg", "Metro train at a platform");

    NewsWorkflowService workflow = new NewsWorkflowService();
    workflow.submit(article);
    workflow.approve(article, "Editor Meera");
    out.println(workflow.publish(article));
%>
    <hr>
    <p>Build Tool: Maven</p>
    <p>CI/CD Tool: Jenkins</p>
    <p>Application Server: Apache Tomcat</p>
</body>
</html>
