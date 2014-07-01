<%@page import="java.io.File"%>
<%@page import="org.jboss.eventmonitor.RuleManagementBean"%>
<%@page contentType="text/html"%>
<html>
<head>
   <title>Event Monitor - Rules</title>
   <!--link rel="stylesheet" href="style_master.css" type="text/css"-->
   <meta http-equiv="cache-control" content="no-cache">
<style>
table thead tr td {font-variant:small-caps;}
</style>
</head>

<%
RuleManagementBean bean=new RuleManagementBean();
%>

<div>
  <h3>Rules</h3>
  <table border=0 style="width:70%; border: black 1px solid;">
    <thead style="background:#eeeeee;">
      <tr>
        <td></td>
      </tr>
    </thead>
    <tbody>
      <tr>
        <td>
          <textarea name="rules" style="width:100%;height:400px;"><%=bean.readRules()%></textarea>
          <input type="submit" value="Save" onclick=""/>
        </td>
      </tr>
    </tbody>
  </table>
</div>


