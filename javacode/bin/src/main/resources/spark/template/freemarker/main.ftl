<!DOCTYPE html>
<html>

  <head>

    <title>${title}</title>
	<link rel="stylesheet" href="css/normalize.css">
	<link rel="stylesheet" href="css/main.css">
	<link rel="stylesheet" href="css/html5bp.css">
	
  </head>

  <body>
  	
    ${content}
    <#if message??>
  	${message}
  	</#if>
  	
  </body>
      <script src="js/jquery-2.1.1.js"></script>
	  <script src="js/canvas.js"></script>
</html>