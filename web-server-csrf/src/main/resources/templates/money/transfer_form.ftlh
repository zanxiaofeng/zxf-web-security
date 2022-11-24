<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <link rel="icon" href="data:;base64,iVBORw0KGgo=">
    <title>CSRF test form on Origin(Money Transfer)</title>
</head>
<body>
<h1>CSRF test form on Origin(Money Transfer)</h1>
    <form method="post" action="/money/transfer" target="_blank">
        Source Account: <input type="text" name="sourceAccount" value="1234567">
        <br>
        Target Account: <input type="text" name="targetAccount" value="3214455">
        <br>
        Transfer Amount: <input type="text" name="amount" value="1000">
        <br>
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
        <input type="submit" value="Transfer">
    </form>
</body>
</html>