<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <link rel="icon" href="data:;base64,iVBORw0KGgo=">
</head>
<body>
<h2>Uploading a File</h2>
<form method="POST" action="/uploadFile" enctype="multipart/form-data">
    <table>
        <tr>
            <td><label path="file">Select a file to upload: </label></td>
            <td><input type="file" name="file" /></td>
        </tr>
        <tr>
            <td><input type="submit" value="Submit" /></td>
        </tr>
    </table>
</form>
<h2>Uploading Multiple Files</h2>
<form method="POST" action="/uploadMultiFile" enctype="multipart/form-data">
    <table>
        <tr>
            <td>Select a file to upload: </td>
            <td><input type="file" name="files" /></td>
        </tr>
        <tr>
            <td>Select a file to upload: </td>
            <td><input type="file" name="files" /></td>
        </tr>
        <tr>
            <td>Select a file to upload: </td>
            <td><input type="file" name="files" /></td>
        </tr>
        <tr>
            <td><input type="submit" value="Submit" /></td>
        </tr>
    </table>
</form>
<h2>Uploading Files With Additional Form Data</h2>
<form method="POST" action="/uploadFileWithAdditionalData" enctype="multipart/form-data">
    <table>
        <tr>
            <td>Name: </td>
            <td><input type="text" name="name" /></td>
        </tr>
        <tr>
            <td>Email: </td>
            <td><input type="text" name="email" /></td>
        </tr>
        <tr>
            <td>Select a file to upload: </td>
            <td><input type="file" name="file" /></td>
        </tr>
        <tr>
            <td><input type="submit" value="Submit" /></td>
        </tr>
    </table>
</form>
</body>
</html>

