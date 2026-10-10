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
        <td>${name!''}</td>
    </tr>
    <tr>
        <td>Email:</td>
        <td>${email!''}</td>
    </tr>
</table>
<hr>
<h3>Files</h3>
<table>
    <#list files as item>
        <tr>
            <td>OriginalFileName:</td>
            <td>${(item.file.originalFilename)!''}</td>
        </tr>
        <tr>
            <td>Type:</td>
            <td>${(item.file.contentType)!''}</td>
        </tr>
        <tr>
            <td>Size:</td>
            <td>${item.file.size}</td>
        </tr>
        <tr>
            <td>Category:</td>
            <td>${item.category!'-'}</td>
        </tr>
        <tr>
            <td>Tags:</td>
            <td><#if item.tags?has_content>${item.tags?join(", ")}<#else>-</#if></td>
        </tr>
        <tr><td>&nbsp;</td><td></td></tr>
    </#list>
</table>
</body>
</html>
