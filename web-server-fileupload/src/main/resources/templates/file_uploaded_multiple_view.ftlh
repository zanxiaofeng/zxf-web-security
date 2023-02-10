<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <link rel="icon" href="data:;base64,iVBORw0KGgo=">
</head>
<body>
<h2>Submitted Files</h2>
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
    </#list>
</table>
</body>
</html>


