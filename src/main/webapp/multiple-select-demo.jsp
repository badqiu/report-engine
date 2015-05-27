<head>
	<link href="css/bootstrap.min.css" rel="stylesheet" />
    <link href="js/multiple-select/multiple-select.css" rel="stylesheet"/>
    <script src="js/jquery.min.js"></script>
    <script src="js/multiple-select/jquery.multiple.select.js"></script>
</head>
<body>
    <select multiple="multiple">
        <option value="1">abc</option>
        <option value="20">diy</option>
        <option value="31">789</option>
    </select>
    <script>
        $("select").multipleSelect({
            filter: true
        });
    </script>
</body>