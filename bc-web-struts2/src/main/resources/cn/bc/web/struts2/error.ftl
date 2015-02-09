<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>异常</title>
    <style>pre {margin: 0; padding: 0}</style>
</head>
<body>
<h2>Struts Problem Report</h2>
<p>Struts has detected an unhandled exception:</p>
<div id="exception-info">
    <table>
        <tbody>
        <tr>
            <td><strong>Messages</strong>:</td>
            <td>
                <li><@s.property value="exception.message"/></li>
            </td>
        </tr>
        </tbody>
    </table>
</div>
<div id="stacktraces">
    <hr>
    <h3>Stacktraces</h3>
    <div class="stacktrace" style="padding-left: 0em">
            <pre><@s.property value="exceptionStack"/></pre>
    </div>
</div>
</body>
</html>