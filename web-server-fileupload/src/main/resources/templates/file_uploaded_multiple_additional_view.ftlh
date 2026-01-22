<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <link rel="icon" href="data:;base64,iVBORw0KGgo=">
</head>
<body>
<h2>Submitted Files with Additional Form Data</h2>
<table>
    <tr>
        <td>Name:</td>
        <td>${name}</td>
    </tr>
    <tr>
        <td>Email:</td>
        <td>${email}</td>
    </tr>
</table>
<hr>
<h3>Files</h3>
<table>
    <#list files as file>
        <tr>
            <td>OriginalFileName:</td>
            <td>${file.originalFilename}</td>
        </tr>
        <tr>
            <td>Type:</td>
            <td>${file.contentType}</td>
        </tr>
        <tr>
            <td>Size:</td>
            <td>${file.size}</td>
        </tr>
        <tr><td>&nbsp;</td><td></td></tr>
    </#list>
</table>
</body>
</html>
